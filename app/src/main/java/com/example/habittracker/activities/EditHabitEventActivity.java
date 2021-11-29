package com.example.habittracker.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;

import com.example.habittracker.DownloadImageTask;
import com.example.habittracker.fragments.MapsFragment;
import com.example.habittracker.R;
import com.example.habittracker.models.Habit.Habit;
import com.example.habittracker.models.HabitEvent.HabitEvent;
import com.example.habittracker.controllers.HabitEventsController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class EditHabitEventActivity extends AppCompatActivity {
    private ImageView addHabitEvent_back_icon;
    private Habit habit;
    private HabitEvent habitEvent;
    private String filterAddress;
    private String habitEventId;
    private SwitchCompat isCompleted;
    private EditText completedDate_editText;
    private ImageButton deletePhotoBtn;
    private ImageButton addPhotoBtn_camera;
    private ImageButton addPhotoBtn_album;
    private ImageView photoAdded;
    private boolean isphotoEnlarged = false;
    private EditText addComment;
    private Button submitBtn;
    public static final int TAKE_CAMERA = 101;
    public static final int PICK_PHOTO = 102;
    private Uri uri = null;
    private Uri imageUri = null;
    private long imageStorageNamePrefix;
    private Bitmap imageBitmap;
    private ImageButton addLocationBtn;
    private EditText addLocation_editText;
    private String storageImagePath = "";

    TextView activeDaysText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_habit_event);

        Intent intent = getIntent();

        // Must pass the Habit through the intent!
        // reference the following link if unsure on how to do this:
        // https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
        habitEvent = (HabitEvent) intent.getSerializableExtra("HabitEvent");
        habit = habitEvent.getHabit();
        filterAddress = (String) intent.getSerializableExtra("Location");

        // View the related habit
        TextView titleText = findViewById(R.id.viewHabitTitle_habitEvent);
        //TextView reasonText = findViewById(R.id.viewHabitReason_habitEvent);
        TextView startDateText = findViewById(R.id.viewHabitDateText_habitEvent);
        activeDaysText = findViewById(R.id.viewActiveDaysText_habitEvent);

        titleText.setText(habit.getTitle());
        //reasonText.setText(habit.getReason());
        startDateText.setText(getDateText(habit.getDateCreated()));
        activeDaysText.setText(getDaysText(habit.getFrequency()));

        addHabitEvent_back_icon = findViewById(R.id.addHabitEvent_back_icon);
        isCompleted = findViewById(R.id.isHabitCompleted);
        completedDate_editText = findViewById(R.id.completedDate_editText);
        deletePhotoBtn = findViewById(R.id.deletePhotoBtn);
        addPhotoBtn_camera = findViewById(R.id.addPhotoBtn_fromCamera);
        addPhotoBtn_album = findViewById(R.id.addPhotoBtn_fromAlbum);
        photoAdded = findViewById(R.id.HabitImageView1);
        addLocationBtn = findViewById(R.id.addLocationBtn);
        addLocation_editText = findViewById(R.id.addLocation_editText);
        addComment = findViewById(R.id.addComment_editText);
        submitBtn = findViewById(R.id.addHabitEventSubmitBtn);

        setAttributes();

        addHabitEvent_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addHabitEvent_back_icon.setAlpha(0.5f);
                onSupportNavigateUp();
            }
        });

        // use photo through the camera
        addPhotoBtn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraBtnOnClick();
            }
        });

        addPhotoBtn_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                albumBtnOnClick();
            }
        });

        deletePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePhoto();
            }
        });

        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationBtnOnClick();
            }
        });

        photoAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isphotoEnlarged) {
                    isphotoEnlarged = enlargePhoto();
                }else{
                    isphotoEnlarged = !narrowPhoto();
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                imageUri = uri;
                Log.i("IN ONCLICK", String.valueOf(imageUri));
                if (imageUri != null) {
                    boolean success = uploadImage(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    if (!success){
                        Toast.makeText(EditHabitEventActivity.this,"Failed to upload image",Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    uploadHabitEvent();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void uploadHabitEvent() {
        FirebaseUser user;
        String uid;
        habitEventId = UUID.randomUUID().toString();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            uid = user.getUid();
        } else {
            Toast.makeText(EditHabitEventActivity.this,"Failed to retrieve userId",Toast.LENGTH_SHORT).show();
            return;
        }

        habitEvent.setImageStorageNamePrefix(storageImagePath);
        habitEvent.setLocation(addLocation_editText.getText().toString());
        habitEvent.setComment(addComment.getText().toString());
        habitEvent.setCompletedDate(habitEvent.getCompletedDate().plusDays(1));
        habitEvent.setCreateDate(habitEvent.getCreateDate().plusDays(1));

        HabitEventsController.getInstance().saveHabitEvent(habitEvent);

        // Navigate back to main activity without changing it's state
        Intent gotoScreenVar = new Intent(this, MainActivity.class);
        gotoScreenVar.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(gotoScreenVar);
    }

    public void setAttributes() {
        isCompleted.setChecked(habitEvent.isCompleted());

        if (habitEvent.getImageStorageNamePrefix().length() > 0) {
            new DownloadImageTask(photoAdded).execute(habitEvent.getImageStorageNamePrefix());
            storageImagePath = habitEvent.getImageStorageNamePrefix();
        }

        if (habitEvent.getCompletedDate() != null) {
            completedDate_editText.setText(getDateText(habitEvent.getCompletedDate()));
        }

        if (habitEvent.getLocation().length() > 0) {
            addLocation_editText.setText(habitEvent.getLocation());
        }

        if (habitEvent.getComment().length() > 0) {
            addComment.setText(habitEvent.getComment());
        }
    }

    /**
     * Get the date in string format of yyyy-dd-mm
     * @param date the date to process
     * @return the formatted string
     */
    public String getDateText(Date date) {
        // Add 1900 to year, as the getYear function returns year - 1900
        return (date.getYear() + 1900) + "-" +
                (date.getMonth() + 1) + "-" + date.getDate();
    }

    /**
     * Get the date in string format of yyyy-dd-mm
     * @param date the LocalDate to process
     * @return the formatted string
     */
    public String getDateText(LocalDate date) {
        // Add 1900 to year, as the getYear function returns year - 1900
        return date.getYear() + "-" +
                date.getMonthValue() + "-" + date.getDayOfMonth();
    }

    /**
     * Get the string of active days
     * @param frequency the frequency list from habit class
     * @return the formatted string
     */
    public String getDaysText(ArrayList<Integer> frequency) {
        String out = "";

        // map days of week to frequency list
        String[] mapping = new String[] {"MON", "TUE", "WED", "THU",
                "FRI", "SAT", "SUN"};

        for (int i = 0; i < 7; i++) {
            // Was getting error when comparing integer values, cast to String as a work around
            boolean check = String.valueOf(frequency.get(i)).equals("1");

            if (check && out.length() == 0) {
                // If this is the first day we are adding, don't want comma in front
                out += mapping[i];
            } else if (check) {
                out += ", " + mapping[i];
            }
        }

        return out.length() == 0 ? "No active days selected" : out;
    }



    public void cameraBtnOnClick() {
        // create File object to store the output photo in app cache of the CD card
        File outputImage;
        try {
            outputImage = getImageFile();
        } catch (Exception e) {
            outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        }

        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // use FileProvider to provide uri to improve the security
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // system # >= 7.0
            uri = FileProvider.getUriForFile(EditHabitEventActivity.this, "com.example.habittracker.fileprovider", outputImage);
        } else {
            // system # < 7.0
            uri = Uri.fromFile(outputImage);
        }

        uri = FileProvider.getUriForFile(EditHabitEventActivity.this, "com.example.habittracker.fileprovider", outputImage);

        // start the camera program
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        EditHabitEventActivity.this.startActivityForResult(intent, TAKE_CAMERA);
    }

    private File getImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = FirebaseAuth.getInstance().getCurrentUser().getUid() + "_" + timestamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        return imageFile;
    }

    public void albumBtnOnClick(){
        // dynamically apply permission of read/write the disk
        if (ContextCompat.checkSelfPermission(EditHabitEventActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditHabitEventActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        } else {
            // open the album
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            // Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
            intent.setType("image/*");
            EditHabitEventActivity.this.startActivityForResult(intent, PICK_PHOTO); // 打开相册
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // uri is type of document，process through document id
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                // reveal digital id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // uri is type of content，process through regular method
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // uri is type of file, obtain the path of file directly
            imagePath = uri.getPath();
        }
        // 根据图片路径显示图片
        displayImage(imagePath);
    }

    /**
     * android process way before 4.4
     * @param data
     */
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // obtain real path of photo from Uri and selection
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig= Bitmap.Config.RGB_565;
            imageBitmap = BitmapFactory.decodeFile(imagePath,options);
            photoAdded.setImageBitmap(imageBitmap);
            photoAdded.setBackgroundResource(R.color.trans);
        } else {
            Toast.makeText(this, "Failed to add image", Toast.LENGTH_SHORT).show();
        }
    }

    public void deletePhoto(){
        Drawable d = ContextCompat.getDrawable(EditHabitEventActivity.this, R.drawable.ic_baseline_photo_filter_24);
        d.setColorFilter(0x89000000, PorterDuff.Mode.MULTIPLY);
        photoAdded.setImageDrawable(d);
        photoAdded.setBackgroundResource(R.color.grey);
        imageUri = null;
        storageImagePath = "";
    }

    public boolean enlargePhoto(){

        // set size with original aspect ratio
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        photoAdded.setLayoutParams(params);

        return true;
    }

    public boolean narrowPhoto(){
        // the layout height for photo in the layout file
        int dpValue = 100;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (dpValue * getResources().getDisplayMetrics().density + 0.5f));
        photoAdded.setLayoutParams(params);
        return true;
    }

    public void locationBtnOnClick(){
        MapsFragment mapsFragment = new MapsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.FL1_addHabitEventL1, mapsFragment).addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_CAMERA:
                if (resultCode == RESULT_OK) {
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig= Bitmap.Config.RGB_565;
                        imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri),null,options);
                        photoAdded.setImageBitmap(imageBitmap);
                        photoAdded.setBackgroundResource(R.color.trans);
                        if (data != null) {
                            imageUri = data.getData();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PICK_PHOTO:
                if (resultCode == RESULT_OK) { // tell system # of the phone
                    if (Build.VERSION.SDK_INT >= 19) {
                        // process image when system # >= 4.4
                        handleImageOnKitKat(data);
                    } else {
                        // process image when system # < 4.4
                        handleImageBeforeKitKat(data);
                    }
                    imageUri = data.getData();
                }
                break;
            default:
                break;
        }
    }

    public boolean errorCheck(SwitchCompat isCompleted, EditText completedDate_editText){
        boolean completedDate_editTextError = false;
        boolean isCompleted_error = false;

        if (!isCompleted.isChecked()) {
            isCompleted.setError("Please Denote as Completed");
            isCompleted_error = true;
        }

        if (completedDate_editText.getText().toString().length() == 0){
            completedDate_editText.setError("Date is required");
            completedDate_editTextError = true;
        }

        if (isCompleted_error || completedDate_editTextError) {
            return true;
        }

        if (isCompleted.isChecked()){
            String datePattern = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$";
            String s = completedDate_editText.getText().toString();

            if (!completedDate_editText.getText().toString().matches(datePattern)){
                completedDate_editText.setError("Unacceptable date format");
                completedDate_editTextError = true;
            } else {
                // check if the day matches the planned days
                Date date = null;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(completedDate_editText.getText().toString());
                } catch (ParseException e) { }

                int day = date.getDay();
                if (day == 0){
                    day = 7;
                }

                boolean check = false;
                boolean isPlanned = false;
                boolean isAfter = date.getTime() >= habit.getDateCreated().getTime();
                for (int i = 0; i < 7; i++) {
                    check = String.valueOf(habit.getFrequency().get(i)).equals("1");
                    if (check && day == (i+1)) {
                        isPlanned = true;
                        break;
                    }
                }

                if (!isPlanned) {
                    completedDate_editText.setError("Date is not within \"" + activeDaysText.getText().toString() + "\"");
                    completedDate_editTextError = true;
                }

                if (!isAfter) {
                    completedDate_editText.setError("Date should be after started date");
                    completedDate_editTextError = true;
                }
            }

        }

        return completedDate_editTextError;
    }

    public String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public boolean uploadImage(String uid){
        if (imageUri != null){
            imageStorageNamePrefix = System.currentTimeMillis();
            if (!getFileExtension(imageUri).equals("jpg")){
                return false;
            }
            StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("HabitEventImages_" + uid)
                    .child(imageStorageNamePrefix + "." + getFileExtension(imageUri));
            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Log.d("DOWNLOADURL", url);
                            storageImagePath = url;
                            Toast.makeText(EditHabitEventActivity.this, "Image upload successfully", Toast.LENGTH_SHORT).show();
                            uploadHabitEvent();
                        }
                    });
                }
            });
            return true;
        }
        return false;
    }

    /**
     * rewrite the back button on the action bar to make its functionality works better
     * @return false to return to the activity before
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}

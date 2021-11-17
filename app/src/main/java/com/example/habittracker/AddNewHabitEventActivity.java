package com.example.habittracker;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;

import com.example.habittracker.classes.Habit;
import com.example.habittracker.classes.HabitEvent;
import com.example.habittracker.controllers.HabitEventController;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class AddNewHabitEventActivity extends AppCompatActivity {
    private Habit habit;
    private String filterAddress;
    private String habitEventId;
    private ImageButton addPhotoBtn_camera;
    private ImageButton addPhotoBtn_album;
    private ImageView photoAdded;
    public static final int TAKE_CAMERA = 101;
    public static final int PICK_PHOTO = 102;
    private Uri uri;
    private Uri imageUri = null;
    private ImageButton addLocationBtn;
    private EditText addLocation_editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_habit_event);

        Intent intent = getIntent();

        // Must pass the Habit through the intent!
        // reference the following link if unsure on how to do this:
        // https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
        habit = (Habit) intent.getSerializableExtra("Habit");
        filterAddress = (String) intent.getSerializableExtra("Location");

        TextView titleText = findViewById(R.id.viewHabitTitle_habitEvent);
        TextView reasonText = findViewById(R.id.viewHabitReason_habitEvent);
        TextView startDateText = findViewById(R.id.viewHabitDateText_habitEvent);
        TextView activeDaysText = findViewById(R.id.viewActiveDaysText_habitEvent);

        titleText.setText(habit.getTitle());
        reasonText.setText(habit.getReason());
        startDateText.setText(getDateText(habit.getDateCreated()));
        activeDaysText.setText(getDaysText(habit.getFrequency()));


        Switch isCompleted = findViewById(R.id.isHabitCompleted);
        addPhotoBtn_camera = findViewById(R.id.addPhotoBtn_fromCamera);
        addPhotoBtn_album = findViewById(R.id.addPhotoBtn_fromAlbum);
        photoAdded = findViewById(R.id.HabitImageView1);
        addLocationBtn = findViewById(R.id.addLocationBtn);
        addLocation_editText = findViewById(R.id.addLocation_editText);
        EditText addComment = findViewById(R.id.addComment_editText);
        Button submitBtn = findViewById(R.id.addHabitEventSubmitBtn);

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

        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationBtnOnClick();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user;
                String uid;
                String imageUri_String = "";
                habitEventId = UUID.randomUUID().toString();

                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in
                    uid = user.getUid();
                } else {
                    Toast.makeText(AddNewHabitEventActivity.this,"Failed to retrieve userId",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (imageUri != null) {
                    boolean success = uploadImage(uid);
                    if (!success){
                        Toast.makeText(AddNewHabitEventActivity.this,"Failed to upload image",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    imageUri_String = imageUri.toString();
                }

                HabitEvent habitEvent = new HabitEvent(
                        habitEventId,
                        uid,
                        isCompleted.isChecked(),
                        imageUri_String,
                        addLocation_editText.getText().toString(),
                        addComment.getText().toString()
                );

                HabitEventController habitEventController = new HabitEventController();
                habitEventController.saveHabitEvent(habitEvent);

                finish();
            }
        });
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

    public void cameraBtnOnClick(){
        // create File object to store the output photo in app cache of the CD card
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");

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
            uri = FileProvider.getUriForFile(AddNewHabitEventActivity.this, "com.example.habittracker.fileprovider", outputImage);
        } else {
            // system # < 7.0
            uri = Uri.fromFile(outputImage);
        }

        // start the camera program
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        AddNewHabitEventActivity.this.startActivityForResult(intent, TAKE_CAMERA);
    }

    public void albumBtnOnClick(){
        // dynamically apply permission of read/write the disk
        if (ContextCompat.checkSelfPermission(AddNewHabitEventActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddNewHabitEventActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        } else {
            // open the album
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            // Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
            intent.setType("image/*");
            AddNewHabitEventActivity.this.startActivityForResult(intent, PICK_PHOTO); // 打开相册
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
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            photoAdded.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "Failed to add image", Toast.LENGTH_SHORT).show();
        }
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
                        // display the photo
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        photoAdded.setImageBitmap(bitmap);
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

    public String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public boolean uploadImage(String uid){
        if (imageUri != null){
            StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("HabitEventImages_" + uid)
                    .child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Log.d("DownloadUrl", url);
                            Toast.makeText(AddNewHabitEventActivity.this, "Image upload successfully", Toast.LENGTH_SHORT).show();
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

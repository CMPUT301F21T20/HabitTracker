package com.example.habittracker.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.habittracker.R;
import com.example.habittracker.classes.HabitEvent;
import com.example.habittracker.classes.HabitEventList;
import com.example.habittracker.controllers.HabitEventController;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

public class HabitEventListAdapter extends ArrayAdapter<HabitEvent> {

    private FirebaseUser user;

    private View view;
    private Context context;
    private HabitEventList habitEventList;
    private HabitEvent habitEvent;
    private String username;

    private TextView habitEventUsername_text;
    private TextView habitEventComment_text;
    private ImageView habitEventImage;
    private TextView recordDateDescription;
    private TextView recordDateHabitEvent;
    private TextView completedDateDescription;
    private TextView completedDateHabitEvent;
    private TextView habitEventLocation_text;
    private ImageView isHabitCompletedImage;
    private ImageView habitEventDelete_btn;

    public HabitEventListAdapter(@NonNull Context context, HabitEventList habitEventList, String username) {
        super(context, 0, habitEventList.getHabitEventList());
        this.context = context;
        this.habitEventList = habitEventList;
        this.username = username;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_habit_event, parent, false);
        }

        habitEvent = habitEventList.get(position);

        habitEventUsername_text = view.findViewById(R.id.habitEventUsername_text);
        habitEventComment_text = view.findViewById(R.id.habitEventComment_text);
        habitEventImage = view.findViewById(R.id.habitEventImage);
        recordDateDescription = view.findViewById(R.id.recordDateDescription);
        recordDateHabitEvent = view.findViewById(R.id.recordDateHabitEvent);
        completedDateDescription = view.findViewById(R.id.completedDateDescription);
        completedDateHabitEvent = view.findViewById(R.id.completedDateHabitEvent);
        habitEventLocation_text = view.findViewById(R.id.habitEventLocation_text);
        isHabitCompletedImage = view.findViewById(R.id.isHabitCompletedImage);
        habitEventDelete_btn = view.findViewById(R.id.habitEventDelete_btn);

        habitEventUsername_text.setVisibility(View.VISIBLE);
        habitEventComment_text.setVisibility(View.VISIBLE);
        habitEventImage.setVisibility(View.VISIBLE);
        recordDateDescription.setVisibility(View.VISIBLE);
        recordDateHabitEvent.setVisibility(View.VISIBLE);
        completedDateDescription.setVisibility(View.VISIBLE);
        completedDateHabitEvent.setVisibility(View.VISIBLE);
        habitEventLocation_text.setVisibility(View.VISIBLE);
        isHabitCompletedImage.setVisibility(View.VISIBLE);
        habitEventDelete_btn.setVisibility(View.VISIBLE);

        habitEventDelete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteHabitEvent(habitEvent);
            }
        });

        if (habitEvent.getCreateDate() == null){
            return view;
        }

        if (!username.equals("")) {
            habitEventUsername_text.setText(username);
        }

        if (!habitEvent.getComment().equals("")) {
            habitEventComment_text.setText(habitEvent.getComment());
        }else{
            habitEventComment_text.setVisibility(View.GONE);
        }

        recordDateHabitEvent.setText(getDateText(habitEvent.getCreateDate()));

        if (habitEvent.getCompletedDate() != null) {
            completedDateHabitEvent.setText(getDateText(habitEvent.getCompletedDate()));
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_check_circle_outline_24);
            isHabitCompletedImage.setImageDrawable(drawable);
        }else{
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_pause_circle_outline_24);
            isHabitCompletedImage.setImageDrawable(drawable);
            completedDateDescription.setVisibility(View.GONE);
            completedDateHabitEvent.setVisibility(View.GONE);
        }

        if (habitEvent.getLocation().length() != 0) {
            habitEventLocation_text.setText(habitEvent.getLocation());
        }else{
            habitEventLocation_text.setVisibility(View.GONE);
        }

        if (habitEvent.getImageStorageNamePrefix().length() != 0){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            String uid;
            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // User is signed in
                uid = user.getUid();
            } else {
                Toast.makeText(context,"Failed to retrieve userId",Toast.LENGTH_SHORT).show();
                return view;
            }
            StorageReference photoReference = storageReference.child("HabitEventImages_" + uid + "/" + habitEvent.getImageStorageNamePrefix() + ".jpg");

            final long ONE_MEGABYTE = 1024 * 1024;

            photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    habitEventImage.setImageBitmap(bmp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(context, "No Such file or Path found!!", Toast.LENGTH_LONG).show();
                }
            });
        }else{
            habitEventImage.setVisibility(View.GONE);
        }

        return view;
    }

    private void deleteHabitEvent(HabitEvent habitEvent){
        HabitEventController habitEventController = new HabitEventController();
        habitEventController.deleteHabitEvent(habitEvent);
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
}

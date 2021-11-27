package com.example.habittracker.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.habittracker.R;
import com.example.habittracker.classes.DownloadImageTask;
import com.example.habittracker.classes.HabitEvent;
import com.example.habittracker.classes.HabitEventList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;

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

    public HabitEventListAdapter(@NonNull Context context, HabitEventList habitEventList, String username) {
        super(context, 0, habitEventList.getHabitEventList());
        this.context = context;
        this.habitEventList = habitEventList;
        this.username = username;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        habitEventUsername_text.setVisibility(View.VISIBLE);
        habitEventComment_text.setVisibility(View.VISIBLE);
        habitEventImage.setVisibility(View.VISIBLE);
        recordDateDescription.setVisibility(View.VISIBLE);
        recordDateHabitEvent.setVisibility(View.VISIBLE);
        completedDateDescription.setVisibility(View.VISIBLE);
        completedDateHabitEvent.setVisibility(View.VISIBLE);
        habitEventLocation_text.setVisibility(View.VISIBLE);
        isHabitCompletedImage.setVisibility(View.VISIBLE);

        habitEventUsername_text.setText(username);

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
            completedDateHabitEvent.setVisibility(View.GONE);
            completedDateDescription.setVisibility(View.GONE);
        }

        if (habitEvent.getLocation().length() != 0) {
            habitEventLocation_text.setText(habitEvent.getLocation());
        }else{
            habitEventLocation_text.setVisibility(View.GONE);
        }

        if (habitEvent.getImageStorageNamePrefix().length() != 0){
            new DownloadImageTask(habitEventImage).execute(habitEvent.getImageStorageNamePrefix());
        }else{
            habitEventImage.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * Get the date in string format of yyyy-dd-mm
     * @param date the date to process
     * @return the formatted string
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDateText(LocalDate date) {
        // Add 1900 to year, as the getYear function returns year - 1900
        return (date.getYear()) + "-" +
                (date.getMonth()) + "-" + date.getDayOfMonth();
    }
}

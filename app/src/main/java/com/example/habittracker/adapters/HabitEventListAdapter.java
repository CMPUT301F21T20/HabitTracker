package com.example.habittracker.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
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
import com.example.habittracker.classes.HabitList;
import com.example.habittracker.controllers.HabitEventsController;
import com.example.habittracker.interfaces.OnHabitEventDeleted;
import com.example.habittracker.interfaces.OnHabitEventsRetrieved;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;

public class HabitEventListAdapter extends ArrayAdapter<HabitEvent> {
    private FirebaseUser user;
    private HabitEventsController habitEventsController;

    private View view;
    private Context context;
    private HabitEventList habitEventList;
    private HabitEvent habitEvent;
    private HabitList habitList;
    private String username;

    private TextView habitEventHabitTitle_text;
    private TextView habitEventComment_text;
    private ImageView habitEventImage;
    private TextView recordDateDescription;
    private TextView recordDateHabitEvent;
    private TextView completedDateDescription;
    private TextView completedDateHabitEvent;
    private TextView habitEventLocation_text;
    private ImageView isHabitCompletedImage;
    private ImageView deleteHabit;

    public HabitEventListAdapter(@NonNull Context context, HabitEventList habitEventList, String username, HabitList habitList) {
        super(context, 0, habitEventList.getHabitEventList());
        this.context = context;
        this.habitEventList = habitEventList;
        this.username = username;
        this.habitList = habitList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_habit_event, parent, false);
        }

        user = FirebaseAuth.getInstance().getCurrentUser();

        habitEvent = habitEventList.get(position);
        habitEventsController = HabitEventsController.getInstance();

        habitEventHabitTitle_text = view.findViewById(R.id.habitEventHabitTitle_text);
        habitEventComment_text = view.findViewById(R.id.habitEventComment_text);
        habitEventImage = view.findViewById(R.id.habitEventImage);
        recordDateDescription = view.findViewById(R.id.recordDateDescription);
        recordDateHabitEvent = view.findViewById(R.id.recordDateHabitEvent);
        completedDateDescription = view.findViewById(R.id.completedDateDescription);
        completedDateHabitEvent = view.findViewById(R.id.completedDateHabitEvent);
        habitEventLocation_text = view.findViewById(R.id.habitEventLocation_text);
        isHabitCompletedImage = view.findViewById(R.id.isHabitCompletedImage);
        deleteHabit = view.findViewById(R.id.deletehabitEvent);

        // If there was no habit events on a certain day display a dummy habit event that shows no habit events today
        if (habitEvent.getComment().equals("Sent By Developer -> No Habits Today")) {
            showNoHabitEventsToday();
            return view;
        }

        habitEventHabitTitle_text.setVisibility(View.VISIBLE);
        habitEventComment_text.setVisibility(View.VISIBLE);
        habitEventImage.setVisibility(View.VISIBLE);
        recordDateDescription.setVisibility(View.VISIBLE);
        recordDateHabitEvent.setVisibility(View.VISIBLE);
        completedDateDescription.setVisibility(View.VISIBLE);
        completedDateHabitEvent.setVisibility(View.VISIBLE);
        habitEventLocation_text.setVisibility(View.VISIBLE);
        isHabitCompletedImage.setVisibility(View.VISIBLE);
        deleteHabit.setVisibility(View.VISIBLE);

        habitEventHabitTitle_text.setText(habitEvent.getHabit().getTitle());

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

        deleteHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteHabit.setAlpha(0.5f);
                habitEventsController.deleteHabitEventWithCallback(habitEvent, new OnHabitEventDeleted() {
                    @Override
                    public void onHabitEventDeleted() {
                        getHabitEvents(
                            habitEvent.getCompletedDate().getYear(),
                            habitEvent.getCompletedDate().getMonthValue(),
                            habitEvent.getCompletedDate().getDayOfMonth()
                        );
                    }

                    @Override
                    public void onError(Exception taskException) {

                    }
                });
            }
        });

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

    /**
     * If there were no habits on a certain day show this by providing
     * a "dummy" habit event that say there are no habit events
     */
    public void showNoHabitEventsToday() {
        habitEventHabitTitle_text.setVisibility(View.VISIBLE);
        habitEventComment_text.setVisibility(View.GONE);
        habitEventImage.setVisibility(View.GONE);
        recordDateDescription.setVisibility(View.GONE);
        recordDateHabitEvent.setVisibility(View.GONE);
        completedDateDescription.setVisibility(View.GONE);
        completedDateHabitEvent.setVisibility(View.GONE);
        habitEventLocation_text.setVisibility(View.GONE);
        isHabitCompletedImage.setVisibility(View.GONE);
        deleteHabit.setVisibility(View.GONE);

        habitEventHabitTitle_text.setText("No Habits Today");
    }


    /**
     * Get Habit Events from Firestore given a date
     * @param year the year of the habit Event
     * @param month the month of the Habit Event
     * @param day the day of the habit Event
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getHabitEvents(int year, int month, int day) {
        HabitEventsController habitEventsController = HabitEventsController.getInstance();

        habitEventsController.loadHabitEvents(user.getUid(), day, month, year, habitList, new OnHabitEventsRetrieved() {
            @Override
            public void onHabitEventsRetrieved(HabitEventList newHabitEventList) {
                habitEventList.clear();

                // Because habits event docs are organized by month, we need to iterate through all retrieved
                // habits and check if date match today
                for (int i = 0; i < newHabitEventList.getCount(); i++) {
                    LocalDate checkDate = newHabitEventList.get(i).getCompletedDate();
                    if (checkDate.getDayOfMonth() == day) {
                        habitEventList.addHabitEvent(newHabitEventList.get(i));
                    }
                }

                // if no habit events were added add a dummy habie event to show that no habit events were added
                if (habitEventList.getCount() == 0) {
                    HabitEvent dummyHabitEvent = new HabitEvent();
                    dummyHabitEvent.setComment("Sent By Developer -> No Habits Today");
                    habitEventList.addHabitEvent(dummyHabitEvent);
                }

                HabitEventListAdapter.super.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception taskException) {
                Log.i("ERROR", "Error retrieving habit event list");
            }
        });
    }
}

package com.example.habittracker.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.habittracker.R;
import com.example.habittracker.DownloadImageTask;
import com.example.habittracker.models.HabitEvent.HabitEvent;
import com.example.habittracker.models.HabitEvent.HabitEventList;
import com.example.habittracker.models.Habit.HabitList;
import com.example.habittracker.controllers.HabitEventsController;
import com.example.habittracker.interfaces.OnHabitEventDeleted;
import com.example.habittracker.interfaces.OnHabitEventsRetrieved;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;

/**
 * Adapter for habit event list, handles functionality for habit event list items
 */
public class HabitEventListAdapter extends ArrayAdapter<HabitEvent> {
    private FirebaseUser user;
    private FirebaseFirestore db;
    private HabitEventsController habitEventsController;

    private View view;
    private Context context;
    private HabitEventList habitEventList;
    private HabitList habitList;

    private TextView habitEventHabitTitle_text;
    private TextView habitEventComment_text;
    private ImageView habitEventImage;
    private TextView recordDateDescription;
    private TextView recordDateHabitEvent;
    private TextView completedDateDescription;
    private TextView completedDateHabitEvent;
    private TextView habitEventLocation_text;
    private ImageView isHabitCompletedImage;

    /**
     * HabitEventListAdapter Constructor
     * @param context the context of the app
     * @param habitEventList the list of habitEvents
     * @param habitList the list of the user's habits
     */
    public HabitEventListAdapter(@NonNull Context context, HabitEventList habitEventList, HabitList habitList) {
        super(context, 0, habitEventList.getHabitEventList());
        this.context = context;
        this.habitEventList = habitEventList;
        this.habitList = habitList;
    }

    /**
     * Configures view of list item
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_habit_event, parent, false);
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        HabitEvent habitEvent = habitEventList.get(position);
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
        ImageView deleteHabit = view.findViewById(R.id.deletehabitEvent);

        habitEventHabitTitle_text.setVisibility(View.VISIBLE);
        habitEventComment_text.setVisibility(View.VISIBLE);
        habitEventImage.setVisibility(View.VISIBLE);
        recordDateDescription.setVisibility(View.GONE);
        recordDateHabitEvent.setVisibility(View.GONE);
        completedDateDescription.setVisibility(View.VISIBLE);
        completedDateHabitEvent.setVisibility(View.VISIBLE);
        habitEventLocation_text.setVisibility(View.VISIBLE);
        isHabitCompletedImage.setVisibility(View.VISIBLE);
        deleteHabit.setVisibility(View.VISIBLE);

        // If there was no habit events on a certain day display a dummy habit event that shows no habit events today
        if (habitEvent.getComment().equals("Sent By Developer -> No Habits Today")) {
            showNoHabitEventsToday(deleteHabit);
            return view;
        }

        habitEventHabitTitle_text.setText(habitEvent.getHabit().getTitle());

        // Set all the attributes of the habit event list item
        setAttributes(habitEvent);

        deleteHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteHabit.setAlpha(0.5f);
                habitEventsController.deleteHabitEventWithCallback(habitEvent, new OnHabitEventDeleted() {
                    @Override
                    public void onHabitEventDeleted() {
                        // When we succesfully delete a habit, update the adapter
                        getHabitEvents(
                            habitEvent.getCompletedDate().getYear(),
                            habitEvent.getCompletedDate().getMonthValue(),
                            habitEvent.getCompletedDate().getDayOfMonth()
                        );
                    }

                    @Override
                    public void onError(Exception taskException) {
                        Log.e("DELETE ERROR", taskException.toString());
                    }
                });
            }
        });
        return view;
    }

    /**
     * sets attributes of the habit in the habit event list
     */
    public void setAttributes(HabitEvent habitEvent) {
        // Add comment only if there is one
        if (!habitEvent.getComment().equals("")) {
            habitEventComment_text.setText(habitEvent.getComment());
        }else{
            habitEventComment_text.setVisibility(View.GONE);
        }

        recordDateHabitEvent.setText(getDateText(habitEvent.getCreateDate()));

        // Add completed Date only if there is one
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

        // Add location only if there is one
        if (habitEvent.getLocation().length() != 0) {
            habitEventLocation_text.setText(habitEvent.getLocation());
        }else{
            habitEventLocation_text.setVisibility(View.GONE);
        }

        // If there is an image, create a new task to download it
        if (habitEvent.getImageStorageNamePrefix().length() != 0){
            new DownloadImageTask(habitEventImage).execute(habitEvent.getImageStorageNamePrefix());
        }else{
            habitEventImage.setVisibility(View.GONE);
        }
    }

    /**
     * Get the date in string format of yyyy-dd-mm
     * @param date the date to process
     * @return the formatted string
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDateText(LocalDate date) {
        return (date.getYear()) + "-" +
                (date.getMonth()) + "-" + date.getDayOfMonth();
    }

    /**
     * If there were no habits on a certain day show this by providing
     * a "dummy" habit event that say there are no habit events
     */
    public void showNoHabitEventsToday(ImageView deleteHabit) {
        // Since we don't have an information to show, hide all elements except for title
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

                // if no habit events were added add a dummy habit event to show that no habit events were added
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

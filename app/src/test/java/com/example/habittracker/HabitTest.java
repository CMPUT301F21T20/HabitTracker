package com.example.habittracker;

import com.example.habittracker.models.Habit.Habit;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class HabitTest {
    private Habit habit;

    @Before
    public void createHabit() {
        habit = new Habit();
    }

    @Test
    public void testGetTitle() {
        habit.setTitle("Test Title");
        assertEquals(habit.getTitle(), "Test Title");
    }

    @Test
    public void testGetReason() {
        habit.setReason("Test Reason");
        assertEquals(habit.getReason(), "Test Reason");
    }

    @Test
    public void testGetUserId() {
        habit.setUserId("Test");
        assertEquals(habit.getUserId(), "Test");
    }

    @Test
    public void testGetHabitId() {
        habit.setHabitId("Test");
        assertEquals(habit.getHabitId(), "Test");
    }

    @Test
    public void testGetDateCreated() {
        Date date = new Date();
        habit.setDateCreated(date);
        assertEquals(habit.getDateCreated(), date);
    }

    @Test
    public void testGetFrequency() {
        ArrayList<Integer> freq = new ArrayList<>(Arrays.asList(0, 1, 0, 0, 0, 0, 1));
        habit.setFrequency(freq);
        assertEquals(habit.getFrequency(), freq);
    }

    @Test
    public void testGetCanShare() {
        habit.setCanShare(true);
        assertEquals(habit.getCanShare(), true);
    }

    @Test
    public void testGetHabitMap() {
        Date date = new Date();
        ArrayList<Integer> freq = new ArrayList<>(Arrays.asList(0, 1, 0, 0, 0, 0, 1));
        habit = new Habit("123", "321", "Test Title", "Test Reason", date, freq, false);
        Map<String, Object> map = habit.getHabitMap();

        assertEquals(map.get("title"), "Test Title");
        assertEquals(map.get("reason"), "Test Reason");
        assertEquals(map.get("dateCreated"), date);
        assertEquals(map.get("frequency"), freq);
        assertEquals(map.get("canShare"), false);
    }
}

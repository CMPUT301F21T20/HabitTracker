package com.example.habittracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.habittracker.classes.Habit;
import com.example.habittracker.classes.HabitList;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class HabitListTest {
    private HabitList list;
    //private final String uid = Objects.requireNonNull(System.getenv("FIREBASE_TEST_ACCOUNT_ID"),
            //"FIREBASE_TEST_ACCOUNT_EMAIL env var cannot be null");

    @Before
    public void createList() {
        list = new HabitList(new ArrayList<Habit>());
    }

    @Test
    public void addHabitTest() {
        int listSize = list.getCount();
        ArrayList<Integer> freq = new ArrayList<>(Arrays.asList(0, 1, 0, 0, 0, 0, 1));
        Habit habit = new Habit("123", "321", "Test Title", "Test Reason", new Date(), freq, false);
        list.addHabit(habit);
        assertEquals(list.getCount(), listSize+1);
    }

    @Test
    public void hasHabitTest() {
        ArrayList<Integer> freq = new ArrayList<>(Arrays.asList(0, 1, 0, 0, 0, 0, 1));
        Habit habit = new Habit("123", "321", "Test Title", "Test Reason", new Date(), freq, false);
        assertFalse(list.hasHabit(habit));
        list.addHabit(habit);
        assertTrue(list.hasHabit(habit));
    }

    @Test
    public void deleteHabitTest() {
        ArrayList<Integer> freq = new ArrayList<>(Arrays.asList(0, 1, 0, 0, 0, 0, 1));
        Habit habit = new Habit("123", "321", "Test Title", "Test Reason", new Date(), freq, false);
        list.addHabit(habit);
        assertTrue(list.hasHabit(habit));
        list.deleteHabit(habit);
        assertFalse(list.hasHabit(habit));
    }

    @Test
    public void countHabitTest() {
        int listSize = list.getCount();
        assertEquals(0, listSize);
        ArrayList<Integer> freq = new ArrayList<>(Arrays.asList(0, 1, 0, 0, 0, 0, 1));
        Habit habit = new Habit("123", "321", "Test Title", "Test Reason", new Date(), freq, false);
        list.addHabit(habit);
        assertEquals(list.getCount(), 1);
    }
}

package com.example.habittracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.habittracker.classes.Habit;
import com.example.habittracker.classes.HabitList;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class HabitListTest {
    private HabitList list;
    private final String uid = Objects.requireNonNull(System.getenv("FIREBASE_TEST_ACCOUNT_ID"),
            "FIREBASE_TEST_ACCOUNT_EMAIL env var cannot be null");

    @Before
    public void createList() {
        list = new HabitList(null, new ArrayList<Habit>());
    }

    @Test
    public void addHabitTest() {
        int listSize = list.getCount();
        list.addHabit(new Habit(UUID.randomUUID().toString(), uid, "Test Title", "Test reason", new Date(), new boolean[]{true}, false,));
        assertEquals(list.getCount(), listSize+1);
    }

    @Test
    public void hasHabitTest() {
        Habit Habit = new Habit("RandomHabit", "AB");
        assertFalse(list.hasHabit(Habit));
        list.addHabit(Habit);
        assertTrue(list.hasHabit(Habit));
    }

    @Test
    public void deleteHabitTest() {
        Habit Habit = new Habit("RandomHabit", "AB");
        list.addHabit(Habit);
        assertTrue(list.hasHabit(Habit));
        list.deleteHabit(Habit);
        assertFalse(list.hasHabit(Habit));
    }

    @Test
    public void countHabitTest() {
        int listSize = list.getCount();
        assertEquals(0, listSize);
        list.addHabit(new Habit("Halifax", "NS"));
        assertEquals(list.getCount(), 1);
    }
}

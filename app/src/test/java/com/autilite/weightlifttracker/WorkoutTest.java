package com.autilite.weightlifttracker;

import com.autilite.weightlifttracker.program.Exercise;
import com.autilite.weightlifttracker.program.Program;
import com.autilite.weightlifttracker.program.Workout;

import org.junit.Test;

import static org.junit.Assert.*;

public class WorkoutTest {
    @Test
    public void workoutTest() throws Exception {
        Exercise squats = new Exercise("Squats", 5, 5, 135, 2.5, 180000);
        Exercise deadlift = new Exercise("Deadlift", 5, 5, 155, 5, 300000);
        Exercise chinup = new Exercise("Chin up", 3, 8, 140, 0, 120000);
        Exercise bicepCurl = new Exercise("Bicep Curl", 3, 8, 25, 0, 60000);

        Workout workout = new Workout("Day ");
        assertEquals("Day ", workout.getName());
        workout.setName("Day 1");
        assertEquals("Day 1", workout.getName());

        // Test adding main exercise
        assertEquals(0, workout.getMainExercises().size());
        assertTrue(workout.addMainExercise(squats));
        assertTrue(workout.getMainExercises().contains(squats));
        assertFalse(workout.getAccessoryExercises().contains(squats));
        assertEquals(1, workout.getMainExercises().size());
        assertTrue(workout.addMainExercise(deadlift));
        assertEquals(2, workout.getMainExercises().size());

        // Disallow duplicate name of existing exercise
        Exercise squats2 = new Exercise("Squats", 5, 5, 155, 2.5, 180000);
        assertFalse(workout.addMainExercise(squats2));
        assertEquals(2, workout.getMainExercises().size());

        // Remove exercises by object
        workout.removeMainExercise(squats);
        assertFalse(workout.getMainExercises().contains(squats));
        assertEquals(1, workout.getMainExercises().size());
        // Remove exercise by name
        workout.removeMainExercise("Deadlift");
        assertFalse(workout.getMainExercises().contains(deadlift));
        assertEquals(0, workout.getMainExercises().size());

        // Test adding accessory exercise
        assertEquals(0, workout.getAccessoryExercises().size());
        assertTrue(workout.addAccessoryExercise(chinup));
        assertTrue(workout.getAccessoryExercises().contains(chinup));
        assertFalse(workout.getMainExercises().contains(chinup));
        assertEquals(1, workout.getAccessoryExercises().size());
        assertTrue(workout.addAccessoryExercise(bicepCurl));
        assertEquals(2, workout.getAccessoryExercises().size());

        // Disallow duplicate name of existing exercise
        Exercise chinup2 = new Exercise("Chin up", 3, 8, 50, 0, 180000);
        assertFalse(workout.addAccessoryExercise(chinup2));
        assertEquals(2, workout.getAccessoryExercises().size());

        // Remove exercises by object
        workout.removeAccessoryExercise(chinup);
        assertFalse(workout.getAccessoryExercises().contains(chinup));
        assertEquals(1, workout.getAccessoryExercises().size());
        // Remove exercise by name
        workout.removeAccessoryExercise("Bicep Curl");
        assertFalse(workout.getAccessoryExercises().contains(bicepCurl));
        assertEquals(0, workout.getAccessoryExercises().size());
    }

    @Test
    public void programTest() throws Exception {
        String descript = "Push, pull, legs program";
        Program ppl = new Program("ppl", descript);
        Workout push = new Workout("Push");
        Exercise bench = new Exercise("Bench", 5,15,45,2.5,120000);
        Exercise neckPress = new Exercise("Seated Behind the Neck Press", 3,25,45,2.5,60000);
        Exercise tricepDips = new Exercise("Tricep Dips", 3,30,140,0,60000);
        push.addMainExercise(bench);
        push.addMainExercise(neckPress);
        push.addMainExercise(tricepDips);
        Workout pull = new Workout("Pull");
        Exercise deadlift = new Exercise("Deadlift", 5,15,135,5,120000);
        Exercise chinups = new Exercise("Chin ups", 3,25,140,2.5,60000);
        Exercise chestRow = new Exercise("Chest Supported Row", 3,30,45,2.5,60000);
        pull.addMainExercise(deadlift);
        pull.addMainExercise(chinups);
        pull.addMainExercise(chestRow);
        Workout legs = new Workout("Legs");
        Exercise squats = new Exercise("Squats", 5, 15, 95, 2.5, 120000);
        Exercise goodMornings = new Exercise("Good Morning", 3,25,45,2.5,60000);
        Exercise legPress = new Exercise("Leg Press", 3,30,135,5,60000);
        legs.addMainExercise(squats);
        legs.addMainExercise(goodMornings);
        legs.addMainExercise(legPress);
        Workout body = new Workout("Body");

        assertEquals(3,push.getMainExercises().size());
        assertEquals(3,pull.getMainExercises().size());
        assertEquals(3,legs.getMainExercises().size());
        assertEquals(0,push.getAccessoryExercises().size());
        assertEquals(0,push.getAccessoryExercises().size());
        assertEquals(0,push.getAccessoryExercises().size());

        ppl.addWorkout(push);
        ppl.addWorkout(pull);
        ppl.addWorkout(legs);
        assertEquals(3, ppl.getWorkouts().size());

        // Edit description
        assertEquals(descript, ppl.getDescription());
        ppl.setDescription("Lazy program");
        assertEquals("Lazy program", ppl.getDescription());
        String name = ppl.getName();
        String newname = "3 day " + name;
        ppl.setName(newname);
        assertEquals("3 day ppl", ppl.getName());

        // try to add workout with a duplicate name
        Workout push2 = new Workout("Push");
        assertFalse(ppl.addWorkout(push2));

        // Remove non-existing workout
        assertEquals(3, ppl.getWorkouts().size());
        ppl.removeWorkout(push2);
        assertEquals(3, ppl.getWorkouts().size());

        // Edit Push
        push.setName("Push A");
        Workout e = ppl.getWorkouts().get(0);
        assertEquals(push, e);
        assertEquals("Push A", e.getName());

        // Remove workout by object
        assertEquals(3, ppl.getWorkouts().size());
        ppl.removeWorkout(push);
        assertFalse(ppl.getWorkouts().contains(push));
        assertEquals(2, ppl.getWorkouts().size());

        // Remove workout by workout name
        assertEquals(2, ppl.getWorkouts().size());
        ppl.removeWorkout(pull.getName());
        assertFalse(ppl.getWorkouts().contains(pull));
        assertEquals(1, ppl.getWorkouts().size());
        assertTrue(ppl.getWorkouts().contains(legs));

    }
}


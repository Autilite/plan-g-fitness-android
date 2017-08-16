package com.autilite.plan_g;

import com.autilite.plan_g.program.Exercise;
import com.autilite.plan_g.program.Program;
import com.autilite.plan_g.program.Workout;

import org.junit.Test;

import static org.junit.Assert.*;

public class WorkoutTest {
    @Test
    public void workoutTest() throws Exception {
        Exercise squats = new Exercise("Squats", 5, 5, 135, 2.5, 180000);
        Exercise deadlift = new Exercise("Deadlift", 5, 5, 155, 5, 300000);

        Workout workout = new Workout("Day ");
        assertEquals("Day ", workout.getName());
        workout.setName("Day 1");
        assertEquals("Day 1", workout.getName());

        // Test adding main exercise
        assertEquals(0, workout.getExercises().size());
        assertTrue(workout.addExercise(squats));
        assertTrue(workout.getExercises().contains(squats));
        assertEquals(1, workout.getExercises().size());
        assertTrue(workout.addExercise(deadlift));
        assertEquals(2, workout.getExercises().size());

        // Disallow duplicate name of existing exercise
        Exercise squats2 = new Exercise("Squats", 5, 5, 155, 2.5, 180000);
        assertFalse(workout.addExercise(squats2));
        assertEquals(2, workout.getExercises().size());

        // Remove exercises by object
        workout.removeExercise(squats);
        assertFalse(workout.getExercises().contains(squats));
        assertEquals(1, workout.getExercises().size());
        // Remove exercise by name
        workout.removeExercise("Deadlift");
        assertFalse(workout.getExercises().contains(deadlift));
        assertEquals(0, workout.getExercises().size());
    }

    @Test
    public void programTest() throws Exception {
        String descript = "Push, pull, legs program";
        Program ppl = new Program("ppl", descript);
        Workout push = new Workout("Push");
        Exercise bench = new Exercise("Bench", 5,15,45,2.5,120000);
        Exercise neckPress = new Exercise("Seated Behind the Neck Press", 3,25,45,2.5,60000);
        Exercise tricepDips = new Exercise("Tricep Dips", 3,30,140,0,60000);
        push.addExercise(bench);
        push.addExercise(neckPress);
        push.addExercise(tricepDips);
        Workout pull = new Workout("Pull");
        Exercise deadlift = new Exercise("Deadlift", 5,15,135,5,120000);
        Exercise chinups = new Exercise("Chin ups", 3,25,140,2.5,60000);
        Exercise chestRow = new Exercise("Chest Supported Row", 3,30,45,2.5,60000);
        pull.addExercise(deadlift);
        pull.addExercise(chinups);
        pull.addExercise(chestRow);
        Workout legs = new Workout("Legs");
        Exercise squats = new Exercise("Squats", 5, 15, 95, 2.5, 120000);
        Exercise goodMornings = new Exercise("Good Morning", 3,25,45,2.5,60000);
        Exercise legPress = new Exercise("Leg Press", 3,30,135,5,60000);
        legs.addExercise(squats);
        legs.addExercise(goodMornings);
        legs.addExercise(legPress);
        Workout body = new Workout("Body");

        assertEquals(3,push.getExercises().size());
        assertEquals(3,pull.getExercises().size());
        assertEquals(3,legs.getExercises().size());

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


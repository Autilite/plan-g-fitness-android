package com.autilite.weightlifttracker.program.session;

import android.os.Parcel;
import android.os.Parcelable;

import com.autilite.weightlifttracker.program.Exercise;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kelvin on Jun 19, 2017.
 */

public class ExerciseSession implements Parcelable {
    public static final int EXERCISE_COMPLETE = -1;
    private static final int INCOMPLETE_SET = -1;

    private Exercise exercise;
    private int currentSet;
    private int currentRep;
    private double currentWeight;
    private List<SetSession> setSessions;

    public ExerciseSession(Exercise exercise) {
        this.exercise = exercise;
        setSessions = new LinkedList<>();

        for (int i = 1; i <= exercise.getSets(); i++) {
            SetSession s = new SetSession(i, INCOMPLETE_SET, INCOMPLETE_SET);
            setSessions.add(s);
        }
        currentSet = 1;
        currentRep = exercise.getReps();
        currentWeight = exercise.getWeight();
    }

    protected ExerciseSession(Parcel in) {
        exercise = in.readParcelable(Exercise.class.getClassLoader());
        currentSet = in.readInt();
        currentRep = in.readInt();
        currentWeight = in.readDouble();
        setSessions = in.createTypedArrayList(SetSession.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(exercise, flags);
        dest.writeInt(currentSet);
        dest.writeInt(currentRep);
        dest.writeDouble(currentWeight);
        dest.writeTypedList(setSessions);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExerciseSession> CREATOR = new Creator<ExerciseSession>() {
        @Override
        public ExerciseSession createFromParcel(Parcel in) {
            return new ExerciseSession(in);
        }

        @Override
        public ExerciseSession[] newArray(int size) {
            return new ExerciseSession[size];
        }
    };

    /**
     * Complete the set for param set with the respective rep and weight
     *
     * @param set The set number
     * @param reps The number of successful reps
     * @param weight The weight for the set
     * @return  false: parameters invalid and were not added to session
     *          true: paramters added to the session
     */
    public boolean completeSet(int set, int reps, double weight) {
        if (reps < 0 || weight < 0) {
            return false;
        }
        try {
            int index = set - 1;
            SetSession s = setSessions.get(index);
            s.setReps(reps);
            s.setWeight(weight);
            if (set == currentSet) {
                incrementCurrentSet();
            }
            return true;
        } catch (IndexOutOfBoundsException e) {
            // Since we handle the set number by the position in SetSession, we can use this
            // exception to determine if the set number if valid
            return false;
        }
    }

    public boolean completeSet(int reps, double weight) {
        return completeSet(currentSet, reps, weight);
    }

    private void incrementCurrentSet() {
        if (currentSet == setSessions.size()) {
            // Set the value to be 1 value outside the outer bound.
            // This can be used to distinguish a complete set
            // while keeping the counter consistent for when a new set is added.
            currentSet++;
            return;
        }
        // Find the first incomplete set
        for (int i = currentSet; i < setSessions.size(); i++) {
            // current set is 1-index whereas reps[] is 0-index
            // -> checking currentSet is the next entry
            SetSession s = setSessions.get(i);
            if (s.getReps() == INCOMPLETE_SET) {
                currentSet = i + 1;
                break;
            }
        }
    }

    /**
     * Return the current set or Session.EXERCISE_COMPLETE if all sets are complete
     * @return
     */
    public int getCurrentSet() {
        // return -1 if session is over
        // i.e., currentSet > sets
        if (currentSet > setSessions.size()) {
            return EXERCISE_COMPLETE;
        } else {
            return currentSet;
        }
    }

    public int getNumCompleteSets() {
        int counter = 0;
        for (SetSession set: setSessions) {
            if (set.getReps() != INCOMPLETE_SET) {
                counter++;
            }
        }
        return counter;
    }

    public boolean isSessionDone() {
        return getCurrentSet() == EXERCISE_COMPLETE;
    }

    public List<SetSession> getSetSessions() {
        return setSessions;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public int getCurrentRep() {
        return currentRep;
    }

    public void setCurrentRep(int currentRep) {
        this.currentRep = currentRep;
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(double currentWeight) {
        this.currentWeight = currentWeight;
    }

    public boolean isSetComplete(SetSession session) {
        checkValidSession(session);
        return isSetComplete(session.getSetNumber());
    }

    public boolean isSetComplete(int setNumber) {
        int index = setNumber - 1;
        SetSession set = setSessions.get(index);
        return isSetComplete(set.getReps(), set.getWeight());
    }

    private boolean isSetComplete(int reps, double weight) {
        return reps != INCOMPLETE_SET && weight != INCOMPLETE_SET;
    }

    public boolean isSetSuccessful(SetSession session) {
        checkValidSession(session);
        return isSetSuccessful(session.getSetNumber());
    }

    public boolean isSetSuccessful(int setNumber) {
        int index = setNumber - 1;
        SetSession set = setSessions.get(index);
        return isSetSuccesful(set.getReps());
    }

    private boolean isSetSuccesful(int reps) {
        return reps >= exercise.getReps();
    }

    /**
     * Checks if this SetSession belongs to the ExerciseSession
     *
     * Throw an IllegalArgumentException if it doesn't
     *
     * @param session
     */
    private void checkValidSession(SetSession session) {
        // Get the session's set number and check if this is the same this object's session
        // This is so we only need to compare a single object rather than iterating through
        // the entire list to find session
        int index = session.getSetNumber() - 1;
        SetSession set = setSessions.get(index);

        if (!session.equals(set)) {
            throw new IllegalArgumentException("This "+ SetSession.class.getSimpleName() +
                    " doesn't belong to the " + ExerciseSession.class.getSimpleName());
        }
    }
}

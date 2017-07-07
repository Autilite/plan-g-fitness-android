package com.autilite.weightlifttracker.program.session;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kelvin on Jun 19, 2017.
 */

class ExerciseSession {
    private static final int INCOMPLETE_SET = -1;

    private int currentSet;
    private List<SetSession> setSessions;

    ExerciseSession(int maxSets) {
        setSessions = new LinkedList<>();

        for (int i = 1; i <= maxSets; i++) {
            SetSession s = new SetSession(i, INCOMPLETE_SET, INCOMPLETE_SET);
            setSessions.add(s);
        }
        currentSet = 1;
    }

    /**
     * Complete the set for param set with the respective rep and weight
     *
     * @param set The set number
     * @param reps The number of successful reps
     * @param weight The weight for the set
     * @return  false: parameters invalid and were not added to session
     *          true: paramters added to the session
     */
    boolean completeSet(int set, int reps, float weight) {
        if (reps < 0 || weight < 0) {
            return false;
        }
        try {
            int index = set - 1;
            SetSession s = setSessions.get(index);
            s.setReps(reps);
            s.setWeight(reps);
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    boolean completeSet(int reps, float weight) {
        if (completeSet(currentSet, reps, weight)) {
            incrementCurrentSet();
            return true;
        }
        return true;
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
     * Return the current set or -1 if all sets are complete
     * @return
     */
    int getCurrentSet() {
        // return -1 if session is over
        // i.e., currentSet > sets
        if (currentSet > setSessions.size()) {
            return Session.EXERCISE_COMPLETE;
        } else {
            return currentSet;
        }
    }
}

package com.autilite.weightlifttracker.program;

import java.util.Arrays;

/**
 * Created by Kelvin on Jun 19, 2017.
 */

class ExerciseSession {
    private static final int INCOMPLETE_SET = -1;

    private int sets;
    private int[] reps;
    private float[] weight;
    private int currentSet;

    ExerciseSession(int maxSets) {
        sets = maxSets;
        // add an extra entry for checking EOF in increment set
        reps = new int[sets + 1];
        weight = new float[sets + 1];
        Arrays.fill(reps, INCOMPLETE_SET);
        Arrays.fill(weight, INCOMPLETE_SET);
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
        // this.reps and this.weight are 0-index whereas set is 1-index
        if (set == currentSet) {
            return completeSet(reps, weight);
        } else if (set <= 0 || set > this.sets) {
            return false;
        } else if (reps < 0 || weight < 0) {
            return false;
        }
        this.reps[set-1] = reps;
        this.weight[set-1] = weight;
        return true;
    }

    boolean completeSet(int reps, float weight) {
        if (currentSet > sets) {
            return false;
        } else if (reps < 0 || weight < 0) {
            return false;
        }
        this.reps[currentSet-1] = reps;
        this.weight[currentSet-1] = weight;
        incrementCurrentSet();
        return true;
    }

    private void incrementCurrentSet() {
        for (int i = currentSet; i <= sets; i++) {
            // current set is 1-index whereas reps[] is 0-index
            // -> checking reps[currentSet] is the next entry
            if (reps[i] == INCOMPLETE_SET) {
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
        if (currentSet > sets) {
            return -1;
        } else {
            return currentSet;
        }
    }
}

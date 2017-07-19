package com.autilite.weightlifttracker.database;

import android.provider.BaseColumns;

/**
 * Created by Kelvin on Jun 10, 2017.
 */

public final class ProgramSessionContract {
    private ProgramSessionContract() {
    }

    public static class ProgramSessionEntry implements BaseColumns {
        public static final String TABLE_NAME = "ProgramSession";
        public static final String COLUMN_PROGRAM_ID = "ProgId";
        public static final String COLUMN_PROGRAM_DAY = "ProgDay";
        public static final String COLUMN_DATE = "SessionDate";
    }
}

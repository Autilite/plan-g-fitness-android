package com.autilite.weightlifttracker.exception;

import android.database.sqlite.SQLiteException;

/**
 * Created by Kelvin on Jul 21, 2017.
 */

public class SQLiteUpdateException extends SQLiteException {
    public SQLiteUpdateException() {
        super();
    }

    public SQLiteUpdateException(String error) {
        super(error);
    }

    public SQLiteUpdateException(String error, Throwable cause) {
        super(error, cause);
    }
}

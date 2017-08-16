package com.autilite.plan_g.exception;

import android.database.sqlite.SQLiteException;

/**
 * Created by Kelvin on Jul 21, 2017.
 */

public class SQLiteInsertException extends SQLiteException {
    public SQLiteInsertException() {
        super();
    }

    public SQLiteInsertException(String error) {
        super(error);
    }

    public SQLiteInsertException(String error, Throwable cause) {
        super(error, cause);
    }
}

package idrabenia.worktime.domain.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import idrabenia.worktime.R;
import idrabenia.worktime.domain.date.Time;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Ilya Drabenia
 * @since 27.04.13
 */
public class WorkTimeDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "worktime.db";
    private static final int DATABASE_VERSION = 1;
    private final String[] DATABASE_DDL;

    public WorkTimeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DATABASE_DDL = context.getResources().getStringArray(R.array.worktime_database_ddl);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String curStatement : DATABASE_DDL) {
            db.execSQL(curStatement);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do not needed
    }

}

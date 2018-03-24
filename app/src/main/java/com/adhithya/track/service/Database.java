package com.adhithya.track.service;

/**
 * Created by ASUS on 3/20/2018.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.adhithya.track.model.User;

import java.util.ArrayList;


public class Database { private static final String TBL_USER = "tbl_user";

    private static final String CLM_ID = "id";
    private static final String CLM_UID = "uid";
    private static final String CLM_MOBILE = "mobile";
    private static final String CLM_NAME = "name";


    private SQLiteDatabase db;
    private SqliteDatbase openHelper;
    private static Database database;


    private Database(Context ctx) {
        openHelper = new SqliteDatbase(ctx);
        db = openHelper.getWritableDatabase();
    }

    public static Database init(Context ctx) {
        //if (database == null) {
        database = new Database(ctx);
        // }
        return database;
    }

    public static Database getInstance(Context ctx) {
        if (database == null) {
            database = new Database(ctx);
        }
        return database;
    }


    public static Database getInstance() {
        if (database != null) {
            database.openHelper.onCreate(database.openHelper.getWritableDatabase());
        }
        return database;
    }

    //To check data exists or not
    public static void checkData() {
        if (database != null) {
            database.openHelper.onCreate(database.openHelper.getWritableDatabase());
            database.db = database.openHelper.getWritableDatabase();
        }
    }

    public static void clear() {
        if (database != null) {
            try {
                database.openHelper.close();
            } catch (Exception e) {
            }
            try {
                database.db.close();
            } catch (Exception e) {
            }
            database.openHelper = null;
            database.db = null;
        }
        database = null;

    }

    public static void closeCursor(Cursor cursor) {
        try {
            cursor.close();
        } catch (Exception e) {
        }
    }

    private void delete(String table) {
        delete(table, null, null);
    }

    private void delete(String table, String condition, String[] args) {
        db.delete(table, condition, args);
    }




    public void login(User userModel) {
        delete(TBL_USER);
        db.insert(TBL_USER, null, getCvForLogin(userModel));
    }

    public void updateLoginDetails(User userModel) {
        ContentValues cv = getCvForLogin(userModel);
        cv.remove(CLM_ID);
        db.update(TBL_USER, cv, String.format("%s=?", CLM_ID), new String[]{String.valueOf(userModel.getUid())});
    }

    //
    public boolean isLoggedIn() {
        return getLoggedUserId(-1) > 0;
    }

    //
    public User getLoggedUser() {
        Cursor c = db.query(TBL_USER, null, null, null, null, null, null);
        if (c.getCount() == 0) {
            return null;
        }
        User model = new User();
        while (c.moveToFirst()) {
            model.setUid(c.getInt(0));
            model.setUid(c.getInt(1));
            model.setReg_mobile_no(c.getString(2));
            model.setName(c.getString(3));
            return model;
        }
        closeCursor(c);
        return model;
    }
    public int getLoggedUserId(int defaultId) {
        User model = getLoggedUser();
        if (model != null) {
            return model.getUid();
        }
        return defaultId;
    }

    public void logout() {
        delete(TBL_USER);
    }

    private ContentValues getCvForLogin(User model) {
        ContentValues cv = new ContentValues();
        cv.put(CLM_ID, model.getUid());
        cv.put(CLM_UID, model.getUid());
        cv.put(CLM_MOBILE, model.getReg_mobile_no());
        cv.put(CLM_NAME, model.getName());
        return cv;
    }


    public static class SqliteDatbase extends SQLiteOpenHelper {

        private Context context;

        public SqliteDatbase(Context context) {
            super(context, "sqlite.tracker", null, 1);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            if (context == null) {
                return;
            }
            context = null;

            db.execSQL(gcs(TBL_USER, new String[]{CLM_UID,CLM_MOBILE,CLM_NAME},
                    new int[]{2,0,0}));


        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }

        private String gcs(String table, String[] clms, int[] type) {

            StringBuilder builder = new StringBuilder();
            builder.append("CREATE TABLE IF NOT EXISTS ").append(table).append(" (").append(CLM_ID).append(" INT(100) ,");
            for (int i = 0, j = clms.length; i < j; i++) {
                builder.append(clms[i]).append(" ").append(getStringForType(type[i])).append(",");
            }
            if (clms.length > 0) {
                builder.deleteCharAt(builder.length() - 1);
                builder.append(")");
            }
            return builder.toString();
        }

        private String getStringForType(int type) {
            switch (type) {
                case 0:
                    return "VARCHAR(255)";
                case 1:
                    return "INT(50)";
                case 2:
                    return "TEXT";
                case 3:
                    return "BIGINT(20)";
                case 4:
                    return "FLOAT";
                case 5:
                    return "INT(1)";
                case 6:
                    return "DATETIME";
            }
            return "VARCHAR(255)";
        }

    }
}
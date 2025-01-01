package com.example.fitapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final String TABLE_NAME = "user";
    private static final String COL_ID = "user_id";
    private static final String COL_USERNAME = "username";
    private static final String COL_EMAIL = "user_email";
    private static final String COL_PASSWORD = "user_password";
    private static final String COL_GENDER = "user_gender";
    private static final String COL_DOB = "user_birthday";
    private static final String COL_PHONE = "user_phonenumber";
    private static final String COL_POINTS = "user_points";
    private static final String COL_SIGN_UP_DATE = "user_sign_up_date";
    private static final String COL_PROFILE_PIC_PATH = "profile_pic_path";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT UNIQUE NOT NULL, "
                + COL_EMAIL + " TEXT UNIQUE NOT NULL, "
                + COL_PASSWORD + " TEXT NOT NULL, "
                + COL_GENDER + " TEXT DEFAULT 'Prefer Not to Say', "
                + COL_DOB + " TEXT DEFAULT 'Prefer Not to Say', "
                + COL_PHONE + " TEXT DEFAULT 'Prefer Not to Say', "
                + COL_POINTS + " INTEGER DEFAULT 0, "
                + COL_SIGN_UP_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + COL_PROFILE_PIC_PATH + " TEXT"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_GENDER, "Prefer Not to Say");
        values.put(COL_DOB, "Prefer Not to Say");
        values.put(COL_PHONE, "Prefer Not to Say");
        values.put(COL_POINTS, 0);

        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COL_USERNAME + "=?",
                new String[]{username}, null, null, null);
        return cursor.getCount() > 0;
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COL_EMAIL + "=?",
                new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    //For login purpose
    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean updateUserProfilePic(int userId, String profilePicPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PROFILE_PIC_PATH, profilePicPath);

        int rowsAffected = db.update(TABLE_NAME, values, COL_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsAffected > 0;
    }
}


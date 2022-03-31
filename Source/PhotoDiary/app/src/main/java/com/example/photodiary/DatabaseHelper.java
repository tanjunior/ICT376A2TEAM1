package com.example.photodiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.photodiary.data.model.DiaryModel;
import com.example.photodiary.data.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String USER_TABLE = "Users";
    private static final String USER_ID = "UserId";
    private static final String USER_NAME = "Username";
    private static final String USER_PASSWORD = "Password";

    private static final String DIARY_TABLE = "Diaries";
    private static final String DIARY_ID = "DiaryId";
    private static final String DIARY_TITLE = "Title";
    private static final String DIARY_DATE = "Date";
    private static final String DIARY_TIME = "Time";
    private static final String DIARY_LOCATION = "Location";
    private static final String DIARY_DESCRIPTION = "Description";
    private static final String DIARY_FILE_NAME = "Filename";
    private static final String DIARY_IMAGE_URI = "ImageURI";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "photodiary.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTableStatement =
                "CREATE TABLE " + USER_TABLE + "(" +
                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_NAME + " TEXT NOT NULL, " +
                USER_PASSWORD + " TEXT NOT NULL)";

        String createDiaryTableStatement =
                "CREATE TABLE " + DIARY_TABLE + "(" +
                DIARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DIARY_TITLE + " TEXT NOT NULL, " +
                DIARY_DATE + " TEXT NOT NULL, " +
                DIARY_TIME + " TEXT NOT NULL, " +
                DIARY_LOCATION + " TEXT NOT NULL, " +
                DIARY_DESCRIPTION + " TEXT NOT NULL, " +
                DIARY_FILE_NAME + " TEXT NOT NULL, "+
                DIARY_IMAGE_URI + " TEXT NOT NULL, " +
                USER_ID + " INTEGER NOT NULL, "+
                "FOREIGN KEY("+USER_ID+") REFERENCES "+USER_TABLE+"("+USER_ID+"))";

        db.execSQL(createUserTableStatement);
        db.execSQL(createDiaryTableStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean checkIfUserExists(String username) {
        String queryString = "SELECT * FROM " + USER_TABLE + " WHERE "+ USER_NAME + " LIKE '"+username+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        boolean b = cursor.getCount() != 0;
        cursor.close();
        db.close();
        return b;

    }

    public boolean addUser(String username, String password) {
        boolean userExist = checkIfUserExists(username);
        if (userExist) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USER_NAME, username);
        cv.put(USER_PASSWORD, password);

        long insert = db.insert(USER_TABLE, null, cv);
        db.close();
        return insert != -1;
    }

    public UserModel getUser(String username) {
        UserModel user;
        String queryString = "SELECT * FROM " + USER_TABLE + " WHERE "+ USER_NAME + " LIKE '"+username+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);



        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String password = cursor.getString(2);
            user = new UserModel(id, username, password);
        } else {
            user = new UserModel();
        }


        cursor.close();
        db.close();
        return user;
    }

    public List<DiaryModel> getAllUserDiaries(int userID) {
        List<DiaryModel> diaryList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + DIARY_TABLE + " WHERE "+ USER_ID + " = "+userID;
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String date = cursor.getString(2);
                String time = cursor.getString(3);
                String loc = cursor.getString(4);
                String desc = cursor.getString(5);
                String fileName = cursor.getString(6);
                String imgPath = cursor.getString(7);
                DiaryModel diaryModel = new DiaryModel(id, title, date, time, loc, desc, fileName, imgPath, userID);
                diaryList.add(diaryModel);
            } while (cursor.moveToNext());
        } else {
            Log.i("diaries", "no diaries found");
        }

        cursor.close();
        db.close();

        return diaryList;
    }

    public boolean addDiary(DiaryModel diaryModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DIARY_TITLE, diaryModel.getTitle());
        cv.put(DIARY_DATE, diaryModel.getDate());
        cv.put(DIARY_TIME, diaryModel.getTime());
        cv.put(DIARY_LOCATION, diaryModel.getLocation());
        cv.put(DIARY_DESCRIPTION, diaryModel.getDescription());
        cv.put(DIARY_FILE_NAME, diaryModel.getFileName());
        cv.put(DIARY_IMAGE_URI, diaryModel.getImageUri());
        cv.put(USER_ID, diaryModel.getUserId());

        long insert = db.insert(DIARY_TABLE, null, cv);
        db.close();
        return insert != -1;
    }
}

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String USER_TABLE = "Users";
    private static final String USER_ID = "UserId";
    private static final String USER_EMAIL = "Email";
    private static final String USER_NAME = "Name";
    private static final String USER_PASSWORD = "Password";
    private static final String USER_GENDER = "Gender";
    private static final String USER_DOB = "Dob";
    private static final String USER_PROFILE_PHOTO_PATH = "ProfilePhotoPath";

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
                        USER_EMAIL + " TEXT NOT NULL, " +
                        USER_NAME + " TEXT NOT NULL, " +
                        USER_PASSWORD + " TEXT NOT NULL, " +
                        USER_GENDER + " TEXT, " +
                        USER_DOB + " TEXT, " +
                        USER_PROFILE_PHOTO_PATH + " TEXT)";

        String createDiaryTableStatement =
                "CREATE TABLE " + DIARY_TABLE + "(" +
                        DIARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DIARY_TITLE + " TEXT NOT NULL, " +
                        DIARY_DATE + " TEXT NOT NULL, " +
                        DIARY_TIME + " TEXT NOT NULL, " +
                        DIARY_LOCATION + " TEXT NOT NULL, " +
                        DIARY_DESCRIPTION + " TEXT NOT NULL, " +
                        DIARY_FILE_NAME + " TEXT NOT NULL, " +
                        DIARY_IMAGE_URI + " TEXT NOT NULL, " +
                        USER_ID + " INTEGER NOT NULL, " +
                        "FOREIGN KEY(" + USER_ID + ") REFERENCES " + USER_TABLE + "(" + USER_ID + "))";

        String insertTestAccountStatement =
                "INSERT INTO " + USER_TABLE +
                        "(" + USER_EMAIL + ", " + USER_NAME + ", " + USER_PASSWORD + ")" +
                        "VALUES ('user1@gmail.com', 'User 1', '1234')";

        db.execSQL(createUserTableStatement);
        db.execSQL(createDiaryTableStatement);
        db.execSQL(insertTestAccountStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean checkIfUserExists(String email) {
        String queryString = "SELECT * FROM " + USER_TABLE + " WHERE " + USER_EMAIL + " LIKE '" + email + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        boolean b = cursor.getCount() != 0;
        cursor.close();
        db.close();
        return b;
    }

    public boolean addUser(
            String email,
            String name,
            String password,
            String gender,
            String dob,
            String profilePhotoPath
    ) {
        boolean userExist = checkIfUserExists(email);
        if (userExist) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USER_EMAIL, email);
        cv.put(USER_NAME, name);
        cv.put(USER_PASSWORD, password);
        cv.put(USER_GENDER, gender);
        cv.put(USER_DOB, dob);
        cv.put(USER_PROFILE_PHOTO_PATH, profilePhotoPath);

        long insert = db.insert(USER_TABLE, null, cv);
        db.close();
        return insert != -1;
    }

    public UserModel getUserById(int id) {
        UserModel user;
        String queryString = "SELECT * FROM " + USER_TABLE + " WHERE " + USER_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            String email = cursor.getString(1);
            String name = cursor.getString(2);
            String password = cursor.getString(3);
            String gender = cursor.getString(4);
            String dobString = cursor.getString(6);
            Date dob;
            try {
                dob = new SimpleDateFormat("dd/mm/yyyy").parse(dobString);
            } catch (Exception e) {
                dob = null;
            }
            String profilePhotoPath = cursor.getString(6);
            user = new UserModel(id, email, name, password, gender, dob, profilePhotoPath);
        } else {
            user = new UserModel();
        }

        cursor.close();
        db.close();
        return user;
    }

    public UserModel getUserByEmail(String email) {
        UserModel user;
        String queryString = "SELECT * FROM " + USER_TABLE + " WHERE " + USER_EMAIL + " LIKE '" + email + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(2);
            String password = cursor.getString(3);
            String gender = cursor.getString(4);
            String dobString = cursor.getString(6);
            Date dob;
            try {
                dob = new SimpleDateFormat("dd/mm/yyyy").parse(dobString);
            } catch (Exception e) {
                dob = null;
            }
            String profilePhotoPath = cursor.getString(6);
            user = new UserModel(id, email, name, password, gender, dob, profilePhotoPath);
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
        String queryString = "SELECT * FROM " + DIARY_TABLE + " WHERE " + USER_ID + " = " + userID;
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

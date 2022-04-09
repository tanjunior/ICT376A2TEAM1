package com.example.photodiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.photodiary.data.model.UserModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class EditProfile extends AppCompatActivity {

    public static final String USER_ID = "USER_ID";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY_IMAGE = 2;

    private ImageView ivProfile;
    private EditText etEmail, etName, etPassword, etGender, etDob;

    private final ImageListDialogFragment imageFragment = ImageListDialogFragment.newInstance(2);

    private int userId;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        userId = getIntent().getIntExtra(USER_ID, 1);
        db = new DatabaseHelper(this);

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        // init views
        ivProfile = findViewById(R.id.ivProfile);
        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etGender = findViewById(R.id.etGender);
        etDob = findViewById(R.id.etDob);
        Button btnSave = findViewById(R.id.btnSave);

        // gender picker
        String[] genders = {"Male", "Female", "Custom"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select gender");
        builder.setItems(genders, (dialog, which) -> etGender.setText(genders[which]));

        // view onclick listener
        etGender.setOnClickListener(view -> builder.show());
        etDob.setOnClickListener(view -> datePickerFragment.show(getSupportFragmentManager(), "datePicker"));
        ivProfile.setOnClickListener(view -> imageFragment.show(getSupportFragmentManager(), "dialog"));
        btnSave.setOnClickListener(view -> saveUser());

        // fragment result listener
        getSupportFragmentManager().setFragmentResultListener("request_Key", this, (requestKey, bundle) -> {
            if (bundle.containsKey("date")) {
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String dateString = dateFormat.format((LocalDate)bundle.getSerializable("date"));
                etDob.setText(dateString);
            }
        });

        populateProfile();
    }

    private void populateProfile() {
        UserModel user = db.getUserById(userId);

        etEmail.setText(user.getEmail());
        etName.setText(user.getName());
        etPassword.setText(user.getPassword());

        if (user.getGender() != null) {
            etGender.setText(user.getGender());
        }

        if (user.getDob() != null) {
            etDob.setText(user.getDob());
        }

        if (user.getProfilePhotoPath() != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(user.getProfilePhotoPath(), options);
                ivProfile.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveUser() {
        String email = etEmail.getText().toString();
        if (email.trim().isEmpty()) {
            Toast.makeText(this, "email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = etName.getText().toString();
        if (name.trim().isEmpty()) {
            Toast.makeText(this, "name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = etPassword.getText().toString();
        if (password.trim().isEmpty()) {
            Toast.makeText(this, "password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap;
        try {
            bitmap = ((BitmapDrawable)ivProfile.getDrawable()).getBitmap();
        } catch (ClassCastException e) {
            Toast.makeText(this, "Profile Picture cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // save to file
        ZoneId zone = ZoneId.of("Asia/Singapore");
        LocalDateTime localDatetime = LocalDateTime.now(zone);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timeStamp = dateFormat.format(localDatetime);
        String imageFileName = "JPEG_" + timeStamp + "_.jpeg";

        String imagePath = saveImage(imageFileName, bitmap);

        DatabaseHelper db = new DatabaseHelper(this);
        boolean b = db.updateUser(userId, email, name, password, etGender.getText().toString(), etDob.getText().toString(), imagePath);
        if (b) {
            finish();
            Toast.makeText(this,"Updated profile successfully",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Failed to update profile",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // get bitmap
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // update view
            ivProfile.setImageBitmap(imageBitmap);
            imageFragment.dismiss();
        } else if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK) {

            try {
                Uri uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                ivProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageFragment.dismiss();
        }
    }

    private String saveImage(String filename, Bitmap bitmapImage){
        FileOutputStream outputStream = null;
        Log.i("image","filename: "+filename);

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);


        } catch (IOException e) {
            e.printStackTrace();
            Log.i("info","exception at writeToFile ");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("info","exception at writeToFile ");
                }
            }
        }

        //shows where the file is stored in the system
        File filesDir = getFilesDir();
        Log.i("image",filesDir.getAbsolutePath());
        return filesDir.getAbsolutePath() + "/" + filename;
    }
}
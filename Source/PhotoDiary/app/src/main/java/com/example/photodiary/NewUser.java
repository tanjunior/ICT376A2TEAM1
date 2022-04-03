package com.example.photodiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class NewUser extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY_IMAGE = 2;

    private ImageView ivProfile;
    private EditText etEmail, etName, etPassword, etGender, etDob;

    ImageListDialogFragment imageFragment = ImageListDialogFragment.newInstance(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        // init views
        ivProfile = findViewById(R.id.ivProfile);
        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etGender = findViewById(R.id.etGender);
        etDob = findViewById(R.id.etDob);

        // gender picker
        String[] genders = {"Male", "Female", "Custom"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select gender");
        builder.setItems(genders, (dialog, which) -> etGender.setText(genders[which]));

        // view onclick listener
        etGender.setOnClickListener(view -> builder.show());
        etDob.setOnClickListener(view -> datePickerFragment.show(getSupportFragmentManager(), "datePicker"));
        ivProfile.setOnClickListener(view -> imageFragment.show(getSupportFragmentManager(), "dialog"));

        // fragment result listener
        getSupportFragmentManager().setFragmentResultListener("request_Key", this, (requestKey, bundle) -> {
            if (bundle.containsKey("date")) {
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String dateString = dateFormat.format((LocalDate)bundle.getSerializable("date"));
                etDob.setText(dateString);
            }
        });
    }

    public void createUser(View view) {
        String email = etEmail.getText().toString();
        if (email.trim().isEmpty()) {
            Toast.makeText(this, "email cannot be empty", Toast.LENGTH_SHORT).show();
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
        boolean b = db.addUser(email, name, password, etGender.getText().toString(), etDob.getText().toString(), imagePath);
        if (b) {
            finish();
            Toast.makeText(this,"User created successfully",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Username already exists!",Toast.LENGTH_SHORT).show();
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
        return filesDir.getAbsolutePath();
    }
}
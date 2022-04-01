package com.example.photodiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewUser extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private String profilePhotoPath;

    private ImageView ivProfile;
    private EditText etEmail, etName, etPassword, etGender, etDob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

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
        builder.setItems(genders, (dialog, which) -> {
            etGender.setText(genders[which]);
        });
        etGender.setOnClickListener(view -> builder.show());

        // dob picker
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            int actualMonth = month + 1;
            etDob.setText(day + "/" + actualMonth + "/" + year);
        };
        etDob.setOnClickListener(view -> {
            Calendar calendar= Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(NewUser.this, dateSetListener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            datePickerDialog.setTitle("Select date of birth");
            datePickerDialog.show();
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

        DatabaseHelper db = new DatabaseHelper(this);
        boolean b = db.addUser(email, name, password, etGender.getText().toString(), etDob.getText().toString(), profilePhotoPath);
        if (b) {
            finish();
            Toast.makeText(this,"User created successfully",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Username already exists!",Toast.LENGTH_SHORT).show();
        }
    }

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
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

            // save to file
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File sd = Environment.getExternalStorageDirectory();
            File dest = new File(sd, imageFileName);
            try {
                FileOutputStream out = new FileOutputStream(dest);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();
                profilePhotoPath = dest.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
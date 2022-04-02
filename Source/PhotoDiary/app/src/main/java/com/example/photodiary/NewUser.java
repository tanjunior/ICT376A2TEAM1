package com.example.photodiary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class NewUser extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY_IMAGE = 2;

    private String profilePhotoPath;

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

        DatabaseHelper db = new DatabaseHelper(this);
        boolean b = db.addUser(email, name, password, etGender.getText().toString(), etDob.getText().toString(), profilePhotoPath);
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

            imageFragment.dismiss();
        } else if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);
            ivProfile.setImageBitmap(imageBitmap);
            imageFragment.dismiss();

        }
    }
}
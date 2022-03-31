package com.example.photodiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewUser extends AppCompatActivity {

    EditText newUsername, newPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
    }



    public void createUser(View view) {
        DatabaseHelper db = new DatabaseHelper(this);
        newUsername = findViewById(R.id.newUserName);
        newPassword = findViewById(R.id.newUserPassword);
        boolean b = db.addUser(newUsername.getText().toString(), newPassword.getText().toString());
        if (b) {
            finish();
        } else {
            Toast.makeText(this,"Username already exists!",Toast.LENGTH_SHORT).show();
        }
    }
}
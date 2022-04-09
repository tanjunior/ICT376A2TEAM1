package com.example.photodiary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.photodiary.data.model.UserModel;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
    }

    public void login(View view) {
        String email = etEmail.getText().toString();
        if (email.trim().isEmpty()) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = etPassword.getText().toString();
        if (password.trim().isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper db = new DatabaseHelper(this);
        UserModel user = db.getUserByEmail(email);
        if (password.equals(user.getPassword())) {
            Intent i = new Intent(MainActivity.this, UserLanding.class);
            i.putExtra("USER_ID", user.getId());
            startActivity(i);
        } else {
            Toast.makeText(this,"Wrong Email/Password",Toast.LENGTH_SHORT).show();
        }
    }

    public void newUser(View view) {
        startActivity(new Intent(this, NewUser.class));
    }

    public void testUser(View view) {
        Intent i = new Intent(MainActivity.this, UserLanding.class);
        i.putExtra("USER_ID", 1);
        startActivity(i);
    }
}
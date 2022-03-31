package com.example.photodiary;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.photodiary.data.model.UserModel;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
    }

    public void login(View view) {
        String enteredPassword = password.getText().toString();
        String enteredUsername = username.getText().toString();
        DatabaseHelper db = new DatabaseHelper(this);
        UserModel user = db.getUser(enteredUsername);
        if (enteredPassword.equals(user.getPassword())) {
            Intent i = new Intent(MainActivity.this, UserLanding.class);
            i.putExtra("USER_ID", user.getId());
            startActivity(i);
        } else {
            Toast.makeText(this,"Wrong Username/Password",Toast.LENGTH_SHORT).show();
        }
    }

    public void newUser(View view) {
        startActivity(new Intent(this, NewUser.class));
    }

    public void testUser(View view) {
        DatabaseHelper db = new DatabaseHelper(this);
        final boolean test = db.checkIfUserExists("test");
        if (!test) db.addUser("test", "1234");

        Intent i = new Intent(MainActivity.this, UserLanding.class);
        i.putExtra("USER_ID", 1);
        startActivity(i);
    }
}
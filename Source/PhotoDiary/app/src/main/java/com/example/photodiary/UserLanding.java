package com.example.photodiary;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class UserLanding extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_landing);
        setSupportActionBar(findViewById(R.id.toolbar));

        Intent intent = getIntent();

        if (intent != null) {
            userId = intent.getIntExtra("USER_ID", 1);
        } else {
            finish();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.rView);

        fab.setOnClickListener(view -> {
            Intent newIntent = new Intent(UserLanding.this, NewEntry.class);
            newIntent.putExtra("USER_ID", userId);
            startActivity(newIntent);
        });
        databaseHelper = new DatabaseHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showUserDiaries();
        String name = databaseHelper.getUserById(userId).getName();
        setTitle(name);
    }

    private void showUserDiaries() {
        DiariesAdapter diariesAdapter = new DiariesAdapter(databaseHelper.getAllUserDiaries(userId));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(diariesAdapter);
    }
}
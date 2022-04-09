package com.example.photodiary;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DiaryActivity extends AppCompatActivity {
    private EditText diaryTitle, diaryDesc;
    private String title, date, time, desc, path, imageName;
    private int id;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        if (intent != null) {
            id = intent.getIntExtra("id", 0);
            title = intent.getStringExtra("title");
            date = intent.getStringExtra("date");
            time = intent.getStringExtra("time");
            desc = intent.getStringExtra("desc");
            path = intent.getStringExtra("path");
            imageName = intent.getStringExtra("imageName");

        } else {
            finish();
        }


        databaseHelper = new DatabaseHelper(this);

        diaryTitle = findViewById(R.id.diaryTitle);
        diaryTitle.setText(title);
        diaryTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                title = s.toString();
                boolean update = databaseHelper.updateDiary(id, title, desc);
                if (!update) Toast.makeText(getApplicationContext(), "Updating title failed", Toast.LENGTH_SHORT).show();
            }
        });

        diaryDesc = findViewById(R.id.diaryDesc);
        diaryDesc.setText(desc);
        diaryDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                desc = s.toString();
                boolean update = databaseHelper.updateDiary(id, title, desc);
                if (!update) Toast.makeText(getApplicationContext(), "Updating description failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void delete(View view) {
        final boolean deleteDiary = databaseHelper.deleteDiary(id);
        if (deleteDiary) finish();
    }

    public void save(View view) {
        finish();
    }
}
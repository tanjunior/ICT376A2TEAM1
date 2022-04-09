package com.example.photodiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditDiaryActivity extends AppCompatActivity {
    private EditText diaryTitle, diaryDesc;
    private String title, desc;
    private int id;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        if (intent != null) {
            id = intent.getIntExtra("id", 0);
            title = intent.getStringExtra("title");
            desc = intent.getStringExtra("desc");

        } else {
            finish();
        }

        databaseHelper = new DatabaseHelper(this);

        diaryTitle = findViewById(R.id.editTitle);
        diaryDesc = findViewById(R.id.editDesc);
        diaryTitle.setText(title);
        diaryDesc.setText(desc);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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
        update();
    }

    public void update() {
        String title = diaryTitle.getText().toString();
        String desc = diaryDesc.getText().toString();
        boolean update = databaseHelper.updateDiary(id, title, desc);
        if (update) finish();
        else Toast.makeText(getApplicationContext(), "Updating description failed", Toast.LENGTH_SHORT).show();

    }
}
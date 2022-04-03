package com.example.photodiary;

import android.content.Intent;
import android.os.Bundle;

import com.example.photodiary.data.model.DiaryModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserLanding extends AppCompatActivity implements DiariesAdapter.OnDiaryClickListener {
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private int userId;
    private List<DiaryModel> diaryModelList;

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
    protected void onStart() {
        super.onStart();
        showUserDiaries();
        String name = databaseHelper.getUserById(userId).getName();
        setTitle(name);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showUserDiaries();
        String name = databaseHelper.getUserById(userId).getName();
        setTitle(name);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        showUserDiaries();
        String name = databaseHelper.getUserById(userId).getName();
        setTitle(name);
    }

    private void showUserDiaries() {
        diaryModelList = databaseHelper.getAllUserDiaries(userId);
        DiariesAdapter diariesAdapter = new DiariesAdapter(diaryModelList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(diariesAdapter);
    }

    @Override
    public void onDiaryClick(int position) {
        DiaryModel diaryModel = diaryModelList.get(position);
        Intent intent = new Intent(this, DiaryActivity.class);
        intent.putExtra("id", diaryModel.getId());
        intent.putExtra("title", diaryModel.getTitle());
        intent.putExtra("date", diaryModel.getDate());
        intent.putExtra("time", diaryModel.getTime());
        intent.putExtra("desc", diaryModel.getDescription());
        intent.putExtra("imageName", diaryModel.getFileName());
        intent.putExtra("path", diaryModel.getImageUri());
        startActivity(intent);
    }
}
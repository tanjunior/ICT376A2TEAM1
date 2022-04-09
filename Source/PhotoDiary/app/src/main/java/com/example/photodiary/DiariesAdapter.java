package com.example.photodiary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photodiary.data.model.DiaryModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class DiariesAdapter extends RecyclerView.Adapter<DiariesAdapter.MyViewHolder> {
    private final List<DiaryModel> diaryModelList;
    private final OnDiaryClickListener mOnDiaryClickListener;

    public DiariesAdapter(List<DiaryModel> diaryModelList, OnDiaryClickListener onDiaryClickListener) {
        this.diaryModelList = diaryModelList;
        this.mOnDiaryClickListener = onDiaryClickListener;
    }


    public interface OnDiaryClickListener {
        void onDiaryClick(int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView title, desc, loc, date, time;
        private final ImageView image;

        public MyViewHolder(final View view, OnDiaryClickListener onDiaryClickListener) {
            super(view);
            Button button = view.findViewById(R.id.editButton);
            title = view.findViewById(R.id.listTitle);
            image = view.findViewById(R.id.listImage);
            desc = view.findViewById(R.id.listDesc);
            loc = view.findViewById(R.id.listLoc);
            date = view.findViewById(R.id.listDate);
            time = view.findViewById(R.id.listTime);

            button.setOnClickListener(v -> onDiaryClickListener.onDiaryClick(getBindingAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public DiariesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_list_view_item, parent, false);
        return new MyViewHolder(itemView, mOnDiaryClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DiariesAdapter.MyViewHolder holder, int position) {
        DiaryModel diaryModel = diaryModelList.get(position);

        holder.title.setText(diaryModel.getTitle());
        holder.desc.setText(diaryModel.getDescription());
        holder.date.setText(diaryModel.getDate());
        holder.time.setText(diaryModel.getTime());
        holder.loc.setText(diaryModel.getLocation());

        File path = new File(diaryModel.getImageUri()+"/"+diaryModel.getFileName());
        FileInputStream fis;
        try {
            fis = new FileInputStream(path);

            final Bitmap bm = BitmapFactory.decodeStream(fis);
            holder.image.setImageBitmap(bm);

            //Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();

            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return diaryModelList.size();
    }
}

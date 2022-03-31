package com.example.photodiary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private List<DiaryModel> diaryModelList;
    public DiariesAdapter(List<DiaryModel> diaryModelList) {
        this.diaryModelList = diaryModelList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, date, time, loc, desc;
        private ImageView image;

        public MyViewHolder(final View view) {
            super(view);
            title = view.findViewById(R.id.listTitle);
            date = view.findViewById(R.id.listDate);
            time = view.findViewById(R.id.listTime);
            loc = view.findViewById(R.id.listLoc);
            desc = view.findViewById(R.id.listDesc);
            image = view.findViewById(R.id.listImage);
        }
    }

    @NonNull
    @Override
    public DiariesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_list_view_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DiariesAdapter.MyViewHolder holder, int position) {
        DiaryModel diaryModel = diaryModelList.get(position);

        holder.title.setText(diaryModel.getTitle());
        holder.date.setText(diaryModel.getDate());
        holder.time.setText(diaryModel.getTime());
        holder.loc.setText(diaryModel.getLocation());
        holder.desc.setText(diaryModel.getDescription());

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

package com.example.photodiary;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.photodiary.databinding.FragmentImageGridDialogItemBinding;
import com.example.photodiary.databinding.FragmentImageGridDialogBinding;

import java.io.File;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     ImageGridFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class ImageGridDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";
    private FragmentImageGridDialogBinding binding;

    // TODO: Customize parameters
    public static ImageGridDialogFragment newInstance(int itemCount) {
        final ImageGridDialogFragment fragment = new ImageGridDialogFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        binding = FragmentImageGridDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(new ImageAdapter(getArguments().getInt(ARG_ITEM_COUNT)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        ViewHolder(FragmentImageGridDialogItemBinding binding) {
            super(binding.getRoot());
            image = binding.image;
        }

    }

    private class ImageAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final int mItemCount;
        static final int REQUEST_IMAGE_CAPTURE = 1;

        ImageAdapter(int itemCount) {
            mItemCount = itemCount;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new ViewHolder(FragmentImageGridDialogItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position == 0) {
                holder.image.setOnClickListener(new ImageView.OnClickListener() {
                    public void onClick(View v) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        try {
                            getActivity().startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        } catch (ActivityNotFoundException e) {
                            // display error state to the user
                        }
                    }
                });
            } else {
                // place holder image
                holder.image.setImageResource(com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark);

                //TODO: set recent image from album
                //String filePath = imageFile.getPath();
                //Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                //holder.image.setImageBitmap(bitmap);


                holder.image.setOnClickListener(new ImageView.OnClickListener() {
                    public void onClick(View v) {
                        //TODO: activityForResult
                        Toast.makeText(getActivity() ,Integer.toString(holder.getBindingAdapterPosition()),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }


    }
}
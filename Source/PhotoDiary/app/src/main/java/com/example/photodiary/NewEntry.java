package com.example.photodiary;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.example.photodiary.data.model.DiaryModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class NewEntry extends AppCompatActivity implements LocationListener {

    protected LocationManager locationManager;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_GALLERY_IMAGE = 2;
    ImageListDialogFragment imageFragment = ImageListDialogFragment.newInstance(2);
    TextView titleView, dateView, timeView, locationView, descriptionView;
    Button saveButton;
    ImageView imageView;
    DialogFragment datePickerFragment, timePickerFragment;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        if (intent != null) {
            userId = intent.getIntExtra("USER_ID", 1);
        } else {
            finish();
        }

        titleView = findViewById(R.id.title);
        dateView = findViewById(R.id.date);
        timeView = findViewById(R.id.time);
        locationView = findViewById(R.id.location);
        descriptionView = findViewById(R.id.description);
        saveButton = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);


        setLocation();
        //TODO: get zone id using GPS?
        ZoneId zone = ZoneId.of("Asia/Singapore");
        LocalDateTime localDatetime = LocalDateTime.now(zone);
        setDate(localDatetime.toLocalDate());
        setTime(localDatetime.toLocalTime());

        datePickerFragment = new DatePickerFragment();
        timePickerFragment = new TimePickerFragment();

        getSupportFragmentManager().setFragmentResultListener("request_Key", this, (requestKey, bundle) -> {
            if (bundle.containsKey("date")) {
                setDate((LocalDate)bundle.getSerializable("date"));
            } else if (bundle.containsKey("time")) {
                setTime((LocalTime)bundle.getSerializable("time"));
            }
        });
    }

    public void imageSource(View view) {
        imageFragment.show(getSupportFragmentManager(), "dialog");
    }

    public void pickDate(View view) {
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void pickTime(View view) {
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "requestCode: " + requestCode + " resultCode: " + resultCode);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            imageFragment.dismiss();
        } else if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK) {
            try {
                Uri uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageFragment.dismiss();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            this.locationView.setText("Loading...");
            if (addresses != null && addresses.size() > 0) {
                String locality = addresses.get(0).getAddressLine(0);
                String country = addresses.get(0).getCountryName();
                String state = addresses.get(0).getAdminArea();
                String sub_admin = addresses.get(0).getSubAdminArea();
                String city = addresses.get(0).getFeatureName();
                String postal_code = addresses.get(0).getPostalCode();
                String locality_city = addresses.get(0).getLocality();
                String sub_locality = addresses.get(0).getSubLocality();
                if (locality != null && country != null) {
                    this.locationView.setText(locality + ", " + (sub_locality != null ? sub_locality + ", " : "")  + (locality_city != null ? locality_city + ", " : "" ) + (city != null ? city + ", " : "")  + (sub_admin != null ? sub_admin + ", " : "") + (state != null ? state + ", " : "") + country + ", " + (postal_code != null ? postal_code : ""));
                } else {
                    this.locationView.setText("Location could not be fetched...");
                }
            }
        } catch (Exception e) {
            this.locationView.setText("Location could not be fetched...");
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        } else {
            // permission is granted
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    private void setDate(LocalDate localDate) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String dateString = dateFormat.format(localDate);
        dateView = findViewById(R.id.date);
        dateView.setText(dateString);
    }

    private void setTime(LocalTime localTime) {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
        String timeString = timeFormat.format(localTime);
        timeView = findViewById(R.id.time);
        timeView.setText(timeString);
    }

    private String saveImage(String filename, Bitmap bitmapImage){
        FileOutputStream outputStream = null;
        Log.i("image","filename: "+filename);

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);


        } catch (IOException e) {
            e.printStackTrace();
            Log.i("info","exception at writeToFile ");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("info","exception at writeToFile ");
                }
            }
        }

        //shows where the file is stored in the system
        File filesDir = getFilesDir();
        Log.i("image",filesDir.getAbsolutePath());
        return filesDir.getAbsolutePath();
    }

    public void save(View view) {
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        String title = titleView.getText().toString();

        String date = dateView.getText().toString();
        String time = timeView.getText().toString();

        //convert String to LocalDate and LocalTime
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
        LocalDate localDate = LocalDate.parse(date, dateFormat);
        LocalTime localTime = LocalTime.parse(time, timeFormat);

        String loc = locationView.getText().toString();
        String desc = descriptionView.getText().toString();

        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        String filename = userId+"-"+localDate+"-"+localTime+".jpeg";
        String imagePath = saveImage(filename, bitmap);

        DiaryModel diaryModel = new DiaryModel(0, title, date, time, loc, desc, filename, imagePath, userId);
        boolean added = databaseHelper.addDiary(diaryModel);
        if (added) finish();

    }
}
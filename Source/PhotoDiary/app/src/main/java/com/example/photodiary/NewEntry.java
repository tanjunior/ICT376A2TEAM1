package com.example.photodiary;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class NewEntry extends AppCompatActivity implements LocationListener {
    protected LocationManager locationManager;
    ImageGridDialogFragment imageFragment = ImageGridDialogFragment.newInstance(30);
    TextView date, time, location;
    DialogFragment datePickerFragment, timePickerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        location = findViewById(R.id.location);




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
        if (requestCode == 1 && resultCode == -1) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageBitmap(imageBitmap);
            imageFragment.dismiss();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            this.location.setText("Loading...");
            if (addresses != null && addresses.size() > 0) {
                String locality = addresses.get(0).getAddressLine(0);
                String country = addresses.get(0).getCountryName();
                String state = addresses.get(0).getAdminArea();
                String sub_admin = addresses.get(0).getSubAdminArea();
                String city = addresses.get(0).getFeatureName();
                String pincode = addresses.get(0).getPostalCode();
                String locality_city = addresses.get(0).getLocality();
                String sub_localoty = addresses.get(0).getSubLocality();
                if (locality != null && country != null) {
                    this.location.setText(locality + ", " + (sub_localoty != null ? sub_localoty + ", " : "")  + (locality_city != null ? locality_city + ", " : "" ) + (city != null ? city + ", " : "")  + (sub_admin != null ? sub_admin + ", " : "") + (state != null ? state + ", " : "") + country + ", " + (pincode != null ? pincode : ""));
                } else {
                    this.location.setText("Location could not be fetched...");
                }
            }
        } catch (Exception e) {
            this.location.setText("Location could not be fetched...");
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }

    public void setLocation(View view) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private void setDate(LocalDate localDate) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String dateString = dateFormat.format(localDate);
        date = findViewById(R.id.date);
        date.setText(dateString);
    }

    private void setTime(LocalTime localTime) {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
        String timeString = timeFormat.format(localTime);
        time = findViewById(R.id.time);
        time.setText(timeString);
    }
}
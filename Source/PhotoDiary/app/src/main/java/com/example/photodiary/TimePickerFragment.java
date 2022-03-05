package com.example.photodiary;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import androidx.fragment.app.DialogFragment;
import java.time.LocalTime;
import java.time.ZoneId;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        ZoneId zone = ZoneId.of("Asia/Singapore");
        LocalTime localTime = LocalTime.now(zone);
        int hour = localTime.getHour();
        int minute = localTime.getMinute();

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Bundle bundle = new Bundle();
        //bundle.putString("time", LocalTime.of(hourOfDay, minute).toString());
        bundle.putSerializable("time", LocalTime.of(hourOfDay, minute));
        requireActivity().getSupportFragmentManager().setFragmentResult("request_Key", bundle);
    }
}
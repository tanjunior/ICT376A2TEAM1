package com.example.photodiary;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.time.ZoneId;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        ZoneId zone = ZoneId.of("Asia/Singapore");
        LocalDate localDate = LocalDate.now(zone);
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("date", LocalDate.of(year,month,day));
        requireActivity().getSupportFragmentManager().setFragmentResult("request_Key", bundle);
    }

}
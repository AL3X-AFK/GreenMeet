package com.alenic.greenmeet;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;


public class CreateActionFragment extends Fragment {

    private TextInputEditText etDate;
    private TextInputLayout tilDate;

    public CreateActionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_action, container, false);

        etDate = view.findViewById(R.id.etDate);
        tilDate = view.findViewById(R.id.tilDate);

        View.OnClickListener openCalendarListener = v -> showDatePicker();

        etDate.setOnClickListener(openCalendarListener);
        tilDate.setEndIconOnClickListener(openCalendarListener);

        return view;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                R.style.DatePickerTheme,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDate.setText(date);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }
}
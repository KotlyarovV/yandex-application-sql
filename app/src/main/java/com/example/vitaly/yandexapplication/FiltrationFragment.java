package com.example.vitaly.yandexapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Vitaly on 25.04.2018.
 */

public class FiltrationFragment extends DialogFragment {

    public static final String FILTRATION_MODE = "FILTRATION_MODE";
    public static final String FILTRATION_BY_MODE = "FILTRATION_BY_MODE";
    public static final String DATE_PICKER_FROM = "DATE_PICKER_FROM";
    public static final String DATE_PICKER_TO = "DATE_PICKER_TO";
    public static final String DATE_PICKER = "DATE_PICKER";
    private static final String DATE_FROM = "DATE_FROM";
    private static final String DATE_TO = "DATE_TO";

    private static SimpleDateFormat buttonDateFormat = new SimpleDateFormat("yyyy.MM.dd");

    private ConstraintLayout constraintLayout;

    private FiltrationMode filtrationMode = null;
    private SortOption filterBy = null;

    private DatePicker datePicker;
    private Button dateButtonFrom;
    private Button dateButtonTo;

    private Date dateFrom = null;
    private Date dateTo = null;

    private RadioGroup.OnCheckedChangeListener setOnRadioGroupListener() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case -1:
                        break;
                    case R.id.filter_radio_button_date:
                        filtrationMode = FiltrationMode.DATE;
                        datePicker.setVisibility(View.VISIBLE);
                        dateButtonFrom.setVisibility(View.GONE);
                        dateButtonFrom.setText("Set start of period");
                        dateFrom = null;
                        dateButtonTo.setVisibility(View.GONE);
                        dateButtonTo.setText("Set end of period");
                        dateTo = null;
                        ConstraintSet constraintSetDate = new ConstraintSet();
                        constraintSetDate.clone(constraintLayout);
                        constraintSetDate.connect(R.id.filter_radio_group_filter_mode, ConstraintSet.BOTTOM, R.id.filter_date_picker, ConstraintSet.TOP,0);
                        constraintSetDate.connect(R.id.filter_date_picker, ConstraintSet.TOP, R.id.filter_radio_group_filter_mode, ConstraintSet.BOTTOM, 0);

                        constraintSetDate.connect(R.id.filter_date_picker, ConstraintSet.BOTTOM, R.id.filter_ok_button, ConstraintSet.TOP,0);
                        constraintSetDate.connect(R.id.filter_ok_button, ConstraintSet.TOP, R.id.filter_date_picker, ConstraintSet.BOTTOM, 0);
                        constraintSetDate.applyTo(constraintLayout);

                        break;

                    case R.id.filter_radio_button_date_range:
                        filtrationMode = FiltrationMode.DATE_RANGE;
                        datePicker.setVisibility(View.GONE);
                        dateButtonFrom.setVisibility(View.VISIBLE);
                        dateButtonTo.setVisibility(View.VISIBLE);

                        ConstraintSet constraintSet = new ConstraintSet();
                        constraintSet.clone(constraintLayout);
                        constraintSet.connect(R.id.filter_radio_group_filter_mode, ConstraintSet.BOTTOM, R.id.filter_date_from, ConstraintSet.TOP,10);
                        constraintSet.connect(R.id.filter_date_from, ConstraintSet.TOP, R.id.filter_radio_group_filter_mode, ConstraintSet.BOTTOM, 0);

                        constraintSet.connect(R.id.filter_date_to, ConstraintSet.BOTTOM, R.id.filter_ok_button, ConstraintSet.TOP,0);
                        constraintSet.connect(R.id.filter_ok_button, ConstraintSet.TOP, R.id.filter_date_to, ConstraintSet.BOTTOM, 0);
                        constraintSet.applyTo(constraintLayout);

                        break;

                    default:
                        break;
                }
            }
        };
    }

    private RadioGroup.OnCheckedChangeListener setOnRadioGroupFilterByListener() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case -1:
                        break;
                    case R.id.filter_radio_button_mode_creation_date:
                        filterBy = SortOption.CREATING_DATE;
                        break;

                    case R.id.filter_radio_button_mode_edition_date:
                        filterBy = SortOption.EDITION_DATE;
                        break;

                    case R.id.filter_radio_button_mode_view_date:
                        filterBy = SortOption.VIEW_DATE;
                        break;

                    default:
                        break;
                }
            }
        };
    }




    public Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    private Button.OnClickListener setButtonDateListener(final DatePickerDialog.OnDateSetListener dateHandler) {
        return new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(getActivity(), dateHandler,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        };
    }

    DatePickerDialog.OnDateSetListener dateToHandler = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTo = new GregorianCalendar(year, monthOfYear, dayOfMonth, 59, 59, 59).getTime();
            String date = buttonDateFormat.format(dateTo);
            dateButtonTo.setText(date);
        }
    };

    DatePickerDialog.OnDateSetListener dateFromHandler = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateFrom = new GregorianCalendar(year, monthOfYear, dayOfMonth, 0, 0,0).getTime();
            String date = buttonDateFormat.format(dateFrom);
            dateButtonFrom.setText(date);
        }
    };

    private Button.OnClickListener setButtonOkListener() {
        return new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filtrationMode == null || filterBy == null) {
                    String stringText = "Please select options";
                    ((MainActivity)getActivity()).showToast(stringText);
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(FILTRATION_BY_MODE, filterBy);
                bundle.putSerializable(FILTRATION_MODE, filtrationMode);

                if (filtrationMode == FiltrationMode.DATE_RANGE) {
                    if (dateTo == null || dateFrom == null) {
                        String stringText = "Please select dates";
                        ((MainActivity)getActivity()).showToast(stringText);
                        return;
                    }
                    String dateFromStr = ListNote.format.format(dateFrom);
                    String dateToStr = ListNote.format.format(dateTo);
                    if (dateTo.compareTo(dateFrom) <= 0) {
                        ((MainActivity)getActivity()).showToast("Please set correct dates");
                        return;
                    }
                    bundle.putString(DATE_PICKER_FROM, dateFromStr);
                    bundle.putString(DATE_PICKER_TO, dateToStr);

                } else if (filtrationMode == FiltrationMode.DATE) {
                    String date = ListNote.format.format(getDateFromDatePicker(datePicker));
                    bundle.putString(DATE_PICKER, date);
                }

                backWithResult(bundle);
            }
        };
    }

    private void backWithResult(Bundle bundle) {
        Intent intent = new Intent().putExtras(bundle);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        dismiss();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filtration_fragment, container, false);

        constraintLayout = rootView.findViewById(R.id.filter_constraint_layout);
        RadioGroup radioGroupFiltrationMode = rootView.findViewById(R.id.filter_radio_group_filter_mode);
        RadioGroup radioGroupFilterByMode = rootView.findViewById(R.id.filter_radio_group_mode_filter_by);
        Button buttonOk = rootView.findViewById(R.id.filter_ok_button);
        datePicker = rootView.findViewById(R.id.filter_date_picker);
        dateButtonFrom = rootView.findViewById(R.id.filter_date_from);
        dateButtonTo = rootView.findViewById(R.id.filter_date_to);

        radioGroupFiltrationMode.setOnCheckedChangeListener(setOnRadioGroupListener());
        radioGroupFilterByMode.setOnCheckedChangeListener(setOnRadioGroupFilterByListener());
        buttonOk.setOnClickListener(setButtonOkListener());
        dateButtonTo.setOnClickListener(setButtonDateListener(dateToHandler));
        dateButtonFrom.setOnClickListener(setButtonDateListener(dateFromHandler));

        FiltrationFragment.this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(FILTRATION_MODE)) {
                filtrationMode = (FiltrationMode) savedInstanceState.getSerializable(FILTRATION_MODE);
                if (filtrationMode == FiltrationMode.DATE_RANGE) {
                    if (savedInstanceState.containsKey(DATE_FROM)) {
                        dateButtonFrom.setText(buttonDateFormat.format(dateFrom));
                    }
                    if (savedInstanceState.containsKey(DATE_TO)) {
                        dateButtonTo.setText(buttonDateFormat.format(dateTo));
                    }
                }
            }
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(FILTRATION_MODE)) {
                filtrationMode = (FiltrationMode) savedInstanceState.getSerializable(FILTRATION_MODE);
                if (filtrationMode == FiltrationMode.DATE_RANGE) {
                    if (savedInstanceState.containsKey(DATE_FROM)) dateFrom = (Date) savedInstanceState.getSerializable(DATE_FROM);
                    if (savedInstanceState.containsKey(DATE_TO)) dateTo = (Date) savedInstanceState.getSerializable(DATE_TO);
                }
            }

            if (savedInstanceState.containsKey(FILTRATION_BY_MODE))
                filterBy = (SortOption) savedInstanceState.getSerializable(FILTRATION_BY_MODE);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (filtrationMode != null)
            outState.putSerializable(FILTRATION_MODE, filtrationMode);
        if (filtrationMode != null && filtrationMode == FiltrationMode.DATE_RANGE) {
            if (dateFrom != null) outState.putSerializable(DATE_FROM, dateFrom);
            if (dateTo != null) outState.putSerializable(DATE_TO, dateTo);
        }
        if (filterBy != null) {
            outState.putSerializable(FILTRATION_BY_MODE, filterBy);
        }
    }
}

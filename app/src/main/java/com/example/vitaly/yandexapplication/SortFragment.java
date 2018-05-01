package com.example.vitaly.yandexapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by Vitaly on 24.04.2018.
 */

public class SortFragment extends DialogFragment {

    public static final String OPTION = "OPTION";
    public static final String MODE = "MODE";

    public SortOption sortOption = null;
    public SortMode sortMode = null;


    private RadioGroup.OnCheckedChangeListener createRadioGroupOptionListener() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case -1:
                        break;
                    case R.id.radioGroup_creating_date:
                        sortOption = SortOption.CREATING_DATE;
                        break;
                    case R.id.radioGroup_edition_date:
                        sortOption = SortOption.EDITION_DATE;
                        break;
                    case R.id.radioGroup_view_date:
                        sortOption = SortOption.VIEW_DATE;
                        break;
                    case R.id.radioGroup_name:
                        sortOption = SortOption.NAME;
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private RadioGroup.OnCheckedChangeListener createRadioGroupModeListener() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case -1:
                        break;
                    case R.id.button_ascending:
                        sortMode = SortMode.ASCENDING;
                        break;
                    case R.id.button_descending:
                        sortMode = SortMode.DESCENDING;
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private View.OnClickListener createButtonOkListener() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                if (sortMode == null || sortOption == null) {
                    Context context = getActivity().getApplicationContext();
                    CharSequence text = "Please choose option and mode";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(OPTION, sortOption);
                    bundle.putSerializable(MODE, sortMode);

                    Intent intent = new Intent().putExtras(bundle);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    dismiss();
                }
            }
        };
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_sort, container, false);

        RadioGroup radioGroupOption = rootView.findViewById(R.id.radioGroup_option);
        RadioGroup radioGroupMode = rootView.findViewById(R.id.radioGroup_ascending_descending);
        Button buttonOk = rootView.findViewById(R.id.dialog_fragment_ok_button);

        radioGroupOption.setOnCheckedChangeListener(createRadioGroupOptionListener());
        radioGroupMode.setOnCheckedChangeListener(createRadioGroupModeListener());
        buttonOk.setOnClickListener(createButtonOkListener());

        SortFragment.this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MODE))
                sortMode = (SortMode) savedInstanceState.getSerializable(MODE);
            if (savedInstanceState.containsKey(OPTION))
                sortOption = (SortOption) savedInstanceState.getSerializable(OPTION);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (sortMode != null)  outState.putSerializable(MODE, sortMode);
        if (sortOption != null) outState.putSerializable(OPTION, sortOption);
    }





}

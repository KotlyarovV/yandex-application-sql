package com.example.vitaly.yandexapplication;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Vitaly on 09.04.2018.
 */

public class NoteEditorFragment extends Fragment implements OnBackPressedListener {

    public static final String NODE_EDITOR_DATA = "node_editor_data";
    public static final int DELETE_RESULT = 1;

    private boolean redactionMode = false;
    private ColorProvider colorProvider;
    private ListNote note;

    private EditText descriptionEditor;
    private EditText captionEditor;
    private  TextView date;
    private Button saveButton;
    private Button deleteButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.note_editor_fragment, container, false);

        descriptionEditor = (EditText) rootView.findViewById(R.id.description_editor);
        captionEditor = (EditText) rootView.findViewById(R.id.caption_editor);
        date = (TextView) rootView.findViewById(R.id.date_editor);

        saveButton = (Button) rootView.findViewById(R.id.save);
        deleteButton = (Button) rootView.findViewById(R.id.delete);

        setOnClickListeners();

        Bundle arguments = getArguments();
        if (note == null && arguments != null && arguments.containsKey(ListFragment.REDACTED_NOTE)) {

            note = (ListNote) arguments.getSerializable(ListFragment.REDACTED_NOTE);
            setNoteViews(note);
            redactionMode = true;
            captionEditor.setVisibility(View.GONE);
            Button buttonDelete = rootView.findViewById(R.id.delete);
            buttonDelete.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).setActionBar(note.getColor(), note.getCaption());
        }
        else {
            colorProvider= new ColorProvider(getActivity().getApplicationContext());
        }

        return rootView;
    }

    private void setOnClickListeners() {
        View.OnClickListener onClickSave = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(view);
            }
        };
        saveButton.setOnClickListener(onClickSave);

        View.OnClickListener onClickDelete = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(view);
            }
        };
        deleteButton.setOnClickListener(onClickDelete);
    }

    private void setNoteViews(ListNote note) {
        descriptionEditor.setText(note.getDescription());
        captionEditor.setText(note.getCaption());

        String dateString = note.getTime() + " " + note.getDate();
        date.setText(dateString);
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Caption is empty")
                .setMessage("Please add any information in caption!")
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private boolean captionIsEmpty() {
        return captionEditor.getText().toString().trim().length() == 0;
    }

    private ListNote getNewNote() {
        String caption = captionEditor.getText().toString();
        String description = descriptionEditor.getText().toString();
        Date date = new Date();
        int color = colorProvider.getRandomColor();

        return new ListNote(color, caption, description, date);
    }

    private ListNote getRedactedNode() {
        String caption = captionEditor.getText().toString();
        String description = descriptionEditor.getText().toString();
        Date date = new Date();

        if (!(caption.equals(note.getCaption()) && description.equals(note.getDescription()))) {
            note.setCaption(caption);
            note.setDescription(description);
            note.setEditingDate(date);
        }

        note.setViewingDate(date);

        return note;
    }

    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager im =
                    (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void returnBack(ListNote listNote) {
        Intent backIntent = new Intent();

        if (redactionMode) {
            Bundle arguments = getArguments();
            int i = arguments.getInt(ListFragment.REDACTED_NOTE_NUMBER, 0);
            backIntent.putExtra(ListFragment.REDACTED_NOTE_NUMBER, i);
        }

        backIntent.putExtra(NODE_EDITOR_DATA, listNote);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(),
                        RESULT_OK, backIntent);
        hideKeyboard();
        getFragmentManager().popBackStack();
    }

    public void save(View view) {

        if (captionIsEmpty()) {
            showAlert();
            return;
        }

        ListNote listNote = (redactionMode) ? getRedactedNode() : getNewNote();
        returnBack(listNote);
    }

    public void delete(View view) {
        Bundle arguments = getArguments();
        int i = arguments.getInt(ListFragment.REDACTED_NOTE_NUMBER, 0);
        Intent backIntent = new Intent();
        backIntent.putExtra(ListFragment.REDACTED_NOTE_NUMBER, i);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(),
                        DELETE_RESULT, backIntent);
        hideKeyboard();
        getFragmentManager().popBackStack();
    }


    @Override
    public boolean needSpecialBackListener() {
        return redactionMode;
    }

    @Override
    public void onBackPressed() {
        if (redactionMode) {
            returnBack(getRedactedNode());
        }
    }

}

package com.example.vitaly.yandexapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ACCESSIBILITY_SERVICE;

/**
 * Created by Vitaly on 09.04.2018.
 */

public class ListFragment extends Fragment {

    public static final int REQUEST_CODE_CREATE_NOTE = 1;
    public static final int REQUEST_CODE_REDACT_NOTE = 2;
    public static final int REQUEST_CODE_SORT = 3;
    public static final int REQUEST_CODE_FILTER = 4;

    public static final String REDACTED_NOTE = "REDACTED_NOTE";
    public static final String REDACTED_NOTE_NUMBER = "REDACTED_NOTE_NUMBER";
    public static final String ITEMS = "ITEMS";
    public static final String SORT = "SORT";
    public static final String FILTER = "FILTER";

    public ArrayList<ListNote> notes;
    public DatabaseHelper databaseHelper;

    private MemoryHelper memoryHelper;
    private RecyclerView recyclerView;
    private ListNoteAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notes = ((MainActivity) getActivity()).notes;
        databaseHelper = ((MainActivity) getActivity()).databaseHelper;
        adapter = new ListNoteAdapter(getContext(), notes, handleItemClick(getContext()));
        memoryHelper = new MemoryHelper(notes);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_fragment, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        MainActivity activity = (MainActivity) getActivity();
        activity.setActionBar(activity.mainColor, getString(R.string.app_name));
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(false);
        recyclerView.setLayoutManager(layoutManager);
        setHasOptionsMenu(true);

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(handleAddButtonClick(getContext()));

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings_export:
                try {
                    ArrayList<ListNote> notesFromBackUp = memoryHelper.readFromBackUp();
                    this.notes.clear();
                    notes.addAll(notesFromBackUp);
                    adapter.notifyDataSetChanged();
                    databaseHelper.deleteNotes(false);
                    databaseHelper.addNotes(notes);
                    ((MainActivity)getActivity()).showToast("Notes were downloaded from backup");
                }
                catch (ParseException e){
                    ((MainActivity)getActivity()).showToast("Can't parse file.");
                }
                catch (IOException a){
                    ((MainActivity)getActivity()).showToast("Can't read backup file.");
                }
                return true;
            case R.id.action_settings_filter:
                FiltrationFragment filtrationFragment = new FiltrationFragment();
                filtrationFragment.setTargetFragment(this, REQUEST_CODE_FILTER);
                filtrationFragment.show(getFragmentManager(), FILTER);
                return true;
            case R.id.action_settings_import:
                try {
                    memoryHelper.makeBackUp();
                    ((MainActivity)getActivity()).showToast("Backup was made successfully");
                }
                catch (JSONException e){
                    ((MainActivity)getActivity()).showToast("Can't make backup. Format problem.");
                }
                catch (IOException a){
                    ((MainActivity)getActivity()).showToast("Can't write backup to file.");
                }
                return true;
            case R.id.action_settings_sort:
                SortFragment sortFragment = new SortFragment();
                sortFragment.setTargetFragment(this, REQUEST_CODE_SORT);
                sortFragment.show(getFragmentManager(), SORT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private View.OnClickListener handleItemClick(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                int i = recyclerView.getChildLayoutPosition(view);
                ListNote listNote = notes.get(i);
                NoteEditorFragment noteEditorFragment = new NoteEditorFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable(REDACTED_NOTE, listNote);
                bundle.putInt(REDACTED_NOTE_NUMBER, i);
                noteEditorFragment.setArguments(bundle);
                launchNoteEditor(noteEditorFragment, REQUEST_CODE_REDACT_NOTE);
            }
        };
    }

    private void launchNoteEditor(NoteEditorFragment noteEditorFragment, int code) {

        noteEditorFragment.setTargetFragment(ListFragment.this, code);

        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, noteEditorFragment)
                .commit();
    }

    private View.OnClickListener handleAddButtonClick(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteEditorFragment noteEditorFragment = new NoteEditorFragment();
                launchNoteEditor(noteEditorFragment, REQUEST_CODE_CREATE_NOTE);
            }
        };
    }

    public static ListFragment newInstance() {
        ListFragment myFragment = new ListFragment();
        Bundle args = new Bundle();
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_NOTE && resultCode == RESULT_OK) {
            ListNote listNote = (ListNote) data.getSerializableExtra(NoteEditorFragment.NODE_EDITOR_DATA);
            databaseHelper.addNote(listNote);
            notes.add(listNote);
            adapter.notifyDataSetChanged();
        }

        if (requestCode == REQUEST_CODE_REDACT_NOTE && resultCode == RESULT_OK) {
            ListNote listNote = (ListNote) data.getSerializableExtra(NoteEditorFragment.NODE_EDITOR_DATA);
            int i = data.getIntExtra(REDACTED_NOTE_NUMBER, 0);
            notes.set(i, listNote);
            databaseHelper.updateNote(listNote);
            adapter.notifyDataSetChanged();
        }

        if (requestCode == REQUEST_CODE_REDACT_NOTE && resultCode == NoteEditorFragment.DELETE_RESULT) {
            int i = data.getIntExtra(REDACTED_NOTE_NUMBER, 0);
            databaseHelper.deleteNote(notes.get(i));
            notes.remove(i);
            adapter.notifyDataSetChanged();
        }

        if (requestCode == REQUEST_CODE_SORT && resultCode == Activity.RESULT_OK) {
            SortMode sortMode = (SortMode) data.getSerializableExtra(SortFragment.MODE);
            SortOption sortOption = (SortOption) data.getSerializableExtra(SortFragment.OPTION);
            try {
                notes.clear();
                notes.addAll(databaseHelper.getAllSorted(sortOption, sortMode));
                adapter.notifyDataSetChanged();
            } catch (ParseException ex) {

            }
        }

        if (requestCode == REQUEST_CODE_FILTER && resultCode == RESULT_OK) {
            FiltrationMode filtrationMode = (FiltrationMode) data.getSerializableExtra(FiltrationFragment.FILTRATION_MODE);
            SortOption filterBy = (SortOption) data.getSerializableExtra(FiltrationFragment.FILTRATION_BY_MODE);
            String[] filtrationArgs = null;
            if (filtrationMode == FiltrationMode.DATE_RANGE) {
                String dateFrom = data.getStringExtra(FiltrationFragment.DATE_PICKER_FROM);
                String dateTo = data.getStringExtra(FiltrationFragment.DATE_PICKER_TO);
                filtrationArgs = new String[] { dateFrom, dateTo };
            }
            if (filtrationMode == FiltrationMode.DATE) {
                String date = data.getStringExtra(FiltrationFragment.DATE_PICKER);
                filtrationArgs = new String[] { date };
            }

            try {
                notes.clear();
                notes.addAll(databaseHelper.getAllSelected(
                        filtrationMode,
                        filterBy,
                        filtrationArgs
                ));
                adapter.notifyDataSetChanged();
            } catch (ParseException ex) {

            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ITEMS, notes);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && notes.size() == 0) {
            List<ListNote> items = (ArrayList<ListNote>) savedInstanceState.getSerializable(ITEMS);
            this.notes.addAll(items);
            adapter.notifyDataSetChanged();
        }
    }
}

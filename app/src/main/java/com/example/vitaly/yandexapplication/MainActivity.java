package com.example.vitaly.yandexapplication;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static final String NOTES = "NOTES";
    public static Integer width = null;

    public int mainColor;
    public ArrayList<ListNote> notes;
    public DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            notes = (ArrayList<ListNote>) savedInstanceState.getSerializable(NOTES);
            databaseHelper = new DatabaseHelper(this);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainColor = getResources().getColor(R.color.actionBar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            notes = new ArrayList<>();
            databaseHelper = new DatabaseHelper(this);
            try {
                List<ListNote> dataBaseNotes = databaseHelper.getAllNodes();
                notes.addAll(dataBaseNotes);
            } catch (ParseException exception){}
            ListFragment listFragment = ListFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, listFragment)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        int orientation = this.getResources().getConfiguration().orientation;
        RotatedDrawerLayout drawerLayout = (RotatedDrawerLayout) findViewById(R.id.drawer_layout);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fragment_container);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            drawerLayout.setScrimColor(getResources().getColor(R.color.drawerNoShadow));
            if (width == null) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                width = displayMetrics.widthPixels;
            }
            frameLayout.setPadding(width / 2, 0, 0, 0);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(NOTES, notes);
    }

    public void showToast(String stringText) {
        Context context = this.getApplicationContext();
        CharSequence text = stringText;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void setActionBar(int color, String title) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setBackgroundDrawable(new ColorDrawable(color));
        }
        else {
            ((AppCompatActivity)this).getSupportActionBar().setTitle(title);
            ((AppCompatActivity)this).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        if (id == R.id.nav_about) {

        } else if (id == R.id.nav_notes) {
            ListFragment listFragment = ListFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, listFragment)
                    .commit();
        } else if (id == R.id.nav_setting) {

        }

        RotatedDrawerLayout drawer = (RotatedDrawerLayout) findViewById(R.id.drawer_layout);
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;

    }



    @Override
    public void onBackPressed() {
        RotatedDrawerLayout drawer = (RotatedDrawerLayout) findViewById(R.id.drawer_layout);
        int orientation = this.getResources().getConfiguration().orientation;
        if (drawer.isDrawerOpen(GravityCompat.START) && orientation == Configuration.ORIENTATION_PORTRAIT) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        OnBackPressedListener backPressedListener = null;
        for (Fragment fragment: fm.getFragments()) {
            if (fragment instanceof  OnBackPressedListener) {
                backPressedListener = (OnBackPressedListener) fragment;
                break;
            }
        }

        if (backPressedListener != null && backPressedListener.needSpecialBackListener()) {
            backPressedListener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

}

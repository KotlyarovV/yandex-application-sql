package com.example.vitaly.yandexapplication;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import java.util.Random;

/**
 * Created by vitaly on 19.03.18.
 */

public class ColorProvider {

    public static Random random = new Random();
    public int[] colors;
    public Context context;

    public ColorProvider(Context context) {
        this.context = context;
        colors = context.getResources().getIntArray(R.array.note_colors);
    }

    public int getRandomColor() {
        return colors[random.nextInt(colors.length)];
    }

}

package com.example.vitaly.yandexapplication;

import android.graphics.Color;

import org.json.JSONException;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        System.out.print(3);
    }

    @Test
    public void checkDate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("Z");
        TimeZone t = TimeZone.getDefault();
        //sdf.setTimeZone(TimeZone.getTimeZone("+05:00"));
        sdf.setTimeZone(TimeZone.getDefault());
        System.out.print(sdf.format(new Date()));
    }

    @Test
    public void checkDate1() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("Z");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        //format.setTimeZone(TimeZone.);

        String uu = format.format(new Date());
        Date ff = format.parse("2018-04-20T22:41:24+06:00");

      //  SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ssZZ:ZZ");
        //sdf.setTimeZone(TimeZone.getDefault());
        System.out.print(format.format(ff));
    }


}
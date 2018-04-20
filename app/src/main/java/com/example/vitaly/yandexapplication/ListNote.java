package com.example.vitaly.yandexapplication;

import android.graphics.Color;
import android.graphics.Shader;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Vitaly on 17.03.2018.
 */

public class ListNote implements Serializable{

    private int color;
    private String caption;
    private String description;

    private Date creationDate;
    private Date editingDate;
    private Date viewingDate;

    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",   Locale.getDefault());
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

    public ListNote(int color, String caption, String description, Date creationDate) {
        this.color = color;
        this.caption = caption;
        this.description = description;
        this.creationDate = creationDate;
        this.editingDate = creationDate;
        this.viewingDate = creationDate;
    }

    public ListNote(
            int color,
            String caption,
            String description,
            String creationDate,
            String editingDate,
            String viewingDate
    ) throws ParseException {
        this.color = color;
        this.caption = caption;
        this.description = description;
        this.creationDate = format.parse(creationDate);
        this.editingDate = format.parse(editingDate);
        this.viewingDate = format.parse(viewingDate);
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Date getCreationDate() {
        return creationDate;
    }
    public String getCreationDateString() {
        return format.format(creationDate);
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return timeFormat.format(creationDate);
    }

    public String getDate() {
        return dateFormat.format(creationDate);
    }

    public Date getEditingDate() { return editingDate; }
    public String getEditingDateString() { return format.format(editingDate); }
    public void setEditingDate(Date editingDate) {
        this.editingDate = editingDate;
        this.viewingDate = editingDate;
    }

    public Date getViewingDate() {return viewingDate;}
    public String getViewingDateString() { return format.format(viewingDate); }
    public void setViewingDate(Date viewingDate) { this.viewingDate = viewingDate; }
}

package com.example.vitaly.yandexapplication;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Vitaly on 17.03.2018.
 */
public class ListNoteAdapter extends RecyclerView.Adapter<ListNoteAdapter.ViewHolder> {

    private List<ListNote> notes;
    private LayoutInflater layoutInflater;
    private View.OnClickListener onClickListener;

    public ListNoteAdapter(Context context, List<ListNote> notes, View.OnClickListener onClickListener) {
        this.notes = notes;
        this.layoutInflater = LayoutInflater.from(context);
        this.onClickListener = onClickListener;
    }

    @Override
    public ListNoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListNoteAdapter.ViewHolder holder, int position) {
        ListNote note = notes.get(position);
        holder.date.setText(note.getDate());
        holder.time.setText(note.getTime());
        holder.caption.setText(note.getCaption());
        holder.description.setText(note.getDescription());
        holder.color.setBackgroundColor(note.getColor());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }


    @Override
    public long getItemId(int i) {
        return i;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView color;
        final TextView caption, description, time, date;
        ViewHolder(View view){
            super(view);
            color = (ImageView)view.findViewById(R.id.color);
            caption = (TextView) view.findViewById(R.id.caption);
            description = (TextView) view.findViewById(R.id.description);
            time = (TextView) view.findViewById(R.id.time);
            date = (TextView) view.findViewById(R.id.date);

            view.setOnClickListener(onClickListener);
        }
    }

}
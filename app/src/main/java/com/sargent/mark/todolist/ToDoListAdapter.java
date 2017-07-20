package com.sargent.mark.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.SyncStateContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sargent.mark.todolist.data.Contract;
import com.sargent.mark.todolist.data.DBHelper;
import com.sargent.mark.todolist.data.ToDoItem;

import java.util.ArrayList;

/**
 * Created by mark on 7/4/17.
 */

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ItemHolder> {

    private Cursor cursor;
    private ItemClickListener listener;
    private SQLiteDatabase db;
    private String TAG = "todolistadapter";


    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item, parent, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bind(holder, position);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public interface ItemClickListener {
        void onItemClick(int pos, String description, String duedate, long id, String category);
    }

    public ToDoListAdapter(Cursor cursor, ItemClickListener listener, SQLiteDatabase db) {
        this.cursor = cursor;
        this.listener = listener;
        this.db=db;
    }

    public void swapCursor(Cursor newCursor){
        if (cursor != null) cursor.close();
        cursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }
    public interface OnCheckBoxClickedListener{
        void onCheckBoxClicked(long id, boolean checked);
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView descr;
        TextView due;
        TextView categ;
        String category;
        String duedate;
        String description;
        CheckBox checkBox;
        boolean checked;
        long id;

        ItemHolder(View view) {
            super(view);
            descr = (TextView) view.findViewById(R.id.description);
            due = (TextView) view.findViewById(R.id.dueDate);
            categ = (TextView) view.findViewById(R.id.category);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            view.setOnClickListener(this);
        }

        public void bind(ItemHolder holder, int pos) {
            cursor.moveToPosition(pos);
            id = cursor.getLong(cursor.getColumnIndex(Contract.TABLE_TODO._ID));
            Log.d(TAG, "deleting id: " + id);
            duedate = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE));
            description = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION));
            category=cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY));
            checked=cursor.getInt(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_Checked))>0;
            descr.setText(description);
            due.setText(duedate);
            categ.setText(category);
            holder.itemView.setTag(id);
            checkBox.setChecked(checked);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ContentValues cv = new ContentValues();
                    cv.put(Contract.TABLE_TODO.COLUMN_NAME_Checked, isChecked);
                    db.update(Contract.TABLE_TODO.TABLE_NAME, cv, Contract.TABLE_TODO._ID + "=" + id, null);
                }
            });
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            listener.onItemClick(pos, description, duedate, id, category);
        }
    }

}

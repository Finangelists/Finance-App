package me.finangelist.finangelist;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Marc on 05/23/2015.
 */
public class EntriesAdapter extends ArrayAdapter<String> {

    private final ArrayList<String> items;

    public EntriesAdapter(Context context, int resourceId, ArrayList<String> items) {
        super(context, resourceId, items);
        this.items = items;
    }

    public ArrayList<String> getItems() {
        return items;
    }
}
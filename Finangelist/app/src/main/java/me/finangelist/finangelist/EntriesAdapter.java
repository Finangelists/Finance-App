package me.finangelist.finangelist;

import android.content.Context;
import android.text.SpannableString;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Marc on 05/23/2015.
 */
public class EntriesAdapter extends ArrayAdapter<FinanceEntry> {


    public EntriesAdapter(Context context, int resourceId, ArrayList<FinanceEntry> items) {
        super(context, resourceId, items);
    }

}
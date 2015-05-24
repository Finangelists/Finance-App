package me.finangelist.finangelist;

import android.app.Application;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Marc on 05/23/2015.
 */
public class FinanceEntry {
    public String title;
    public BigDecimal amount;
    public boolean isExpense;
    public long objectDate;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("MM/d/yy");

    public FinanceEntry() {
    }

    public FinanceEntry(String title, BigDecimal amount, boolean isExpense, long objectDate) {
        if (title == null){
            this.title = "";
        }
        else{
            this.title = title;
        }
        this.amount = amount;
        this.isExpense = isExpense;
        this.objectDate = objectDate;
    }

    public String toSerializedString() {
        return title + "," + amount.toString() + "," + (isExpense ? "1" : "0") + "," + objectDate;
    }

    public SpannableString toSpannableString(Context context) {
        StringBuilder builder = new StringBuilder();
        int color;
        if (isExpense) {
            builder.append("-");
            color = R.color.darkRed;
        }
        else {
            builder.append("+");
            color = R.color.darkGreen;
        }
        Date date = new Date(objectDate);

        builder.append(NumberFormat.getCurrencyInstance().format(amount) + " " + title + "  " + dateFormat.format(date));

        SpannableString styledString = new SpannableString(builder.toString());
        styledString.setSpan(new ForegroundColorSpan(context.getResources().getColor(color)), 0, styledString.toString().indexOf(" "), 0);

        return styledString;
    }

    public static FinanceEntry fromString(String serializedString) {
        FinanceEntry entry =  new FinanceEntry();
        String[] splitString = serializedString.split(",");
        entry.title = splitString[0];
        entry.amount = new BigDecimal(splitString[1]);
        entry.isExpense = (splitString[2].equals("1"));
        entry.objectDate = Long.valueOf(splitString[3]);
        return entry;
    }

    public static String serializeList(ArrayList<FinanceEntry> entryList) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < entryList.size(); i++) {
            FinanceEntry entry = entryList.get(i);
            builder.append(entry.toSerializedString());
            if (entryList.size() != i -1) {
                builder.append(";");
            }
        }

        return builder.toString();
    }

    public static ArrayList<FinanceEntry> deserializeList(String serializedString) {
        ArrayList<FinanceEntry> entryList = new ArrayList<>();

        String[] splitString = serializedString.split(";");

        for (int i = 0; i < splitString.length; i++) {
            entryList.add(FinanceEntry.fromString(splitString[i]));
        }

        return entryList;
    }
}

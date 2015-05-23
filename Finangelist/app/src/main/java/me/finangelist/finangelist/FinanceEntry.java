package me.finangelist.finangelist;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by Marc on 05/23/2015.
 */
public class FinanceEntry {
    public String title;
    public BigDecimal amount;
    public boolean isExpense;

    public String toString() {
        return title + "," + amount.toString() + "," + (isExpense ? "1" : "0");
    }

    public static FinanceEntry fromString(String serializedString) {
        FinanceEntry entry =  new FinanceEntry();
        String[] splitString = serializedString.split(",");
        entry.title = splitString[0];
        entry.amount = new BigDecimal(Double.valueOf(splitString[1]));
        entry.isExpense = (splitString[2].equals("1"));
        return entry;
    }

    public static String serializeList(ArrayList<FinanceEntry> entryList) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < entryList.size(); i++) {
            FinanceEntry entry = entryList.get(i);
            builder.append(entry.toString());
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

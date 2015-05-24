package me.finangelist.finangelist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private EditText editTextMoney;
    private EditText editTextDescription;
    private Button btnSubmit;
    private ListView listViewMain;
    private Boolean textClear = false;
    private RadioButton radioButtonExpense;
    private TextView finalTotal;
    private static final String SHAREDPREFSFILEKEY = "FinanceAppSharedPrefs";
    private static final String SHAREDPREFSKEY = "EntriesKey";
    private BigDecimal netTotal = new BigDecimal(0);


    private FinanceAdapter adapter;

    private ArrayList<FinanceEntry> entries = new ArrayList<>();

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        finalTotal = (TextView) findViewById(R.id.txtTotalMoney);
        loadEntriesFromPrefs();
        editTextMoney = (EditText) findViewById(R.id.txtMoneyEntered);
        editTextDescription = (EditText) findViewById(R.id.txtDescription);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        listViewMain = (ListView) findViewById((R.id.listViewMain));
        radioButtonExpense = (RadioButton) findViewById(R.id.rbnExpense);




        adapter = new FinanceAdapter(this, android.R.layout.simple_list_item_1, entries);

        listViewMain.setAdapter(adapter);

        editTextMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!textClear) {
                    if (!s.toString().equals(current)) {
                        editTextMoney.removeTextChangedListener(this);

                        String cleanString = s.toString().replaceAll("[$,.]", "");

                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));

                        current = formatted;
                        editTextMoney.setText(formatted);
                        editTextMoney.setSelection(formatted.length());

                        editTextMoney.addTextChangedListener(this);
                    }
                }
                else
                    textClear = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //String title, BigDecimal amount, boolean isExpense, long objectDate
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(editTextMoney.getText())) {
                    return;
                }

                String description = editTextDescription.getText().toString();

                if (!TextUtils.isEmpty(description)){
                    if (!description.matches("^[a-zA-Z0-9 ]*$")){
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setMessage("Please don't use special characters");
                        alert.setNegativeButton("Ok", null);
                        alert.show();
                        return;
                    }

                }
                DecimalFormat format = new DecimalFormat();
                format.setParseBigDecimal(true);

                FinanceEntry newEntry = new FinanceEntry(editTextDescription.getText().toString(),
                       new BigDecimal(editTextMoney.getText().toString().replaceAll("[$,]", "")),
                        radioButtonExpense.isChecked(),
                        Calendar.getInstance().getTimeInMillis());
                entries.add(0, newEntry);
                //adapter.insert(newEntry, 0);
                updatePrefs();

                textClear = true;
                editTextMoney.getText().clear();
                editTextDescription.setText("");
                editTextMoney.requestFocus();

            }
        });

        listViewMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setMessage("Delete Item?");
                alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        entries.remove(position);
                        adapter.notifyDataSetChanged();
                        updatePrefs();
                    }
                });
                alert.setPositiveButton("No", null);
                alert.show();
                return true;
            }

        });

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
//        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_deleteAll) {

            entries.clear();
            updatePrefs();

            Toast.makeText(this, "Deleted", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_running_total, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    private void loadEntriesFromPrefs(){
        SharedPreferences mPrefs = getSharedPreferences(SHAREDPREFSFILEKEY, 0);
        String mString = mPrefs.getString(SHAREDPREFSKEY, null);
        if (!TextUtils.isEmpty(mString)){
            entries = FinanceEntry.deserializeList(mString);
            updateTotal();
        }

    }

    private void updatePrefs(){
        SharedPreferences mPrefs = getSharedPreferences(SHAREDPREFSFILEKEY, 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString(SHAREDPREFSKEY, FinanceEntry.serializeList(entries)).commit();
        updateTotal();
    }

    private void updateTotal(){
        netTotal = new BigDecimal(0);
        for (FinanceEntry items : entries){
            if (items.isExpense){
                netTotal = netTotal.subtract(items.amount);
            }
            else {
                netTotal = netTotal.add(items.amount);
            }

        }
        if (netTotal.toString().contains("-")){
            finalTotal.setText("-" + NumberFormat.getCurrencyInstance().format(netTotal.abs()));
            finalTotal.setTextColor(getResources().getColor(R.color.darkRed));
        }
        else {
            finalTotal.setText(NumberFormat.getCurrencyInstance().format(netTotal));
            finalTotal.setTextColor(getResources().getColor(R.color.darkGreen));
        }

    }
}

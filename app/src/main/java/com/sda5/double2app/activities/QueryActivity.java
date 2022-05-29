package com.sda5.double2app.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sda5.double2app.R;
import com.sda5.double2app.adapters.ExpenseAdapter;
import com.sda5.double2app.models.Account;
import com.sda5.double2app.models.Expense;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;

public class QueryActivity extends AppCompatActivity {

    private ExpenseAdapter expenseAdapter;
    private DatePickerDialog.OnDateSetListener mDateSetListenerFrom;
    private DatePickerDialog.OnDateSetListener mDateSetListenerTo;
    private ArrayList<Expense> expenses = new ArrayList<>();
    private ArrayList<Expense> tempExpenses = new ArrayList<>();
    private FirebaseFirestore database;
    private String accountId;
    private String currentUserId;
    private Button btnDateFrom;
    private Button btnDateTo;
    private Button btnGoToQuery;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String selectedCategory;
    private String selectedTimePeriodString;
    private RadioButton rbAllHistory;
    private ArrayList<String> catlist = new ArrayList<>();
    private ArrayList<Double> categoriesSumAmount = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        btnDateFrom = findViewById(R.id.btn_query_from);
        btnDateFrom.setEnabled(false);
        btnDateTo = findViewById(R.id.btn_query_to);
        btnDateTo.setEnabled(false);

        btnGoToQuery = findViewById(R.id.btn_query_goToPieChart);
        btnGoToQuery.setEnabled(false);

        rbAllHistory = findViewById(R.id.all_history_radioButton);
        rbAllHistory.setChecked(true);
        RadioButton rbPayHistory = findViewById(R.id.pay_history_radioButton);




        mDateSetListenerFrom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fromDate = LocalDate.of(year, month+1, dayOfMonth);
                String s = " " + dayOfMonth + " - " + (month +1)+ " - " + year;
                if(fromDate.compareTo(LocalDate.now()) > 0){
                    Toast.makeText(QueryActivity.this, "The Start date must be past", Toast.LENGTH_SHORT).show();
                    fromDate = null;
                } else {
                    btnDateFrom.setText(s);
                }

            }
        };

        mDateSetListenerTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                toDate = LocalDate.of(year, month+1, dayOfMonth);
                String s = " " + dayOfMonth + " - " + (month +1)+ " - " + year;
                if(fromDate == null){
                    Toast.makeText(QueryActivity.this, "First choose the From date", Toast.LENGTH_SHORT).show();
                    toDate = null;
                } else if (toDate.compareTo(fromDate) < 0 ){
                    Toast.makeText(QueryActivity.this, "The To date must be after From date", Toast.LENGTH_SHORT).show();
                    toDate = null;
                } else {
                    btnDateTo.setText(s);
                }
            }
        };

        catlist.add("All Categories");
        catlist.add("Grocery");
        catlist.add("Clothes");
        catlist.add("Transportation");
        catlist.add("Recurring");
        catlist.add("Eat out");
        catlist.add("Utility");
        catlist.add("Membership");
        catlist.add("Other");

        // Create spinner for user to choose the category of query
        Spinner sprCategory = findViewById(R.id.spr_query_category);
        ArrayAdapter adapterCategory = new ArrayAdapter(this, android.R.layout.simple_spinner_item, catlist);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprCategory.setAdapter(adapterCategory);
        sprCategory.setSelection(0);
        sprCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (String) parent.getItemAtPosition(position);
                System.out.println("Check");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayList<String> timeList = new ArrayList<>();
        timeList.add("Select time");
        timeList.add("Last Month");
        timeList.add("Last Two Months");
        timeList.add("Custom");

        // Create spinner for user to choose the time period of query
        Spinner sprTimePeriod = findViewById(R.id.spr_query_time);
        ArrayAdapter adapterTimePeriod = new ArrayAdapter(this, android.R.layout.simple_spinner_item, timeList);
        adapterTimePeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprTimePeriod.setAdapter(adapterTimePeriod);
        sprTimePeriod.setSelection(0);
        sprTimePeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTimePeriodString = (String) parent.getItemAtPosition(position);
                if(selectedTimePeriodString.equalsIgnoreCase("custom")){
                    btnDateFrom.setEnabled(true);
                    btnDateTo.setEnabled(true);
                } else{
                    btnDateFrom.setEnabled(false);
                    btnDateTo.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ListView listView = findViewById(R.id.expense_query_list);
        listView.setScrollingCacheEnabled(false);

        expenseAdapter = new ExpenseAdapter(this, expenses);
        listView.setAdapter(expenseAdapter);

        expenses.clear();
        resetArray(categoriesSumAmount, catlist.size());
        btnGoToQuery.setEnabled(false);
        database.collection("Accounts").whereEqualTo("userID", currentUserId).get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot accountSnapshot = task.getResult();
                        if (null != accountSnapshot) {
                            Optional<Account> account = accountSnapshot.toObjects(Account.class).stream().findFirst();
                            if (account.isPresent()) {
                                accountId = account.get().getId();
                                database.collection("Expenses")
                                        .whereArrayContains("expenseAccountIds", accountId)
                                        .get()
                                        .addOnSuccessListener(queryDocumentSnapshots -> {
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                Expense expense = documentSnapshot.toObject(Expense.class);
                                                expenses.add(expense);
                                            }
                                            for(Expense e: expenses){
                                                String c = e.getCategory();
                                                Double a = e.getAmount();
                                                for(int i = 0; i < catlist.size(); i++){
                                                    if (catlist.get(i).equalsIgnoreCase(c)){
                                                        Double newAmount = categoriesSumAmount.get(i) + a;
                                                        categoriesSumAmount.set(i, newAmount);
                                                    }
                                                }
                                            }
                                            expenseAdapter.notifyDataSetChanged();
                                            if(expenses.size() != 0){
                                                btnGoToQuery.setEnabled(true);
                                            }
                                        });
                            }
                        }
                    }
                }
        );
    }
    public void pickDateFrom(View view) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListenerFrom,
                year, month, day);
        dialog.show();
    }

    public void pickDateTo(View view) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListenerTo,
                year, month, day);
        dialog.show();
    }

    public void search(View view) {

        if(selectedTimePeriodString.equalsIgnoreCase("Last Month")){
            toDate = LocalDate.now();
            fromDate = toDate.minus(1, ChronoUnit.MONTHS);
        } else if(selectedTimePeriodString.equalsIgnoreCase("Last Two Months")){
            toDate = LocalDate.now();
            fromDate = toDate.minus(2, ChronoUnit.MONTHS);
        }

        if(fromDate == null){
            fromDate = LocalDate.parse("2000-01-01");
        }

        if(toDate == null){
            toDate = LocalDate.now();
        }

        long fromDateLong = fromDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long toDateLong = toDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        if(rbAllHistory.isChecked()){
            runQueryForAllHistory(fromDateLong, toDateLong, selectedCategory );
        } else {
            runQueryForPayer(fromDateLong, toDateLong,selectedCategory);
        }
    }

    public void runQueryForAllHistory(long fromDateLong, long toDateLong, String category){

        ListView listView = findViewById(R.id.expense_query_list);
        listView.setScrollingCacheEnabled(false);

        expenseAdapter = new ExpenseAdapter(this, expenses);
        listView.setAdapter(expenseAdapter);

        expenses.clear();
        tempExpenses.clear();
        resetArray(categoriesSumAmount, catlist.size());
        btnGoToQuery.setEnabled(false);
        database.collection("Accounts").whereEqualTo("userID", currentUserId).get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot accountSnapshot = task.getResult();
                        if (null != accountSnapshot) {
                            Optional<Account> account = accountSnapshot.toObjects(Account.class).stream().findFirst();
                            if (account.isPresent()) {
                                accountId = account.get().getId();
                                database.collection("Expenses")
                                        .whereArrayContains( "expenseAccountIds", accountId)
                                        .whereGreaterThanOrEqualTo("dateMillisec", fromDateLong)
                                        .whereLessThanOrEqualTo("dateMillisec", toDateLong)
                                        .get()
                                        .addOnSuccessListener(queryDocumentSnapshots -> {
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                Expense expense = documentSnapshot.toObject(Expense.class);
                                                tempExpenses.add(expense);
                                            }
                                            expenses.addAll(arrayBasedCategory(tempExpenses,category));
                                            for(Expense e: expenses){
                                                String c = e.getCategory();
                                                Double a = e.getAmount();
                                                for(int i = 0; i < catlist.size(); i++){
                                                    if (catlist.get(i).equalsIgnoreCase(c)){
                                                        Double newAmount = categoriesSumAmount.get(i) + a;
                                                        categoriesSumAmount.set(i, newAmount);
                                                    }
                                                }
                                            }
                                            expenseAdapter.notifyDataSetChanged();
                                            if(expenses.size() != 0){
                                                btnGoToQuery.setEnabled(true);
                                            }
                                        });

                            }
                        }
                    }
                }
        );
    }

    public void runQueryForPayer(long fromDateLong, long toDateLong, String category){
        ListView listView = findViewById(R.id.expense_query_list);
        listView.setScrollingCacheEnabled(false);

        expenseAdapter = new ExpenseAdapter(this, expenses);
        listView.setAdapter(expenseAdapter);

        expenses.clear();
        tempExpenses.clear();
        expenses.clear();
        resetArray(categoriesSumAmount, catlist.size());
        btnGoToQuery.setEnabled(false);
        database.collection("Accounts").whereEqualTo("userID", currentUserId).get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot accountSnapshot = task.getResult();
                        if (null != accountSnapshot) {
                            Optional<Account> account = accountSnapshot.toObjects(Account.class).stream().findFirst();
                            if (account.isPresent()) {
                                accountId = account.get().getId();
                                database.collection("Expenses")
                                        .whereEqualTo("payerAccountId", accountId)
                                        .whereGreaterThanOrEqualTo("dateMillisec", fromDateLong)
                                        .whereLessThanOrEqualTo("dateMillisec", toDateLong)
                                        .get()
                                        .addOnSuccessListener(queryDocumentSnapshots -> {
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                Expense expense = documentSnapshot.toObject(Expense.class);
                                                tempExpenses.add(expense);
                                            }
                                            expenses.addAll(arrayBasedCategory(tempExpenses,category));
                                            for(Expense e: expenses){
                                                String c = e.getCategory();
                                                Double a = e.getAmount();
                                                for(int i = 0; i < catlist.size(); i++){
                                                    if (catlist.get(i).equalsIgnoreCase(c)){
                                                        Double newAmount = categoriesSumAmount.get(i) + a;
                                                        categoriesSumAmount.set(i, newAmount);
                                                    }
                                                }
                                            }
                                            expenseAdapter.notifyDataSetChanged();
                                            if(expenses.size() != 0){
                                                btnGoToQuery.setEnabled(true);
                                            }
                                        });
                            }
                        }
                    }
                }
        );
    }

    public ArrayList<Expense> arrayBasedCategory(ArrayList<Expense> expenseList, String category){
        ArrayList<Expense> result = new ArrayList<>();
        if(!category.equalsIgnoreCase("all categories")){
            for(Expense expense: expenseList){
                if(expense.getCategory().equalsIgnoreCase(category)){
                    result.add(expense);
                }
            }
            return result;
        } else return expenseList;
    }

    public void gotToPieChart(View view){
        if (expenses.size() == 0) btnGoToQuery.setEnabled(false);
        Intent intent = new Intent(getApplicationContext(), com.sda5.double2app.activities.Graphs.MyPieChartActivity.class);
        intent.putStringArrayListExtra("categories", catlist);
        intent.putExtra("categoriesSumAmount", categoriesSumAmount);
        startActivity(intent);
    }

    public void resetArray(ArrayList<Double> list, int size){
        list.clear();
        for(int i = 0; i < size; i ++){
            list.add(0.0);
        }
    }
}

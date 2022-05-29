package com.sda5.double2app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sda5.double2app.R;
import com.sda5.double2app.activities.Graphs.MyBarGraph;
import com.sda5.double2app.helper.StartEndDate;
import com.sda5.double2app.models.Account;
import com.sda5.double2app.models.Expense;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SeeExpenseGraphForParticularCategory extends AppCompatActivity {


    private FirebaseFirestore database;
    private String currentUserId;
    private String accountId;
    private String categoryName;
    private ArrayList<Expense> expenses;
    private Spinner sprCategory;
    private Spinner sprTimePeriod;
    private String selectedCategory;
    private String selectedTimePeriod;
    private Integer selectedTimePeriodInteger;
    ArrayList<String> catlist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_expense_graph_for_particular_category);

        database = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        catlist.add("Grocery");
        catlist.add("Clothes");
        catlist.add("Transportation");
        catlist.add("Recurring");
        catlist.add("Eat out");
        catlist.add("Utility");
        catlist.add("Membership");
        catlist.add("Other");

        sprCategory = findViewById(R.id.query_category);
        ArrayAdapter adapterCategory = new ArrayAdapter(this, android.R.layout.simple_spinner_item, catlist);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprCategory.setAdapter(adapterCategory);
        sprCategory.setSelection(0);
        sprCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> timePeriodString = new ArrayList<>();

        timePeriodString.add("Select Time");
        timePeriodString.add("Last Month");
        timePeriodString.add("Last Three Months");
        timePeriodString.add("Last Six Months");
        timePeriodString.add("Last One Year");

        Map<String, Integer> timePeriod = new HashMap<>();

        timePeriod.put(timePeriodString.get(0), null);
        timePeriod.put(timePeriodString.get(1), 1);
        timePeriod.put(timePeriodString.get(2), 3);
        timePeriod.put(timePeriodString.get(3), 6);
        timePeriod.put(timePeriodString.get(4), 12);

        sprTimePeriod = findViewById(R.id.query_month);
        ArrayAdapter adapterTimePeriod = new ArrayAdapter(this, android.R.layout.simple_spinner_item, timePeriodString);
        adapterTimePeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprTimePeriod.setAdapter(adapterTimePeriod);
        sprTimePeriod.setSelection(0);
        sprTimePeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTimePeriod = (String) parent.getItemAtPosition(position);
                selectedTimePeriodInteger = timePeriod.get(selectedTimePeriod);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void runQuery(View view) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (selectedTimePeriodInteger == null) {
            Toast.makeText(SeeExpenseGraphForParticularCategory.this,
                    "" +
                            "Please select time period", Toast.LENGTH_SHORT).show();
        } else {
            StartEndDate startEndDate = getStartEndDate(selectedTimePeriodInteger);
            long startDate = startEndDate.getStartDate();
            long endDate = startEndDate.getEndDate();

            expenses = new ArrayList<>();
            database.collection("Accounts").whereEqualTo("userID", currentUserId).get().addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot accountSnapshot = task.getResult();
                            if (null != accountSnapshot) {
                                Optional<Account> account = accountSnapshot.toObjects(Account.class).stream().findFirst();
                                if (account.isPresent()) {
                                    accountId = account.get().getId();
                                    database.collection("Expenses")
                                            .whereEqualTo("category", selectedCategory)
                                            .whereArrayContains("expenseAccountIds", accountId)
                                            .whereGreaterThanOrEqualTo("dateMillisec", startDate)
                                            .whereLessThanOrEqualTo("dateMillisec", endDate)
                                            .orderBy("dateMillisec")
                                            .get().addOnSuccessListener(queryDocumentSnapshots -> {
                                        Map<String, Double> totalExpenseMapByMonth = new HashMap<>();
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            Expense expense = documentSnapshot.toObject(Expense.class);
                                            expense.setAmount(expense.getAmount() / expense.getExpenseAccountIds().size());
                                            expenses.add(expense);
                                            int expenseMonth = LocalDate.parse(expense.getDate(), formatter).getMonth().getValue();
                                            int expenseYear = LocalDate.parse(expense.getDate(), formatter).getYear();
                                            String key;
                                            if(expenseMonth<10){

                                                key = expenseYear + "-" + "0"+ expenseMonth;
                                            }else {
                                                key = expenseYear + "-" + expenseMonth;
                                            }
                                            Double totalAmountForMonth = totalExpenseMapByMonth.getOrDefault(key, 0.0);
                                            totalAmountForMonth += expense.getAmount();
                                            totalExpenseMapByMonth.put(key, totalAmountForMonth);
                                        }
                                        // Graph method can be called from this line with totalExpenseMapByMonth Map.
                                        Intent intent = new Intent(this, MyBarGraph.class);
                                        intent.putExtra("map", (Serializable) totalExpenseMapByMonth);
                                        intent.putExtra("category",selectedCategory);
                                        startActivity(intent);
                                        //finish();
                                        System.out.println("______________________" + totalExpenseMapByMonth.entrySet().toString());
                                    });
                                }
                            }
                        }
                    }
            );
        }

    }

    public StartEndDate getStartEndDate(Integer howManyMonths) {

        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = toDate.minus(howManyMonths, ChronoUnit.MONTHS);

        System.out.println("FROM==========" + fromDate.toString() + "TO==============" + toDate.toString());
        long fromDateLong = fromDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long toDateLong = toDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new StartEndDate(fromDateLong, toDateLong);
    }
}

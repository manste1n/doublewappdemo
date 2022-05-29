package com.sda5.double2app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sda5.double2app.R;
import com.sda5.double2app.activities.Graphs.MyBarGraphComparison;
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

public class SeeExpenseGraphForTwoActivities extends AppCompatActivity {

    private FirebaseFirestore database;
    private String currentUserId;
    private String accountId;
    private String categoryName;
    private ArrayList<Expense> expenses;
    private ArrayList<Expense> expenses2;
    private Spinner sprCategory;
    private Spinner sprCategory2;
    private Spinner sprTimePeriod;
    private Spinner sprTimePeriod2;
    private String selectedCategory;
    private String selectedCategory2;
    private String selectedTimePeriod;
    private String selectedTimePeriod2;
    private Integer selectedTimePeriodInteger;
    private Integer selectedTimePeriodInteger2;
    private Map<String, Double> totalExpenseMapByMonth2;
    private Map<String, Double> totalExpenseMapByMonth;
    ArrayList<String> catlist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_expense_graph_for_two_activities);
        database = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        catlist.add("Choose Category");
        catlist.add("Grocery");
        catlist.add("Clothes");
        catlist.add("Transportation");
        catlist.add("Recurring");
        catlist.add("Eat out");
        catlist.add("Utility");
        catlist.add("Membership");
        catlist.add("Other");

        sprCategory = findViewById(R.id.query_categoryComp1);
        ArrayAdapter adapterCategory = new ArrayAdapter(this, android.R.layout.simple_spinner_item, catlist);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprCategory.setAdapter(adapterCategory);
        sprCategory.setSelection(0);
        sprCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (String) parent.getItemAtPosition(position);
                System.out.println("SelectedCategory " + selectedCategory);
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

        sprTimePeriod = findViewById(R.id.query_month_1);
        ArrayAdapter adapterTimePeriod = new ArrayAdapter(this, android.R.layout.simple_spinner_item, timePeriodString);
        adapterTimePeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprTimePeriod.setAdapter(adapterTimePeriod);
        sprTimePeriod.setSelection(0);
        sprTimePeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTimePeriod = (String) parent.getItemAtPosition(position);
                selectedTimePeriodInteger = timePeriod.get(selectedTimePeriod);
                System.out.println("SelectedTimeperiodInteger " + selectedTimePeriodInteger);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Second category
        sprCategory2 = findViewById(R.id.query_categoryComp2);
        ArrayAdapter adapterCategory2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, catlist);
        adapterCategory2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprCategory2.setAdapter(adapterCategory2);
        sprCategory2.setSelection(0);
        sprCategory2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory2 = (String) parent.getItemAtPosition(position);
                System.out.println("SelectedCategory2 " + selectedCategory2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> timePeriodString2 = new ArrayList<>();

        timePeriodString2.add("Select Time");
        timePeriodString2.add("Last Month");
        timePeriodString2.add("Last Three Months");
        timePeriodString2.add("Last Six Months");
        timePeriodString2.add("Last One Year");

        Map<String, Integer> timePeriod2 = new HashMap<>();

        timePeriod2.put(timePeriodString2.get(0), null);
        timePeriod2.put(timePeriodString2.get(1), 1);
        timePeriod2.put(timePeriodString2.get(2), 3);
        timePeriod2.put(timePeriodString2.get(3), 6);
        timePeriod2.put(timePeriodString2.get(4), 12);

        sprTimePeriod2 = findViewById(R.id.query_month_2);
        ArrayAdapter adapterTimePeriod2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, timePeriodString2);
        adapterTimePeriod2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprTimePeriod2.setAdapter(adapterTimePeriod2);
        sprTimePeriod2.setSelection(0);
        sprTimePeriod2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTimePeriod2 = (String) parent.getItemAtPosition(position);
                selectedTimePeriodInteger2 = timePeriod2.get(selectedTimePeriod2);
                System.out.println("SelectedTimeperiodInteger2 " + selectedTimePeriodInteger2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void runQueryy(View view) {

        Intent intent = new Intent(this, MyBarGraphComparison.class);

        System.out.println("SelectedTimeperiodInteger2Querry " + selectedTimePeriodInteger);
        System.out.println("SelectedTimeperiodIntegerQuerry " + selectedTimePeriodInteger2);
        System.out.println("SelectedCategoryQuerry " + selectedCategory);
        System.out.println("SelectedCategory2Querry " + selectedCategory2);

        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (selectedTimePeriodInteger == null) {
            Toast.makeText(SeeExpenseGraphForTwoActivities.this,
                    "" +
                            "Please select time period", Toast.LENGTH_SHORT).show();
        } else if (selectedTimePeriodInteger2 == null) {
            Toast.makeText(SeeExpenseGraphForTwoActivities.this,
                    "" +
                            "Please select time period Comp2", Toast.LENGTH_SHORT).show();
        } else {
            StartEndDate startEndDate = getStartEndDate(selectedTimePeriodInteger);
            long startDate = startEndDate.getStartDate();
            long endDate = startEndDate.getEndDate();

            expenses = new ArrayList<>();
            Task<Void> queryCategory1 = database.collection("Accounts").whereEqualTo("userID", currentUserId).get()
                    .onSuccessTask(
                            accountSnapshot -> {
                                if (null != accountSnapshot) {
                                    Optional<Account> account = accountSnapshot.toObjects(Account.class).stream().findFirst();
                                    if (account.isPresent()) {
                                        accountId = account.get().getId();
                                        return database.collection("Expenses")
                                                .whereEqualTo("category", selectedCategory)
                                                .whereArrayContains("expenseAccountIds", accountId)
                                                .whereGreaterThanOrEqualTo("dateMillisec", startDate)
                                                .whereLessThanOrEqualTo("dateMillisec", endDate)
                                                .orderBy("dateMillisec")
                                                .get()
                                                .onSuccessTask(queryDocumentSnapshots -> {
                                                    totalExpenseMapByMonth = new HashMap<>();
                                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                        Expense expense = documentSnapshot.toObject(Expense.class);
                                                        expense.setAmount(expense.getAmount() / expense.getExpenseAccountIds().size());
                                                        expenses.add(expense);
                                                        int expenseMonth = LocalDate.parse(expense.getDate(), formatter1).getMonth().getValue();
                                                        int expenseYear = LocalDate.parse(expense.getDate(), formatter1).getYear();
                                                        String key;
                                                        if (expenseMonth < 10) {

                                                            key = expenseYear + "-" + "0" + expenseMonth;
                                                        } else {
                                                            key = expenseYear + "-" + expenseMonth;
                                                        }
                                                        Double totalAmountForMonth = totalExpenseMapByMonth.getOrDefault(key, 0.0);
                                                        totalAmountForMonth += expense.getAmount();
                                                        totalExpenseMapByMonth.put(key, totalAmountForMonth);
                                                    }


                                                    intent.putExtra("map", (Serializable) totalExpenseMapByMonth);
                                                    intent.putExtra("category1", selectedCategory);


                                                    System.out.println("______________________" + totalExpenseMapByMonth.entrySet().toString());
                                                    return Tasks.forResult(null);
                                                });
                                    } else {
                                        return Tasks.forException(new RuntimeException("No account"));
                                    }
                                } else {
                                    return Tasks.forException(new RuntimeException("No account"));
                                }
                            }
                    );

            startEndDate = getStartEndDate(selectedTimePeriodInteger2);
            final long startDate2 = startEndDate.getStartDate();
            final long endDate2 = startEndDate.getEndDate();
            final String startTime2 = startEndDate.getStart();// When search starts
            final String endTime2 = startEndDate.getEnd(); // When search ends

            System.out.println("startDate " + startTime2);
            System.out.println("endDate " + endTime2);

            expenses2 = new ArrayList<>();
            Task<Void> queryForCategory2 = database.collection("Accounts").whereEqualTo("userID", currentUserId).get()
                    .onSuccessTask(
                            accountSnapshot -> {
                                if (null != accountSnapshot) {
                                    Optional<Account> account = accountSnapshot.toObjects(Account.class).stream().findFirst();
                                    if (account.isPresent()) {
                                        accountId = account.get().getId();
                                        return database.collection("Expenses")
                                                .whereEqualTo("category", selectedCategory2)
                                                .whereArrayContains("expenseAccountIds", accountId)
                                                .whereGreaterThanOrEqualTo("dateMillisec", startDate2)
                                                .whereLessThanOrEqualTo("dateMillisec", endDate2)
                                                .orderBy("dateMillisec")
                                                .get()
                                                .onSuccessTask(queryDocumentSnapshots -> {
                                                    totalExpenseMapByMonth2 = new HashMap<>();
                                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                        Expense expense = documentSnapshot.toObject(Expense.class);
                                                        expense.setAmount(expense.getAmount() / expense.getExpenseAccountIds().size());
                                                        expenses2.add(expense);
                                                        int expenseMonth = LocalDate.parse(expense.getDate(), formatter2).getMonth().getValue();
                                                        int expenseYear = LocalDate.parse(expense.getDate(), formatter2).getYear();
                                                        String key;
                                                        if (expenseMonth < 10) {

                                                            key = expenseYear + "-" + "0" + expenseMonth;
                                                        } else {
                                                            key = expenseYear + "-" + expenseMonth;
                                                        }
                                                        Double totalAmountForMonth2 = totalExpenseMapByMonth2.getOrDefault(key, 0.0);
                                                        totalAmountForMonth2 += expense.getAmount();
                                                        totalExpenseMapByMonth2.put(key, totalAmountForMonth2);
                                                    }
                                                    String timeIntent = selectedTimePeriodInteger2.toString();
                                                    intent.putExtra("selectedTimePeriod", selectedTimePeriodInteger2 + "");
                                                    intent.putExtra("map2", (Serializable) totalExpenseMapByMonth2);
                                                    intent.putExtra("category2", selectedCategory2);
                                                    intent.putExtra("startDate", startTime2);
                                                    intent.putExtra("endDate", endTime2);
                                                    System.out.println("______________________" + totalExpenseMapByMonth2.entrySet().toString());
                                                    return Tasks.forResult(null);

                                                });
                                    } else {
                                        return Tasks.forException(new RuntimeException("No account"));
                                    }
                                } else {
                                    return Tasks.forException(new RuntimeException("No account"));
                                }
                            }
                    );

            Tasks.whenAll(queryCategory1, queryForCategory2)
                    .addOnSuccessListener(aVoid -> startActivity(intent));
        }


        //return totalExpenseMapByMonth2;

        //finish();

    }


    public void queryForBarplot(View view) {


        // Graph method can be called from this line with totalExpenseMapByMonth Map.
        Intent intent = new Intent(this, MyBarGraphComparison.class);
        intent.putExtra("map", (Serializable) totalExpenseMapByMonth);
        intent.putExtra("map2", (Serializable) totalExpenseMapByMonth2);
        startActivity(intent);
        //finish();
    }

    public StartEndDate getStartEndDate(Integer howManyMonths) {

        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = toDate.minus(howManyMonths, ChronoUnit.MONTHS);

        System.out.println("FROM==========" + fromDate.toString() + "TO==============" + toDate.toString());
        long fromDateLong = fromDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long toDateLong = toDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new StartEndDate(fromDateLong, toDateLong, fromDate.toString(), toDate.toString());
    }

}

package com.sda5.double2app.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sda5.double2app.R;
import com.sda5.double2app.models.Account;
import com.sda5.double2app.models.Expense;
import com.sda5.double2app.models.Group;
import com.sda5.double2app.models.Notification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    private ArrayList<Group> groups = new ArrayList<>();

    private Spinner sprCategory;
    private Spinner sprBuyer;
    private Spinner sprCurrency;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Group selectedGroup;
    private String selectedCurrency;
    private TextView btnPickDate;

    // To save on database
    private EditText etTitle;
    private EditText etAmount;
    private Button addExpenseUsers;
    private CheckBox checkBoxGroupExpense;
    private LocalDate selectedDate;
    private Long dateMillisec;
    private String selectedCategory;
    private ArrayList<String> groupMembersIds = new ArrayList<>();
    private ArrayList<String> expenseUsersId = new ArrayList<>();
    private ArrayList<String> expenseUsersName = new ArrayList<>();
    private String buyerId;
    private HashMap<String, Double> balanceOfExpense;
    private HashMap<String, Double> oldBalanceOfGroup;
    private HashMap<String, Double> balanceToUpdate;
    private double usersShare;
    private double buyerShare;
    private double rate;

    private TextToSpeech tts;
    private int languageResult;

    private Notification notification;


    // Firestore database stuff
    private FirebaseFirestore database;

    String currentUserId;
    private FirebaseAuth mAuth;
    private String groupId;

    // onRestoreInstanceState
    static String tempTitle;
    static String tempAmount;
    //    static LocalDate tempDate;
    static String tempDateS;
    static int sprCategoryDefaultItem;
    static int sprCurrencyDefaultItem;
    HashMap<String, Double> CurMap;
    static boolean isGroupExpenseChecked;
    private Boolean done = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        btnPickDate = findViewById(R.id.btnPickDate);
        addExpenseUsers = findViewById(R.id.btn_add_expense_user);
        checkBoxGroupExpense = findViewById(R.id.checkBox_group_expense);
        addExpenseUsers.setVisibility(View.GONE);

        if (btnPickDate.getText().toString().trim().isEmpty()){
            btnPickDate.setText(LocalDate.now().toString());
        }
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    languageResult = tts.setLanguage(Locale.UK);
                } else {
                    Toast.makeText(getApplicationContext(), "Feature is not supported on your Device", Toast.LENGTH_SHORT).show();
                }
            }
        });


        checkBoxGroupExpense.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isGroupExpenseChecked = true;
                    addExpenseUsers.setVisibility(View.VISIBLE);

                } else {
                    isGroupExpenseChecked = false;
                    addExpenseUsers.setVisibility(View.GONE);
                }

            }
        });


        FirebaseApp.initializeApp(this);
        database = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        groupId = getIntent().getStringExtra("group_id");
        groupMembersIds = getIntent().getStringArrayListExtra("groupMembersIds");
        expenseUsersId = getIntent().getStringArrayListExtra("expenseUsersIds");
        expenseUsersName = getIntent().getStringArrayListExtra("expenseUsersAccounts");

        etTitle = findViewById(R.id.txt_addExpense_expenseTitle);
        etAmount = findViewById(R.id.txt_addExpense_expenseAmount);

        // Create spinner for currencies
        ArrayList<String> currencies = new ArrayList<>();
        currencies.add("SEK");
        currencies.add("EUR");
        currencies.add("USD");
        currencies.add("NOK");
        currencies.add("DKK");

        sprCurrency = findViewById(R.id.sprCurrency);
        ArrayAdapter<String> adapterCurrency = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapterCurrency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprCurrency.setAdapter(adapterCurrency);
        sprCurrency.setSelection(sprCurrencyDefaultItem);
        sprCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCurrency = (String) parent.getItemAtPosition(position);
                sprCurrencyDefaultItem = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // Create sample category list for now
        // TODO update this when category is decided by team. it should retrieve data from fire store
        ArrayList<String> catlist = new ArrayList<>();
        catlist.add("Grocery");
        catlist.add("Clothes");
        catlist.add("Transportation");
        catlist.add("Recurring");
        catlist.add("Eat out");
        catlist.add("Utility");
        catlist.add("Membership");
        catlist.add("Other");

        // Create spinner for user to choose the category of expense
        sprCategory = findViewById(R.id.spr_addExpense_category);
        ArrayAdapter adapterCategory = new ArrayAdapter(this, android.R.layout.simple_spinner_item, catlist);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprCategory.setAdapter(adapterCategory);
        sprCategory.setSelection(sprCategoryDefaultItem);
        sprCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (String) parent.getItemAtPosition(position);
                sprCategoryDefaultItem = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        if (expenseUsersId != null) {
//            sprBuyer = findViewById(R.id.spr_addExpense_users);
//            ArrayAdapter adapterBuyer = new ArrayAdapter(this, android.R.layout.simple_spinner_item, expenseUsersName);
//            adapterBuyer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            adapterBuyer.notifyDataSetChanged();
//            sprBuyer.setAdapter(adapterBuyer);
//            sprBuyer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    String buyerSelected = (String) parent.getItemAtPosition(position);
//                    for (int i = 0; i < expenseUsersName.size(); i++) {
//                        if (buyerSelected == expenseUsersName.get(i)) {
//                            buyerId = expenseUsersId.get(i);
//                        }
//                    }
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//                }
//            });
//        }

        // Get the date of expense from user
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
//                String s = " " + dayOfMonth + " - " + (month + 1) + " - " + year;
                btnPickDate.setText(selectedDate.toString());
            }
        };
    }

    public void pickDate(View view) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);
        dialog.show();
    }

    public void addExpenseUsers(View view) {
        if (!etTitle.getText().toString().trim().isEmpty()) {
            tempTitle = etTitle.getText().toString();
        }
        if (!etAmount.getText().toString().trim().isEmpty()) {
            tempAmount = etAmount.getText().toString();
        }

        if (selectedDate != null) {
            tempDateS = selectedDate.toString();
        }
        Intent intent = new Intent(this, ChooseGroupForExpenseActivity.class);
        startActivity(intent);
        finish();
    }

    public void checkExpeseForPersonal(View view) {
        if (!isGroupExpenseChecked) {
            database.collection("Accounts").whereEqualTo("userID", currentUserId).limit(1).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                Account currentAccount = queryDocumentSnapshot.toObject(Account.class);
                                expenseUsersId = new ArrayList<>();
                                expenseUsersId.add(currentAccount.getId());
                                buyerId = currentAccount.getId();
                            }
                            groupId = null;
                            saveExpense();
                            finish();
                        }
                    });


        } else {
            if (groupId != null) {
                database.collection("Accounts").whereEqualTo("userID", currentUserId).limit(1).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                    Account currentAccount = queryDocumentSnapshot.toObject(Account.class);
                                    buyerId = currentAccount.getId();
                                }
                                saveExpense();
                                finish();
                            }
                        });
            } else {
                Toast.makeText(this, "There is no Group assigned", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void saveExpense() {
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
            tempDateS = selectedDate.toString();
        }

        if (etTitle.getText().toString().trim().isEmpty() ||
                etAmount.getText().toString().trim().isEmpty() ||
                selectedCategory == null ||
                selectedDate == null ||
                expenseUsersId.size() == 0 ||
                buyerId == null) {
            Toast.makeText(this, "Please enter all fields first", Toast.LENGTH_SHORT).show();
        } else {
            // Fetch data from API for getting rate for different currencies.
            exchangeRatesMap exchangeRatesMap = new exchangeRatesMap();
            CurMap = exchangeRatesMap.getCurrMap();
            rate = 1 / CurMap.get(selectedCurrency);
            String title = etTitle.getText().toString().trim();
            double amount = rate * Double.parseDouble(etAmount.getText().toString());
            String date = selectedDate.toString();
            dateMillisec = selectedDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            // creating hashmap to update balance in group collection
            if (groupId != null) {
                balanceOfExpense = new HashMap<>();

                for (String memberId : groupMembersIds) {
                    balanceOfExpense.put(memberId, 0.0);
                }

                usersShare = -amount / expenseUsersId.size();
                buyerShare = amount + usersShare;

                for (String usersId : expenseUsersId) {
                    if (usersId.equals(buyerId)) {
                        balanceOfExpense.put(usersId, buyerShare);
                    } else {
                        balanceOfExpense.put(usersId, usersShare);
                    }
                }
                final String groupName;
                // Getting existing group balance hashmap from database
                database.collection("Groups").whereEqualTo("id", groupId).limit(1).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                    Group groupToUpdate = queryDocumentSnapshot.toObject(Group.class);
                                    oldBalanceOfGroup = groupToUpdate.getBalance();
                                    balanceToUpdate = new HashMap<>(oldBalanceOfGroup);
                                    balanceOfExpense.forEach((k, v) -> balanceToUpdate.merge(k, v, (a, b) -> a + b));
                                    //update the balance
                                    database.collection("Groups").document(groupId).update("balance", balanceToUpdate);
                                    System.out.println("check");
                                }
                            }
                        });
            }

            // creating expense object
            Expense expense = new Expense(title, amount, selectedCategory, buyerId, groupId,
                    date, dateMillisec, expenseUsersId, false);

            database.collection("Expenses").document(expense.getId()).set(expense).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddExpenseActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                    sprCategory.setSelection(0);
                    sprCurrency.setSelection(0);
                    sprCategoryDefaultItem = 0;
                    sprCurrencyDefaultItem = 0;
                    selectedDate = LocalDate.now();
                    tempDateS = selectedDate.toString();
                    tempAmount = "";
                    tempTitle = "";
                    etTitle.setText(tempTitle);
                    etAmount.setText(tempAmount);
                    btnPickDate.setText(tempDateS);
                    checkBoxGroupExpense.setChecked(false);

                    database.collection("Groups").whereEqualTo("id", groupId).limit(1).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                        Group group = queryDocumentSnapshot.toObject(Group.class);
                                        expenseUsersId.remove(buyerId);
                                        for (String accountID : expenseUsersId) {

                                            database.collection("Accounts").whereEqualTo("id", accountID).limit(1).get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                                                Account account = queryDocumentSnapshot.toObject(Account.class);
                                                                String tokenId = account.getTokenID();
                                                                Notification notification;
                                                                String from = mAuth.getCurrentUser().getDisplayName().toUpperCase();
                                                                Double amount = balanceOfExpense.get(account.getId());
                                                                Long amountForNot = Math.round(amount);
                                                                String message = "Hi! You are assigned to " + title + " expense with " + amountForNot + " Kr";
                                                                String groupName = group.getName();
                                                                notification = new Notification(from, groupName, message, tokenId);
                                                                database.collection("Accounts")
                                                                        .document(account.getId()).collection("Notifications")
                                                                        .document(notification.getNotificationId())
                                                                        .set(notification)
                                                                        .addOnCompleteListener(task1 -> {

                                                                        });
                                                            }
                                                        }
                                                    });
                                        }
                                    }


                                }
                            });
                }

            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddExpenseActivity.this, "Sth is wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedDate == null) selectedDate = LocalDate.now();
        if (!etTitle.getText().toString().trim().isEmpty() || !etAmount.getText().toString().trim().isEmpty()) {
            outState.putString("Title", etTitle.getText().toString().trim());
            outState.putString("Amount", etAmount.getText().toString().trim());
            outState.putString("Date", selectedDate.toString());
            outState.putInt("CategoryPosition", sprCategoryDefaultItem);
            outState.putInt("CurrencyPosition", sprCurrencyDefaultItem);


            tempTitle = etTitle.getText().toString().trim();
            tempAmount = etAmount.getText().toString().trim();
            tempDateS = selectedDate.toString();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tempTitle = savedInstanceState.getString("Title");
        tempAmount = savedInstanceState.getString("Amount");
        tempDateS = savedInstanceState.getString("Date");
        sprCategoryDefaultItem = savedInstanceState.getInt("CategoryPosition");
        sprCurrencyDefaultItem = savedInstanceState.getInt("CurrencyPosition");
    }

    @Override
    protected void onResume() {
        super.onResume();
        etTitle.setText(tempTitle);
        etAmount.setText(tempAmount);
//        if(tempDateS != null && tempDateS != ""){
//            selectedDate = LocalDate.parse(tempDateS);
//            btnPickDate.setText(tempDateS);
//        }
        try {
            selectedDate = LocalDate.parse(tempDateS);
            btnPickDate.setText(tempDateS);
        } catch (Exception e) {

        }

        if (isGroupExpenseChecked) {
            checkBoxGroupExpense.setChecked(true);
        }
    }

    public void listenTitle(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(this, "Feature is not supported on your Device", Toast.LENGTH_LONG).show();
        }
    }

    public void listenAmount(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 2);
        } else {
            Toast.makeText(this, "Feature is not supported on your Device", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String speachResult = result.get(0);
                tempTitle = speachResult;
            }
        }

        if (requestCode == 2) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String speachResult = result.get(0);
                try {
                    Double d = Double.valueOf(speachResult);
                    tempAmount = speachResult;
                    tempTitle = etTitle.getText().toString();
                } catch (Exception e) {
                    talk("Not a valid Amount");
                    tempAmount = "";
                    tempTitle = etTitle.getText().toString();
                    Toast.makeText(this, "Not a valid amount. Try again", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void talk(String message) {
        if (languageResult == TextToSpeech.LANG_NOT_SUPPORTED || languageResult == TextToSpeech.LANG_MISSING_DATA) {
            Toast.makeText(this, "Feature is not supported on your Device", Toast.LENGTH_LONG).show();
        } else {
            tts.speak(message, 0, null, "Mehdi");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }
}

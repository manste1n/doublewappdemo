package com.sda5.double2app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sda5.double2app.R;
import com.sda5.double2app.adapters.AccountAdapter;
import com.sda5.double2app.models.Account;
import com.sda5.double2app.models.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreateNewGroupActivity extends AppCompatActivity {
    private AccountAdapter accountAdapter;
    private ArrayList<Account> accounts = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private String accountId;
    private EditText editTextExternalAccountName;
    private EditText editTextExternalAccountEmail;
    private EditText editTextExternalAccountPhone;
    private Button buttonAddExternalAccount;
    private ListView listViewExternalAccounts;
    private HashMap<String, String> externalAccountNameAndEmails = new HashMap<>();
    private ArrayList<String> externalAccountList = new ArrayList<>();
    private ArrayAdapter<String> externalUserAdapter;
    private HashMap<String, String> externalUserPhoneNumber = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);

        accounts.clear();
        externalAccountList.clear();

        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ListView listView = findViewById(R.id.account_list);
        listView.setScrollingCacheEnabled(false);

        externalUserAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                externalAccountList);

        accountAdapter = new AccountAdapter(getApplicationContext(), accounts, true);
        listView.setAdapter(accountAdapter);

        CheckBox checkBoxExternalAccount = findViewById(R.id.checkBox_external_account);
        editTextExternalAccountName = findViewById(R.id.editText_external_account_name);
        editTextExternalAccountEmail = findViewById(R.id.editText_external_account_email);
        editTextExternalAccountPhone = findViewById(R.id.editText_external_account_phone);
        buttonAddExternalAccount = findViewById(R.id.button_add_external_account);
        listViewExternalAccounts = findViewById(R.id.listView_external_accounts);

        editTextExternalAccountName.setVisibility(View.GONE);
        editTextExternalAccountEmail.setVisibility(View.GONE);
        editTextExternalAccountPhone.setVisibility(View.GONE);
        buttonAddExternalAccount.setVisibility(View.GONE);
        listViewExternalAccounts.setVisibility(View.GONE);

        checkBoxExternalAccount.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextExternalAccountName.setVisibility(View.VISIBLE);
                editTextExternalAccountEmail.setVisibility(View.VISIBLE);
                editTextExternalAccountPhone.setVisibility(View.VISIBLE);
                buttonAddExternalAccount.setVisibility(View.VISIBLE);
                listViewExternalAccounts.setVisibility(View.VISIBLE);
                listViewExternalAccounts.setAdapter(externalUserAdapter);
            } else {
                editTextExternalAccountName.setVisibility(View.GONE);
                editTextExternalAccountEmail.setVisibility(View.GONE);
                editTextExternalAccountPhone.setVisibility(View.GONE);
                buttonAddExternalAccount.setVisibility(View.GONE);
                listViewExternalAccounts.setVisibility(View.GONE);
            }
        });

        database.collection("Accounts")
                .whereEqualTo("internalAccount", true).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot accountSnapshot = task.getResult();
                        for (QueryDocumentSnapshot doc : accountSnapshot) {
                            Account account = doc.toObject(Account.class);
                            if (!account.getUserID().equals(mAuth.getUid())) {
                                accounts.add(account);
                            }
                        }
                        accountAdapter.notifyDataSetChanged();
                    }
                });
    }

    public void saveNewGroup(View view) {
        String userID = mAuth.getCurrentUser().getUid();

        Task<Void> currentUsersAccountRetrieverTask = database.collection("Accounts")
                .whereEqualTo("userID", userID)
                .get()
                .onSuccessTask(this::addCurrentUsersAccountToList);

        List<Task<Void>> externalAccountRetrieverTasks = new ArrayList<>();
        for (final String email : externalAccountNameAndEmails.keySet()) {
            String ownerName = externalAccountNameAndEmails.get(email);
            Task<Void> externalAccountRetrieverTask = getExternalAccountRetrieverTask(ownerName, email);
            externalAccountRetrieverTasks.add(externalAccountRetrieverTask);
        }

        List<Task<Void>> allTasks = Stream.concat(externalAccountRetrieverTasks.stream(), Stream.of(currentUsersAccountRetrieverTask))
                .collect(Collectors.toList());

        Tasks.whenAll(allTasks)
                .continueWithTask(this::persistGroup)
                .addOnSuccessListener(this::goToGroupPage);
    }

    private Task<Void> addCurrentUsersAccountToList(QuerySnapshot accountSnapshots) {
        if (null != accountSnapshots) {
            Optional<Account> account = accountSnapshots.toObjects(Account.class).stream().findFirst();
            if (account.isPresent()) {
                accountId = account.get().getId();
                return Tasks.forResult(null);
            }
        }
        return Tasks.forException(new Exception("User's account doesn't exist"));
    }

    private Task<Void> getExternalAccountRetrieverTask(String ownerName, String email) {
        return database.collection("Accounts")
                .whereEqualTo("email", email).get()
                .onSuccessTask(task -> getOrCreateExternalAccount(ownerName, email, task));
    }

    private Task<Void> getOrCreateExternalAccount(String ownerName, String email, QuerySnapshot accountSnapshot) {
        Optional<Account> accountOptional = accountSnapshot.toObjects(Account.class).stream().findFirst();
        if (accountOptional.isPresent()) {
            accountAdapter.addSelectedAccountId(accountOptional.get().getId());
            Toast.makeText(getApplicationContext(), "Email already exists in the App. Name will be" + " "
                    + accountOptional.get().getOwnerName(), Toast.LENGTH_LONG).show();
            return Tasks.forResult(null);
        } else {
            final Account externalAccount = new Account(false, ownerName, email);
            externalAccount.setPhoneNumber( externalUserPhoneNumber.get(email));
            return database.collection("Accounts")
                    .document(externalAccount.getId())
                    .set(externalAccount)
                    .addOnCompleteListener(task11 -> {
                        if (task11.isSuccessful()) {
                            accountAdapter.addSelectedAccountId(externalAccount.getId());
                        }
                    });
        }
    }

    private void goToGroupPage(Void aVoid) {
        Intent intent = new Intent(CreateNewGroupActivity.this, ServiceActivity.class);
        startActivity(intent);
    }

    private Task<Void> persistGroup(Task<Void> voidTask) {
        String groupName = ((EditText) findViewById(R.id.et_group_name)).getText().toString();
        if (!groupName.equals("") && accountAdapter.getSelectedAccountIDList().size() != 0) {
            accountAdapter.addSelectedAccountId(accountId);
            Group group = new Group(groupName, accountAdapter.getSelectedAccountIDList(), accountId);
            return database.collection("Groups").document(group.getId()).set(group);
        } else {
            Toast.makeText(CreateNewGroupActivity.this, "Please enter the group name and select the members", Toast.LENGTH_SHORT).show();
            throw new RuntimeException("Enter all fields");
            //return voidTask;
        }
    }

    public void addExternalAccount(View view) {
        String externalAccountName = editTextExternalAccountName.getText().toString();
        String externalAccountEmail = editTextExternalAccountEmail.getText().toString();
        String externalAccountPhone = editTextExternalAccountPhone.getText().toString();
        if (externalAccountEmail.equals("") || externalAccountName.equals("")) {
            Toast.makeText(this, "Please fill all name and email", Toast.LENGTH_SHORT).show();

        } else {
            externalAccountNameAndEmails.put(externalAccountEmail, externalAccountName);
            externalAccountList.add("Name:  " + externalAccountName + "\n" + "Email:  "
                    + externalAccountEmail + "\n" + "Phone: " + externalAccountPhone);
            externalUserAdapter.notifyDataSetChanged();
            editTextExternalAccountName.setText("");
            editTextExternalAccountEmail.setText("");
            externalUserPhoneNumber.put(externalAccountEmail,editTextExternalAccountPhone.getText().toString());
            editTextExternalAccountPhone.setText("");
        }
    }
}

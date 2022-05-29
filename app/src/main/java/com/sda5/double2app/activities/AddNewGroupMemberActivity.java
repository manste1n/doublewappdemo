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
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.Map;
import java.util.Optional;

public class AddNewGroupMemberActivity extends AppCompatActivity {
    private AccountAdapter accountAdapter;
    private ArrayList<Account> accounts = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private String groupID;
    private Group group;
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
        setContentView(R.layout.activity_add_new_group_member_activity);

        ListView listView = findViewById(R.id.account_list);
        listView.setScrollingCacheEnabled(false);

        accounts.clear();
        externalAccountList.clear();

        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        groupID = intent.getStringExtra("group_id");

        externalUserAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                externalAccountList);

        accountAdapter = new AccountAdapter(getApplicationContext(), accounts, true);
        listView.setAdapter(accountAdapter);

        CheckBox checkBoxExternalAccount = findViewById(R.id.cb_external_account_add_member);
        editTextExternalAccountName = findViewById(R.id.et_external_name_add_member);
        editTextExternalAccountEmail = findViewById(R.id.et_external_email_add_member);
        editTextExternalAccountPhone = findViewById(R.id.et_external_phone_add_member);
        buttonAddExternalAccount = findViewById(R.id.button_add_external_add_member);
        listViewExternalAccounts = findViewById(R.id.listView_external_add_member);

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

        database.collection("Groups")
                .document(groupID).get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot groupSnapshot = task.getResult();
                    group = groupSnapshot.toObject(Group.class);

                    database.collection("Accounts")
                            .addSnapshotListener((value, e) -> {
                                for (QueryDocumentSnapshot doc : value) {
                                    Account account = doc.toObject(Account.class);
                                    if (account.isInternalAccount()) {
                                        if (!account.getUserID().equals(mAuth.getUid())) {
                                            if (!group.getAccountIdList().contains(account.getId())) {
                                                accounts.add(account);
                                            }
                                        }
                                    }
                                }
                                accountAdapter.notifyDataSetChanged();
                            });
                });
    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        accounts.clear();
//        accountAdapter.notifyDataSetChanged();
//    }

    public void addMembersToGroup(View view) {

        List<Task<Void>> externalAccountRetrieverTasks = new ArrayList<>();
        for (final String email : externalAccountNameAndEmails.keySet()) {
            String ownerName = externalAccountNameAndEmails.get(email);
            Task<Void> externalAccountRetrieverTask = getExternalAccountRetrieverTask(ownerName, email);
            externalAccountRetrieverTasks.add(externalAccountRetrieverTask);
        }

        Tasks.whenAll(externalAccountRetrieverTasks)
                .continueWithTask(this::persistGroup)
                .addOnSuccessListener(this::goToGroupDetailPage);
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
            externalAccount.setPhoneNumber(externalUserPhoneNumber.get(email));
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

    private Task<Void> persistGroup(Task<Void> voidTask) {
        Map<String, Double> groupBalance = group.getBalance();
//        TODO Something we can do here I think the duplicate may happen here
        group.getAccountIdList().addAll(accountAdapter.getSelectedAccountIDList());
//        for(String newOne: accountAdapter.getSelectedAccountIDList()){
//            if(!group.getAccountIdList().contains(newOne)){
//                group.getAccountIdList().add(newOne);
//            }
//        }


        for (String accountID : accountAdapter.getSelectedAccountIDList()) {
            groupBalance.put(accountID, 0.0);
        }

        return database.collection("Groups")
                .document(groupID)
                .update("accountIdList", group.getAccountIdList())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        database.collection("Groups")
                                .document(groupID)
                                .update("balance", groupBalance);
                        accountAdapter.notifyDataSetChanged();
                        Toast.makeText(AddNewGroupMemberActivity.this, "Members are added successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void goToGroupDetailPage(Void avoid) {
        Intent intent = new Intent(AddNewGroupMemberActivity.this, GroupDetailActivity.class);
        intent.putExtra("group_id", groupID);
        startActivity(intent);

    }

    public void addExternalMembersToListView(View view) {
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
            externalUserPhoneNumber.put(externalAccountEmail, editTextExternalAccountPhone.getText().toString());
            editTextExternalAccountPhone.setText("");
        }
    }
}

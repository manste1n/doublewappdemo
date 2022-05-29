package com.sda5.double2app.activities;

import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sda5.double2app.R;
import com.sda5.double2app.adapters.AccountAdapterAddExpense;
import com.sda5.double2app.models.Account;
import com.sda5.double2app.models.Group;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddExpenseUsersActivity extends AppCompatActivity {

    private AccountAdapterAddExpense accountAdapterAddExpense;
    private ArrayList<String> groupMembersIds = new ArrayList<>();
    private ArrayList<Account> accountsForExpense = new ArrayList<>();
    private ArrayList<String> expenseUsersId = new ArrayList<>();
    private ArrayList<String> expenseUsersName = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseFirestore database;
    String currentUserId;
    String groupID;
    Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense_users);
        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        accountsForExpense.clear();

        Intent intent = getIntent();
        groupID = intent.getStringExtra("group_id");

        currentUserId = mAuth.getCurrentUser().getUid();

        ListView listView = findViewById(R.id.expense_users_list);
        listView.setScrollingCacheEnabled(false);

        accountAdapterAddExpense = new AccountAdapterAddExpense(getApplicationContext(), accountsForExpense, true);
        listView.setAdapter(accountAdapterAddExpense);


        database.collection("Groups")
                .document(groupID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot groupSnapshot = task.getResult();
                        group = groupSnapshot.toObject(Group.class);
                        groupMembersIds = (ArrayList<String>) group.getAccountIdList();

                        database.collection("Accounts")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value,
                                                        @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            return;
                                        }
                                        for (QueryDocumentSnapshot doc : value) {
                                            Account account = doc.toObject(Account.class);
                                                if (group.getAccountIdList().contains(account.getId())) {
                                                    if(!accountsForExpense.contains(account)){
                                                        accountsForExpense.add(account);
                                                    }
                                                }
                                        }
                                        accountAdapterAddExpense.notifyDataSetChanged();
                                    }
                                });
                    }
                });
    }

    public void addExpenseUsers(View view) {
        expenseUsersId.addAll(accountAdapterAddExpense.getSelectedExpenseUsersIDList());
        expenseUsersName.addAll(accountAdapterAddExpense.getSelectedExpenseUsersNameList());
        Intent intent = new Intent(AddExpenseUsersActivity.this, AddExpenseActivity.class);
        intent.putExtra("group_id", groupID);
        intent.putStringArrayListExtra("groupMembersIds", groupMembersIds);
        intent.putStringArrayListExtra("expenseUsersIds", expenseUsersId);
        intent.putStringArrayListExtra("expenseUsersAccounts", expenseUsersName);
        startActivity(intent);
        finish();
    }
}

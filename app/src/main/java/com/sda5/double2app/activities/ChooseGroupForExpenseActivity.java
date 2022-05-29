package com.sda5.double2app.activities;

import android.os.Bundle;
import android.widget.ListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sda5.double2app.R;
import com.sda5.double2app.adapters.GroupAdapterAddExpense;
import com.sda5.double2app.models.Account;
import com.sda5.double2app.models.Group;

import java.util.ArrayList;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ChooseGroupForExpenseActivity extends AppCompatActivity {

    private GroupAdapterAddExpense groupAdapterAddExpense;
    private ArrayList<Group> groups = new ArrayList<>();
    FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private String accountId;
    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_group_for_expense);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        ListView listView = findViewById(R.id.add_expense_group_list);
        listView.setScrollingCacheEnabled(false);

        groupAdapterAddExpense = new GroupAdapterAddExpense(this, groups);
        listView.setAdapter(groupAdapterAddExpense);

        database.collection("Accounts").whereEqualTo("userID", currentUserId).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot accountSnapshot = task.getResult();
                            if (null != accountSnapshot) {
                                Optional<Account> account = accountSnapshot.toObjects(Account.class).stream().findFirst();
                                if (account.isPresent()) {
                                    accountId = account.get().getId();
                                    database.collection("Groups").whereArrayContains("accountIdList", accountId)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                        Group group = documentSnapshot.toObject(Group.class);
                                                        groups.add(group);
                                                    }
                                                    groupAdapterAddExpense.notifyDataSetChanged();
                                                }
                                            });

                                } else {

                                }
                            }
                        }
                    }
                }
        );
    }
}

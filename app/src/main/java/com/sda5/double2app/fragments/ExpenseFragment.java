package com.sda5.double2app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sda5.double2app.R;
import com.sda5.double2app.adapters.ExpenseAdapter;
import com.sda5.double2app.models.Account;
import com.sda5.double2app.models.Expense;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Optional;

public class ExpenseFragment extends Fragment {
    private ExpenseAdapter expenseAdapter;
    private ArrayList<Expense> expenses = new ArrayList<>();
    FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private String accountId;
    String currentUserId;
    Integer i;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_expense, null);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        i = 0;

        ListView listView = v.findViewById(R.id.expense_list);
        listView.setScrollingCacheEnabled(false);

        expenseAdapter = new ExpenseAdapter(v.getContext(), expenses);
        listView.setAdapter(expenseAdapter);

        expenses.clear();
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
                                        .orderBy("logDate", Query.Direction.DESCENDING)
                                        .get()
                                        .addOnSuccessListener(queryDocumentSnapshots -> {
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                Expense expense = documentSnapshot.toObject(Expense.class);
                                                expenses.add(expense);
                                            }
                                            expenseAdapter.notifyDataSetChanged();
                                        });
                            }
                        }
                    }
                }
        );
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (i != 0 ){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
        i = 1;
    }
}

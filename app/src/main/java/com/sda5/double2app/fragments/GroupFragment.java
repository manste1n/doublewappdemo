package com.sda5.double2app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sda5.double2app.R;
import com.sda5.double2app.adapters.GroupAdapter;
import com.sda5.double2app.models.Account;
import com.sda5.double2app.models.Group;

import java.util.ArrayList;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GroupFragment extends Fragment {
    private GroupAdapter groupAdapter;
    private ArrayList<Group> groups = new ArrayList<>();
    FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private String accountId;
    String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group, null);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        ListView listView = v.findViewById(R.id.group_list);
        listView.setScrollingCacheEnabled(false);

        groupAdapter = new GroupAdapter(v.getContext(), groups);
        listView.setAdapter(groupAdapter);

        database.collection("Accounts").whereEqualTo("userID", currentUserId).get().addOnCompleteListener(task->{
            if (task.isSuccessful()) {
                QuerySnapshot accountSnapshot = task.getResult();
                if (null != accountSnapshot) {
                    Optional<Account> account = accountSnapshot.toObjects(Account.class).stream().findFirst();
                    if (account.isPresent()) {
                        accountId = account.get().getId();
                        database.collection("Groups").whereArrayContains("accountIdList", accountId).get().addOnSuccessListener(queryDocumentSnapshots->{
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Group group = documentSnapshot.toObject(Group.class);
                                groups.add(group);
                            }
                            groupAdapter.notifyDataSetChanged();
                        });
                    }
                }
            }
        });

        return v;
    }
}

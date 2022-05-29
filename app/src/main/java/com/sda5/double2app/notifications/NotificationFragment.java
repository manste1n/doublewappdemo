package com.sda5.double2app.notifications;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sda5.double2app.R;
import com.sda5.double2app.models.Account;
import com.sda5.double2app.models.Notification;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */


public class NotificationFragment extends Fragment {

    private List<Notification> notificationList;

    private static final String TAG = "FireLog";
    private RecyclerView mNotificationList;
    private NotificationRecyclerViewAdapter notificationRecyclerViewAdapter;

    FirebaseAuth auth;

    String User_id;

    FirebaseFirestore databaseReference = FirebaseFirestore.getInstance();

    private FirebaseFirestore mFirestore;

    // Constructor
    public NotificationFragment() {

    }


    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        auth = FirebaseAuth.getInstance();
        User_id = auth.getCurrentUser().getUid();

        // ArrayList initialized
        notificationList = new ArrayList<>();

        // Adapter initialized
        notificationRecyclerViewAdapter = new NotificationRecyclerViewAdapter(notificationList, getContext());

        // Recycler view
        mNotificationList = (RecyclerView) view.findViewById(R.id.notification_list);
        mNotificationList.setHasFixedSize(true);
        mNotificationList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mNotificationList.setAdapter(notificationRecyclerViewAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        final String current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String current_user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        //String current_user_display_name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        //String appUserId = mFirestore.getApp().toString();

        mFirestore.collection("Accounts").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (!queryDocumentSnapshots.isEmpty()) {

                    System.out.println("current user id" + current_user_id);

                    List<DocumentSnapshot> list1 = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot documentSnapshot1 : list1) {

                        Account account = documentSnapshot1.toObject(Account.class);

                        String remoteID = documentSnapshot1.getString("id");
                        Log.d(TAG,"remoteID " + remoteID);
                        //System.out.println("iddd: " + iddd);
                        String remoteUserEmail = documentSnapshot1.getString("email");

                        System.out.println("remoteUserEmail: " + remoteUserEmail);
                        // not fetching value properly hence forced matched to my id

                        if (remoteUserEmail.equals(current_user_email)) {
                            String appUser2 = remoteID;
                            System.out.println("appUser2: " + appUser2);


                            mFirestore.collection("Accounts").document(appUser2).collection("Notifications")
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                            if (e != null || queryDocumentSnapshots.size() == 0) {
                                                Toast.makeText(container.getContext(), "User not found", Toast.LENGTH_SHORT).show();
                                                //Log.d(TAG, "FirebaseError: " + e.getMessage());
                                            }
                                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {


                                                Notification notification = doc.getDocument().toObject(Notification.class);

                                                String userNames = doc.getDocument().getString("from");
                                                Log.d(TAG, "usersFire= " + userNames);
                                                notificationList.add(notification);
                                                notificationRecyclerViewAdapter.notifyDataSetChanged();
                                                Log.d(TAG, "remoteNotification" + notification.getFrom());

                                            }
                                        }
                                    });
                          //  Toast.makeText(container.getContext(), "User_ID: " + current_user_email, Toast.LENGTH_LONG).show();


                        }
                    }
                }
            }
        });
        return view;
    }
}


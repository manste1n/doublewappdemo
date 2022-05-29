package com.sda5.double2app.activities;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sda5.double2app.GMailSender;
import com.sda5.double2app.R;
import com.sda5.double2app.adapters.AccountAdapterGroupDetail;
import com.sda5.double2app.models.Account;
import com.sda5.double2app.models.Group;
import com.sda5.double2app.models.Notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GroupDetailActivity extends AppCompatActivity {
    private String groupID;
    private FirebaseFirestore database;
    private String currentUserId;
    private String accountId;
    private AccountAdapterGroupDetail accountAdapter;
    private Group group;
    private ArrayList<Account> accounts = new ArrayList<>();
    private ImageView btnAddMember;
    private TextView txtAddMember;

    private ImageView btnDeleteMember;
    private TextView txtDeleteMember;
    private ImageView btnDeleteGroup;
    private TextView txtDeleteGroup;

    private Button btnLeaveGroup;
    private ImageView btnSettle;
    private TextView txtSettle;
    private Account currentAccount;
    private final static int SEND_SMS_PERMISSION_REQ=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        database = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        if(!checkPermission(Manifest.permission.SEND_SMS)) {
            ActivityCompat.requestPermissions(GroupDetailActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);
        }

        Intent intent = getIntent();
        groupID = intent.getStringExtra("group_id");


        final ListView listView = findViewById(R.id.member_list);
        listView.setScrollingCacheEnabled(false);


        btnAddMember = findViewById(R.id.btn_add_member);
        btnDeleteMember = findViewById(R.id.btn_delete_member);
        btnDeleteGroup = findViewById(R.id.btn_delete_group);
        btnLeaveGroup = findViewById(R.id.btn_leave_group);
        btnSettle = findViewById(R.id.btn_settle);

        txtAddMember = findViewById(R.id.txt_add_member);
        txtDeleteMember = findViewById(R.id.txt_delete_group_member);
        txtDeleteGroup = findViewById(R.id.txt_delete_group);
        txtSettle = findViewById(R.id.txt_settle);

//        if(!checkPermission(Manifest.permission.SEND_SMS)) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);
//        }

        database.collection("Groups")
                .whereEqualTo("id", groupID)
                .addSnapshotListener((value, e) -> {
                    if (null != value) {
                        Optional<Group> groupOptional = value.toObjects(Group.class).stream().findAny();
                        if (groupOptional.isPresent()) {
                            group = groupOptional.get();
                            accountAdapter = new AccountAdapterGroupDetail(getApplicationContext(), group, accounts, isGroupAdmin());
                            listView.setAdapter(accountAdapter);

                            if (!group.getAdminUserId().equals(currentUserId)) {
                                btnSettle.setVisibility(View.GONE);
                                btnAddMember.setVisibility(View.GONE);
                                btnDeleteMember.setVisibility(View.GONE);
                                btnDeleteGroup.setVisibility(View.GONE);
                                txtSettle.setVisibility(View.GONE);
                                txtAddMember.setVisibility(View.GONE);
                                txtDeleteMember.setVisibility(View.GONE);
                                txtDeleteGroup.setVisibility(View.GONE);
                            } else {
                                btnLeaveGroup.setVisibility(View.GONE);

                            }
                            CollectionReference accountRef = database.collection("Accounts");
                            for (String accountId : group.getAccountIdList()) {
                                accountRef
                                        .whereEqualTo("id", accountId)
                                        .get()
                                        .addOnSuccessListener(queryDocumentSnapshots -> {
                                            if(!accounts.contains(queryDocumentSnapshots.toObjects(Account.class).get(0))){
                                                accounts.add(queryDocumentSnapshots.toObjects(Account.class).get(0));
                                            }
                                            accountAdapter.notifyDataSetChanged();
                                        });
                            }
                        }
                    }
                });

        /***Goes Account collection and finds current user's account id*/

        database.collection("Accounts").whereEqualTo("userID", currentUserId).get().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot accountSnapshot = task.getResult();
                        if (null != accountSnapshot) {
                            Optional<Account> account = accountSnapshot.toObjects(Account.class).stream().findFirst();
                            if (account.isPresent()) {
                                currentAccount = account.get();
                                accountId = account.get().getId();
                            }
                        }
                    }
                });


    }

    private boolean isGroupAdmin() {
        return group.getAdminUserId().equals(currentUserId);
    }

    public void leaveGroup(View view) {
        HashMap<String, Double> groupBalance = group.getBalance();
        Double balanceForCurrentUser = groupBalance.get(accountId);
        if (balanceForCurrentUser != 0) {
            Toast.makeText(GroupDetailActivity.this, "You are not allowed to leave group since you have a balance. ",
                    Toast.LENGTH_SHORT).show();

        } else {
            group.getAccountIdList().remove(accountId);
            database.collection("Groups").document(groupID)
                    .update("accountIdList", group.getAccountIdList())
                    .addOnCompleteListener(task -> {
                        Toast.makeText(GroupDetailActivity.this, "You left the group",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(GroupDetailActivity.this, ServiceActivity.class));
                    });
        }
    }


    public void addMember(View view) {
        Intent intent = new Intent(this, AddNewGroupMemberActivity.class);
        intent.putExtra("group_id", groupID);
        startActivity(intent);
        finish();
    }

    public void deleteMembers(View view) {
        Map<String, Double> groupBalance = group.getBalance();
        for (String accountID : accountAdapter.getSelectedAccountIDList()) {
            groupBalance.remove(accountID);
        }

        List<String> accountIDsToBeDeleted = accountAdapter.getSelectedAccountIDList();
        for (String accountId : accountIDsToBeDeleted) {
            accounts.remove(accountId);
            accountAdapter.notifyDataSetChanged();
            group.getAccountIdList().remove(accountId);
            groupBalance.remove(accountId);
        }

        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("accountIdList", group.getAccountIdList());
        updateFields.put("balance", groupBalance);

        database.collection("Groups").document(groupID)
                .update(updateFields)
                .addOnCompleteListener(task -> {

                    startActivity(getIntent());
                    finish();
                });
    }

    public void deleteGroup(View view) {
        database.collection("Groups")
                .document(groupID)
                .delete()
                .addOnCompleteListener(task -> {
                    Toast.makeText(GroupDetailActivity.this,
                            "Group is deleted successfully",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(GroupDetailActivity.this, ServiceActivity.class));

                });
    }

    public void settleTheGroupExpenses(View view) {
        HashMap<String, Double> previousGroupBalance = new HashMap<>(group.getBalance());
        HashMap<String, Double> groupBalance = group.getBalance();
        groupBalance.forEach((key, value) -> groupBalance.put(key, 0.0));
        database.collection("Groups")
                .document(groupID)
                .update("balance", groupBalance)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String from;
                        String tokenId;
                        String message;
                        String amount;
                        String groupName = group.getName();
                        Notification notification;
                        List<Account> externalAccounts = new ArrayList<>();
                        Map<Account, String> balanceStatus = new HashMap<>();
                        for (Account account : accounts) {
                            long amountL = Math.round(previousGroupBalance.get(account.getId()));
                            amount = Long.toString(amountL);
                            balanceStatus.put(account, amount);
                            if (account.isInternalAccount()) {
                                from = currentAccount.getOwnerName().toUpperCase();
                                message = "Hi! You owe " + amount + "Kr for settlement of expenses of group : " + groupName;
                                tokenId = account.getTokenID();
                                notification = new Notification(from, groupName, message, tokenId);
                                database.collection("Accounts")
                                        .document(account.getId()).collection("Notifications")
                                        .document(notification.getNotificationId())
                                        .set(notification)
                                        .addOnCompleteListener(task1 -> {

                                        });
                            } else if (previousGroupBalance.get(account.getId()) != 0) {
                                externalAccounts.add(account);
                            }
                        }

                        if (externalAccounts.size() > 0) {
                            boolean successfulSendMail = true;
                            boolean successfulSendSMS= true;
                            boolean atLestOneSms = false;
                            int numberOfSuccessfulSMS = 0 ;
                            for (Account account : externalAccounts) {
                                String ownerName = account.getOwnerName();
                                String amountForPerson = balanceStatus.get(account);
                                String subject = "WalletDroid settlement detail for group: "+groupName;
                                String messageContentIndividual ="Hi "+ownerName+"! Your balance is "+amountForPerson+" Kr in group: "+groupName;
                                // you can call sendEmail() method inside this for. this for goes through external users in group
                                // which they have balance different than zero.
                                String phoneNo = account.getPhoneNumber();
                                String emailTo = account.getEmail();
                                String emailFrom = "walletdroid@gmail.com";
                                String emailPass = "walletdroid123";

//                                if(atLestOneSms){
//                                    if(!checkPermission(Manifest.permission.SEND_SMS)) {
//                                        ActivityCompat.requestPermissions(GroupDetailActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);
//                                    }
//                                }

                                if(phoneNo != null && !phoneNo.equals("")){
                                    if(!checkPermission(Manifest.permission.SEND_SMS)) {
                                        ActivityCompat.requestPermissions(GroupDetailActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);
                                    }
                                    atLestOneSms = true;
                                    if(sendSMS(phoneNo,messageContentIndividual)){
                                        numberOfSuccessfulSMS++;
                                    }
                                } else {
                                    GMailSender sender = new GMailSender(emailFrom, emailPass);
                                    try {
                                        sender.sendMail(subject, messageContentIndividual, emailFrom, emailTo);

                                    } catch (Exception e){
                                        Log.e("Email problem: ", e.getMessage());
                                        successfulSendMail = false;
                                    }
                                }
                            }
                            if(successfulSendMail) {
                                Toast.makeText(GroupDetailActivity.this, "Emails SENT", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());
                            }
                            if(numberOfSuccessfulSMS == externalAccounts.size()){
                                Toast.makeText(GroupDetailActivity.this, "Emails SENT", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());
                            }
                            // if we have no external
                        } else {
                            finish();
                            startActivity(getIntent());
                        }
                    }
                });
    }

    private boolean checkPermission(String sendSms) {
        int checkpermission= ContextCompat.checkSelfPermission(this,sendSms);
        return checkpermission== PackageManager.PERMISSION_GRANTED;
    }

    public boolean sendSMS(String phoneNo, String sms) {
        if(!TextUtils.isEmpty(phoneNo)&&!TextUtils.isEmpty(sms)) {
            if(checkPermission(Manifest.permission.SEND_SMS)) {
                SmsManager smsManager=SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo,null,sms,null,null);
                return true;
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case SEND_SMS_PERMISSION_REQ:
                if(grantResults.length>0 &&(grantResults[0]==PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

package com.sda5.double2app.activities;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sda5.double2app.R;
import com.sda5.double2app.models.Account;
import com.sda5.double2app.models.Expense;
import java.util.ArrayList;

public class ExpenseDetailsActivity extends AppCompatActivity {

    FirebaseFirestore database;
    FirebaseAuth mAuth;
    String currentUserId;
    private Expense expense;
    private TextView title;
    private TextView category;
    private TextView date;
    private TextView buyer;
    private TextView totalAmount;
    private ListView expenseUserslistview;
    private ArrayList<String> names = new ArrayList<>();
    private Account buyerObject;
    private ImageView categoryImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);

        FirebaseApp.initializeApp(this);
        database = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        title = findViewById(R.id.expense_detail_title);
        category = findViewById(R.id.expense_detail_category);
        date = findViewById(R.id.expense_detail_date);
        buyer = findViewById(R.id.expense_detail_buyer);
        totalAmount = findViewById(R.id.expense_detail_totalAmount);
        expenseUserslistview = findViewById(R.id.expense_details_listview);
        categoryImage = findViewById(R.id.expense_detail_category_pic);

        Bundle bundle = getIntent().getExtras();

        //Getting expense from expense list view
        expense = (Expense) bundle.getSerializable("expense");

        title.setText(expense.getTitle());
        date.setText(expense.getDate());
       category.setText(expense.getCategory());
        Long totalAmountL = (Math.round(expense.getAmount()));
        totalAmount.setText(totalAmountL.toString() + " SEK");

        // Setting the picture for each category
        switch (expense.getCategory()){
            case "Grocery": categoryImage.setImageResource(R.drawable.grocery); break;
            case "Clothes": categoryImage.setImageResource(R.drawable.clothes); break;
            case "Transportation": categoryImage.setImageResource(R.drawable.transportation); break;
            case "Recurring": categoryImage.setImageResource(R.drawable.recurring); break;
            case "Eat out": categoryImage.setImageResource(R.drawable.eat); break;
            case "Utility": categoryImage.setImageResource(R.drawable.utility); break;
            case "Membership": categoryImage.setImageResource(R.drawable.membership); break;
            case "Other": categoryImage.setImageResource(R.drawable.other); break;
        }

        // Create a listView for people who are involved in expense
        ExpenseUsersAdapter adapter = new ExpenseUsersAdapter();
        expenseUserslistview.setAdapter(adapter);

        // Get the buyer name from database regard to Account id of buyer saved in expense object
        database.collection("Accounts").whereEqualTo("id", expense.getPayerAccountId()).limit(1).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots) {
                            buyerObject = queryDocumentSnapshot.toObject(Account.class);
                            buyer.setText(buyerObject.getOwnerName());
                        }
                    }
                });
        // Find the name for ids who are involved in expense
        for(String expenseAccountId : expense.getExpenseAccountIds()){
            database.collection("Accounts").whereEqualTo("id", expenseAccountId).limit(1).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots) {
                                Account expenseUserObject = queryDocumentSnapshot.toObject(Account.class);
                                names.add(expenseUserObject.getOwnerName());
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    // Custom adapter for people who are involved in expense
    class ExpenseUsersAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return expense.getExpenseAccountIds().size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.expense_users_list, null);
            ImageView buyerOrNot = convertView.findViewById(R.id.buyer_image);
            TextView tvName = convertView.findViewById(R.id.expense_detail_list_name);

            if(names.size() > position && buyerObject != null){

                String b = expense.getPayerAccountId();
                String c = expense.getExpenseAccountIds().get(position);
                String d = names.get(position);

                // If the person is buyer or not put adifferent picture for it
                if(buyerObject.getOwnerName().equals(names.get(position))){
                    buyerOrNot.setImageResource(R.drawable.buyer);
                } else {
                    buyerOrNot.setImageResource(R.drawable.no_buyer);
                }
            }

            // Due to sync problem checks and prevent index out of bonds
            if(names.size() > position){
                tvName.setText(names.get(position));
            }
            return convertView;
        }
    }
}

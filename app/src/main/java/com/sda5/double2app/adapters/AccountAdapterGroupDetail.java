package com.sda5.double2app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sda5.double2app.R;
import com.sda5.double2app.models.Account;
import com.sda5.double2app.models.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;


public class AccountAdapterGroupDetail extends ArrayAdapter<Account> {
    private Context mContext;
    private final List<Account> accounts;
    private final boolean showCheckboxes;
    private final List<String> selectedAccountIDList = new ArrayList<>();
    private Group group;
    private final List<Boolean> checkedItems = new ArrayList<>();



    public AccountAdapterGroupDetail(Context context, Group group, ArrayList<Account> accounts, boolean showCheckboxes) {
        super(context, 0, accounts);
        mContext = context;
        this.accounts = accounts;
        this.showCheckboxes = showCheckboxes;
        this.group = group;
        initializeCheckedList();

    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        initializeCheckedList();
    }

    private void initializeCheckedList() {
        checkedItems.clear();
        IntStream.range(0, accounts.size()).boxed().forEach(ignored -> checkedItems.add(false));
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_account_item_group_detail, parent, false);

        Account account = accounts.get(position);
        TextView debt = listItem.findViewById(R.id.textview_account_debt_gd);
        ImageView userTypeImage = listItem.findViewById(R.id.picture_user_type);
        String accountID = account.getId();


        if (accountID != null) {
            Double balance = group.getBalance().get(accountID);
            if (null != balance) {
                String balanceString = balance.toString();
                Long accountBalance =(Math.round(balance));
                debt.setText(accountBalance.toString()+ " SEK  ");
                int greenColorValue = Color.parseColor("#277521");
                if (balanceString.contains("-")) {
                    debt.setTextColor(Color.RED);
                } else {
                    debt.setTextColor(greenColorValue);
                }
            }
        }

        TextView textViewAccount = listItem.findViewById(R.id.textview_account_item_gd);
        if (account.isInternalAccount()) {
            textViewAccount.setText(account.getOwnerName());
            userTypeImage.setImageResource(R.drawable.circleblue);
        } else {
            textViewAccount.setText( account.getOwnerName());
            userTypeImage.setImageResource(R.drawable.greycircle);
        }

        textViewAccount.setTag(account.getId());

        final CheckBox checkBoxAccount = listItem.findViewById(R.id.checkbox_account_item_gd);
        checkBoxAccount.setTag(account.getId());

        if (showCheckboxes) {
            checkBoxAccount.setVisibility(View.VISIBLE);
        } else {
            checkBoxAccount.setVisibility(View.GONE);
        }

        View finalListItem = listItem;
        checkBoxAccount.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String selectedAccountID = checkBoxAccount.getTag().toString();
            HashMap<String, Double> groupBalance = group.getBalance();
            checkedItems.set(position, isChecked);
            Double balanceForCurrentUser = groupBalance.get(selectedAccountID);
            if (isChecked) {
                if (balanceForCurrentUser == 0) {
                    selectedAccountIDList.add(selectedAccountID);
                    finalListItem.setBackgroundColor(Color.parseColor("#FFFFE0"));
                } else {
                    Toast.makeText(getContext(), "You are not allowed to delete member since there is a balance.",
                            Toast.LENGTH_SHORT).show();
                    checkBoxAccount.setChecked(false);

                }
            } else {
                selectedAccountIDList.remove(selectedAccountID);
                finalListItem.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });
        checkBoxAccount.setChecked(checkedItems.get(position));
        return listItem;
    }

    public List<String> getSelectedAccountIDList() {
        return selectedAccountIDList;
    }


    public void addSelectedAccountId(String accountId) {
        this.selectedAccountIDList.add(accountId);
    }
}

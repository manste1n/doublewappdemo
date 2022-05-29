package com.sda5.double2app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sda5.double2app.R;
import com.sda5.double2app.models.Account;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccountAdapter extends ArrayAdapter<Account> {
    private Context mContext;
    private final List<Account> accounts;
    private final boolean showCheckboxes;
    private final List<String> selectedAccountIDList = new ArrayList<>();
    private final List<Boolean> checkedItems = new ArrayList<>();


    public AccountAdapter(Context context, ArrayList<Account> accounts, boolean showCheckboxes) {
        super(context, 0, accounts);
        mContext = context;
        this.accounts = accounts;
        this.showCheckboxes = showCheckboxes;
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
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_account_item, parent, false);


        Account account = accounts.get(position);

        final CheckBox checkBoxAccount = listItem.findViewById(R.id.checkbox_account_item);
        checkBoxAccount.setTag(account.getId());

        TextView textViewAccount = listItem.findViewById(R.id.textview_account_item);
        if (account.isInternalAccount()) {
            textViewAccount.setText(account.getOwnerName());
        } else {
            textViewAccount.setText("Name: " + account.getOwnerName() + "\n" + "Email:" + account.getEmail());
            textViewAccount.setTypeface(null, Typeface.ITALIC);
        }

        textViewAccount.setTag(account.getId());


        if (showCheckboxes) {
            checkBoxAccount.setVisibility(View.VISIBLE);
        } else {
            checkBoxAccount.setVisibility(View.GONE);
        }

        View finalListItem = listItem;
        checkBoxAccount.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String selectedAccountID = checkBoxAccount.getTag().toString();
            checkedItems.set(position, isChecked);

            if (isChecked) {
                selectedAccountIDList.add(selectedAccountID);
                finalListItem.setBackgroundColor(Color.parseColor("#FFFFE0"));
            } else {
                selectedAccountIDList.remove(selectedAccountID);
                finalListItem.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        checkBoxAccount.setChecked(checkedItems.get(position));

        return listItem;
    }

    public List<String> getSelectedAccountIDList() {
        Set<String> set = new HashSet<>(selectedAccountIDList);
        selectedAccountIDList.clear();
        selectedAccountIDList.addAll(set);
        return selectedAccountIDList;
    }


    public void addSelectedAccountId(String accountId) {
        this.selectedAccountIDList.add(accountId);
    }
}

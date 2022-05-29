package com.sda5.double2app.adapters;

        import android.content.Context;
//        import android.support.annotation.NonNull;
//        import android.support.annotation.Nullable;
        import android.graphics.Color;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.CheckBox;
        import android.widget.CompoundButton;
        import android.widget.TextView;

        import com.sda5.double2app.R;
        import com.sda5.double2app.models.Account;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.stream.IntStream;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;

public class AccountAdapterAddExpense extends ArrayAdapter<Account> {
    private Context mContext;
    private final List<Account> accounts;
    private final boolean showCheckboxes;
    private final List<String> selectedExpenseUsersIDList = new ArrayList<>();
    private final List<String> selectedExpenseUsersNameList = new ArrayList<>();
    private final List<Boolean> checkedItems = new ArrayList<>();


    public AccountAdapterAddExpense(Context context, ArrayList<Account> accounts, boolean showCheckboxes) {
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

        final Account account = accounts.get(position);

        TextView textViewAccount = listItem.findViewById(R.id.textview_account_item);
        textViewAccount.setText(account.getOwnerName());
        textViewAccount.setTag(account.getId());

        final CheckBox checkBoxAccount = listItem.findViewById(R.id.checkbox_account_item);
        checkBoxAccount.setTag(account.getId());

        if (showCheckboxes) {
            checkBoxAccount.setVisibility(View.VISIBLE);
        } else {
            checkBoxAccount.setVisibility(View.GONE);
        }

        View finalListItem = listItem;
        checkBoxAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String selectedAccountID = checkBoxAccount.getTag().toString();
                checkedItems.set(position, isChecked);
                if (isChecked) {
                    selectedExpenseUsersIDList.add(selectedAccountID);
                    selectedExpenseUsersNameList.add(account.getOwnerName());
                    finalListItem.setBackgroundColor(Color.parseColor("#FFFFE0"));

                } else {
                    selectedExpenseUsersIDList.remove(selectedAccountID);
                    selectedExpenseUsersNameList.remove(account.getOwnerName());
                    finalListItem.setBackgroundColor(Color.parseColor("#FFFFFF"));

                }
            }
        });
        checkBoxAccount.setChecked(checkedItems.get(position));

        return listItem;
    }

    public List<String> getSelectedExpenseUsersIDList() {
        return selectedExpenseUsersIDList;
    }

    public List<String> getSelectedExpenseUsersNameList(){
        return selectedExpenseUsersNameList;
    }
}

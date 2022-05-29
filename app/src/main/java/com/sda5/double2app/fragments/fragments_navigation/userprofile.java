package com.sda5.double2app.fragments.fragments_navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.sda5.double2app.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class userprofile extends Fragment {

    private UserprofileViewModel mViewModel;
    FirebaseAuth mAuth;

    public static userprofile newInstance() {
        return new userprofile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.userprofile_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(UserprofileViewModel.class);
        // TODO: Use the ViewModel

        mAuth = FirebaseAuth.getInstance();
        TextView tvname = getView().findViewById(R.id.user_profile_name);
        TextView tvEmail = getView().findViewById(R.id.user_profile_email);

        tvname.setText(mAuth.getCurrentUser().getDisplayName());
        tvEmail.setText(mAuth.getCurrentUser().getEmail());
    }

}

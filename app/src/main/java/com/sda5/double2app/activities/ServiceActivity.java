package com.sda5.double2app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.sda5.double2app.notifications.NotificationFragment;
import com.sda5.double2app.R;
import com.sda5.double2app.fragments.ExpenseFragment;
import com.sda5.double2app.fragments.GroupFragment;
import com.sda5.double2app.fragments.fragments_navigation.AppDetails;
import com.sda5.double2app.fragments.fragments_navigation.Feedbacknav;
import com.sda5.double2app.fragments.fragments_navigation.GraphFragment;
import com.sda5.double2app.fragments.fragments_navigation.ShareNav;
import com.sda5.double2app.fragments.fragments_navigation.userprofile;

//import android.widget.Toolbar;

public class ServiceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;

    public NavigationView navigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.navigation_group:
                        fragment = new GroupFragment();
                        break;
                    case R.id.navigation_expense:
                        fragment = new ExpenseFragment();
                        break;

                    case R.id.navigation_graph:
                        fragment = new GraphFragment();
                        break;
                    case R.id.navigation_notification:
                        fragment = new NotificationFragment();



                }
                return loadFragment(fragment);
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        BottomNavigationView navigation = findViewById(R.id.navigation);


        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        //Navigation view
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //navigation drawer listener
        navigationView.setNavigationItemSelectedListener(this);

        loadFragment(new GroupFragment());
    }

    // FOR NAVIGATION DRAWER
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    // FOR NAVIGATION DRAWER
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    //FOR NAVIGATION DRAWER
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.nav_userProfile:
                fragment = new userprofile();
                break;
            case R.id.nav_noOfNotification:
                fragment = new NotificationFragment();
                break;

            case R.id.nav_expenseSetting:
                fragment = new GraphFragment();
                break;

            case R.id.nav_feedback:
                fragment = new Feedbacknav();
                break;

            case R.id.nav_phoneNumber:
                fragment = new AppDetails();
                break;
            case R.id.nav_share:
                fragment = new ShareNav();
                break;
            case R.id.nav_signOut:
                signOut(findViewById(R.id.nav_signOut));
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return loadFragment(fragment);
    }


    private boolean loadFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return true;
        }
            return false;
    }


    /**
     * Send an email to your friend and let him know abt this app
     * @param v
     */
    public void composeEmail(View v) {
        final EditText userFeedback = findViewById(R.id.giveFriendEmail);
        String emailFriend = userFeedback.getText().toString();
        switch (v.getId()) {
            case R.id.buttonShareEmail:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_TEXT, "Use this link to download Wallet droid https://github.com/sda5-walletdroid/walletdroid");
                intent.putExtra(Intent.EXTRA_SUBJECT, "WalletDroid Invitation");
                intent.putExtra(Intent.EXTRA_EMAIL,new String[]{emailFriend});
                //intent.setType("message/rfc822");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
        }
    }
//                    intent.putExtra(Intent.EXTRA_SUBJECT, "Wallet droid Invitation");
//                    intent.putExtra(Intent.EXTRA_TEXT, "Use this link to download Wallet droid https://github.com/sda5-walletdroid/walletdroid");

    /**
     * Send email with your feedback
     * @param v
     */
    public void sendFeedback(View v) {

        final TextInputLayout userFeedback = findViewById(R.id.textInputLayoutFeed);
        String feedback = userFeedback.getEditText().getText().toString();
        String emailApp = "walletdroid@gmail.com";
        switch (v.getId()) {
            case R.id.btnfeedback:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_TEXT, feedback);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback WalletDroid");
                intent.putExtra(Intent.EXTRA_EMAIL,new String[]{emailApp});
                //intent.setType("message/rfc822");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
        }
    }


    public void runQueryForParticularExpense(View view) {
        Intent myIntent = new Intent(getApplicationContext(), SeeExpenseGraphForParticularCategory.class);
        startActivity(myIntent);
    }

    /**
     * For comparing 2 expense categories and making a bar plot
     * @param view
     */
    public void runQueryForExpenseComparison(View view) {
        Intent myIntent = new Intent(getApplicationContext(), SeeExpenseGraphForTwoActivities.class);
        startActivity(myIntent);
    }

    public void signOut(View view) {
        mAuth.signOut();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void createNewGroup(View view) {
        Intent intent = new Intent(this, CreateNewGroupActivity.class);
        startActivity(intent);
    }

    public void addExpense(View v){
        Intent intent = new Intent(getApplicationContext(), AddExpenseActivity.class);
        startActivity(intent);
    }

    public void goToQuery(View view){
        Intent intent = new Intent(getApplicationContext(), com.sda5.double2app.activities.QueryActivity.class);
        startActivity(intent);

    }
}

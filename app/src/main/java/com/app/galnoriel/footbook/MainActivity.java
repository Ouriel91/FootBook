package com.app.galnoriel.footbook;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.galnoriel.footbook.adapters.SectionsAdapter;
import com.app.galnoriel.footbook.fragments.ProfileFragment;
import com.app.galnoriel.footbook.fragments.GroupFragment;
import com.app.galnoriel.footbook.fragments.GameFragment;
import com.app.galnoriel.footbook.fragments.SearchGameFieldFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SectionsAdapter sectionsAdapter;
    private ViewPager viewPager;

    TextInputLayout emailLayout;
    TextInputLayout userNameLayout;
    TextInputLayout passwordLayout;
    TextInputLayout passwordConfirmLayout;
    TextInputLayout regionLayout;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z0-9])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$");

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//region viewpager
        sectionsAdapter = new SectionsAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        int[] imageResID = {
                R.drawable.thumbnail,
                R.drawable.team_avatar,
                R.drawable.match_ic_tab,
                R.drawable.map_ic_tab
        };
//set tags for tabs?

        for (int i = 0; i <imageResID.length ; i++) {
            tabLayout.getTabAt(i).setIcon(imageResID[i]);
        }
//endregion


    }

    private void setupViewPager(ViewPager viewPager) {

        SectionsAdapter adapter = new SectionsAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfileFragment());
        adapter.addFragment(new GroupFragment());
        adapter.addFragment(new GameFragment());
        adapter.addFragment(new SearchGameFieldFragment());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogSignView = getLayoutInflater().inflate(R.layout.sign_dialog,null);

        emailLayout = dialogSignView.findViewById(R.id.email_layout);
        userNameLayout = dialogSignView.findViewById(R.id.username_layout);
        passwordLayout = dialogSignView.findViewById(R.id.password_layout);
        passwordConfirmLayout = dialogSignView.findViewById(R.id.password_confirm_layout);
        regionLayout = dialogSignView.findViewById(R.id.region_layout);

        Button signUpBtn = dialogSignView.findViewById(R.id.sign_up_btn);
        Button signInBtn = dialogSignView.findViewById(R.id.sign_in_btn);

        if (id == R.id.sign_up) {



            builder.setView(dialogSignView).show();

            signUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!validateEmail(emailLayout) | !validateUserName(userNameLayout) |
                            !validatePassword(passwordLayout,passwordConfirmLayout)){
                        return;
                    }
                }
            });

            //Button positiveButton = builder.getButton()

        } else if (id == R.id.sign_in) {

        }

        else if (id == R.id.sign_out) {

            firebaseAuth.signOut();
        }
        else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean validateEmail(TextInputLayout emailLayout) {

        String emailInput = emailLayout.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()){

            emailLayout.setError("Field cannot be empty");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            emailLayout.setError("Please enter a valid email address");
            return false;
        }
        else {
            emailLayout.setError(null);
            return true;
        }
    }

    private boolean validateUserName(TextInputLayout userNameLayout) {

        String usernameInput = userNameLayout.getEditText().getText().toString().trim();

        if (usernameInput.isEmpty()){

            userNameLayout.setError("Field cannot be empty");
            return false;
        }
        else if (usernameInput.length() > 20){

            userNameLayout.setError("User name is too long");
            return false;
        }
        else {

            userNameLayout.setError(null);
            return true;
        }
    }

    private boolean validatePassword(TextInputLayout passwordLayout, TextInputLayout passwordConfirmLayout) {

        String password = passwordLayout.getEditText().getText().toString().trim();
        String confirmPassword = passwordConfirmLayout.getEditText().getText().toString().trim();

        if (password.isEmpty()){

            passwordLayout.setError("Field cannot be empty");
            return false;
        }else if(!PASSWORD_PATTERN.matcher(password).matches()){
            passwordLayout.setError("Give password at least 6 six characters with one special character");
            return false;
        }
        else if (PASSWORD_PATTERN.matcher(password).matches() && !password.equals(confirmPassword)){
            passwordLayout.setError(null);
            passwordConfirmLayout.setError("Miss match between two passwords");
            return false;
        }
        else {
            passwordLayout.setError(null);
            passwordConfirmLayout.setError(null);
            return true;
        }

    }
}

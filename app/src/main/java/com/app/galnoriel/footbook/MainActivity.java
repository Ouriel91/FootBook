package com.app.galnoriel.footbook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.galnoriel.footbook.adapters.SectionsAdapter;
import com.app.galnoriel.footbook.fragments.ProfileFragment;
import com.app.galnoriel.footbook.fragments.GroupFragment;
import com.app.galnoriel.footbook.fragments.GameFragment;
import com.app.galnoriel.footbook.fragments.SearchGameFieldFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SectionsAdapter sectionsAdapter;
    private ViewPager viewPager;

    private TextInputLayout emailLayout;
    private TextInputLayout userNameLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout passwordConfirmLayout;
    private TextInputLayout regionLayout;
    private NavigationView navigationView;
    private CoordinatorLayout coordinatorLayout;
    private AlertDialog alertDialog;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z0-9])" +      //any letter
//                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
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
//region toolbar drawer layout navigation view coordinator
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
//endregion

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                View headerView = navigationView.getHeaderView(0);

                TextView loginTV = headerView.findViewById(R.id.login_tv);
                TextView userLoginTV = headerView.findViewById(R.id.user_login_tv);

                final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null){ //sign up or sign in

                    if (userName != null){

                        currentUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(userName).build())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        userName = null; //user registrated

                                        if (task.isSuccessful()){
                                            Snackbar.make(coordinatorLayout, currentUser.getDisplayName() + " Welcome",Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                    loginTV.setText("Welcome!!!");
                    userLoginTV.setText(currentUser.getDisplayName());
                    navigationView.getMenu().findItem(R.id.sign_in).setVisible(false);
                    navigationView.getMenu().findItem(R.id.sign_up).setVisible(false);
                    navigationView.getMenu().findItem(R.id.sign_out).setVisible(true);
                }
                else { //sign out


                    loginTV.setText("Please Log in");
                    userLoginTV.setText("We are waiting for you");
                    navigationView.getMenu().findItem(R.id.sign_in).setVisible(true);
                    navigationView.getMenu().findItem(R.id.sign_up).setVisible(true);
                    navigationView.getMenu().findItem(R.id.sign_out).setVisible(false);

                }

            }
        };

//region viewpager
        sectionsAdapter = new SectionsAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        int[] imageResID = {
                R.drawable.player_avatar,
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

        ImageButton signUpBtn = dialogSignView.findViewById(R.id.sign_up_btn_logdia);
        ImageButton signInBtn = dialogSignView.findViewById(R.id.sign_in_btn_logdia);

        if (id == R.id.sign_up) {

            signInBtn.setVisibility(View.GONE);
            signUpBtn.setVisibility(View.VISIBLE);

            emailLayout.setVisibility(View.VISIBLE);
            userNameLayout.setVisibility(View.VISIBLE);
            passwordLayout.setVisibility(View.VISIBLE);
            passwordConfirmLayout.setVisibility(View.VISIBLE);
            regionLayout.setVisibility(View.VISIBLE);

            builder.setView(dialogSignView);

            alertDialog = builder.create();

            signUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!validateEmail(emailLayout) | !validateUserName(userNameLayout) |
                            !validatePasswordSignUp(passwordLayout,passwordConfirmLayout)){
                        return;
                    }


                    String email = emailLayout.getEditText().getText().toString();
                    String password = passwordLayout.getEditText().getText().toString();
                    userName = userNameLayout.getEditText().getText().toString();

                    firebaseAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){

                                        Snackbar.make(coordinatorLayout, "Sign up successful", Snackbar.LENGTH_SHORT).show();
                                    }
                                    else {

                                        Snackbar.make(coordinatorLayout, "Sign up failed", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    alertDialog.dismiss();
                }
            });

            alertDialog.show();
        } else if (id == R.id.sign_in) {

            signInBtn.setVisibility(View.VISIBLE);
            signUpBtn.setVisibility(View.GONE);

            userNameLayout.setVisibility(View.GONE);
            passwordConfirmLayout.setVisibility(View.GONE);
            regionLayout.setVisibility(View.GONE);

            builder.setView(dialogSignView);

            alertDialog = builder.create();

            signInBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String email = emailLayout.getEditText().getText().toString();
                    String password = passwordLayout.getEditText().getText().toString();

                    if (!validateEmail(emailLayout) | !validatePasswordSignIn()){
                        return;
                    }


                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){

                                        Snackbar.make(coordinatorLayout, "Sign in succesfull", Snackbar.LENGTH_SHORT).show();
                                    }
                                    else {

                                        Snackbar.make(coordinatorLayout, "Sign in failed", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();
        }

        else if (id == R.id.sign_out) {

            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            Snackbar.make(coordinatorLayout, "Bye bye " + currentUser.getDisplayName(), Snackbar.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
        else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        firebaseAuth.removeAuthStateListener(authStateListener);
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

    private boolean validatePasswordSignUp(TextInputLayout passwordLayout, TextInputLayout passwordConfirmLayout) {

        String password = passwordLayout.getEditText().getText().toString().trim();
        String confirmPassword = passwordConfirmLayout.getEditText().getText().toString().trim();

        if (password.isEmpty()){

            passwordLayout.setError("Field cannot be empty");
            return false;
        }else if(!PASSWORD_PATTERN.matcher(password).matches()){
            passwordLayout.setError("Password must be at least 6 six characters with one capital letter");
            return false;
        }
        else if (PASSWORD_PATTERN.matcher(password).matches() && !password.equals(confirmPassword)){
            passwordLayout.setError(null);
            passwordConfirmLayout.setError("Mismatch between two passwords");
            return false;
        }
        else {
            passwordLayout.setError(null);
            passwordConfirmLayout.setError(null);
            return true;
        }

    }

    private boolean validatePasswordSignIn() {

        String password = passwordLayout.getEditText().getText().toString().trim();


        if (password.isEmpty()){

            passwordLayout.setError("Field cannot be empty");
            return false;
        }else if(!PASSWORD_PATTERN.matcher(password).matches()){
            passwordLayout.setError("Give password at least 6 six characters with one special character");
            return false;
        }
        else {
            passwordLayout.setError(null);
            passwordConfirmLayout.setError(null);
            return true;
        }
    }
}

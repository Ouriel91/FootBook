package com.app.galnoriel.footbook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.galnoriel.footbook.adapters.SectionsAdapter;
import com.app.galnoriel.footbook.classes.CustomSharedPrefAdapter;
import com.app.galnoriel.footbook.classes.GroupPlay;
import com.app.galnoriel.footbook.classes.Player;
import com.app.galnoriel.footbook.fragments.ProfileFragment;
import com.app.galnoriel.footbook.fragments.GroupFragment;
import com.app.galnoriel.footbook.fragments.GameFragment;
import com.app.galnoriel.footbook.fragments.SearchGameFieldFragment;

import com.app.galnoriel.footbook.interfaces.AccessGroupDB;
import com.app.galnoriel.footbook.interfaces.MainToGroupFrag;
import com.app.galnoriel.footbook.interfaces.MainToPlayerFrag;
import com.app.galnoriel.footbook.interfaces.MoveToTab;
import com.app.galnoriel.footbook.interfaces.AccessPlayerDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MoveToTab, AccessGroupDB, AccessPlayerDB {
    //region interfaces

    public MainToPlayerFrag sendToPlayerFrag;
    public MainToGroupFrag sendToGroupFrag;
    public static final int TAB_PROFILE = 0;
    public static final int TAB_GROUP = 1;
    public static final int TAB_GAME = 2;
    public static final int TAB_MAP= 3;

    //endregion

    //region global declarations
    private SectionsAdapter sectionsAdapter;
    private ViewPager viewPager;
    private TextInputLayout emailLayout;
    private TextInputLayout userNameLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout passwordConfirmLayout;
    private TextInputLayout regionLayout;
    private NavigationView navigationView;
    public static CoordinatorLayout coordinatorLayout;
    private AlertDialog alertDialog;
    private CustomSharedPrefAdapter sharedPref;
    private ImageButton signUpBtn;
    private ImageButton signInBtn;
    private FirebaseFirestore db;
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
    String userName; //will be fetched from layout on signup Dialog
//endregion
    //region decleration for user database

    //endregion

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof ProfileFragment){ //set interface with Profile frag
            ((ProfileFragment) fragment).showTab = this;
            ((ProfileFragment) fragment).prfGroupDB = this;
            ((ProfileFragment) fragment).prfPlayerDB = this;

        }else if (fragment instanceof GroupFragment){
            ((GroupFragment) fragment).showTab = this;
            ((GroupFragment) fragment).grfGroupDB = this;
            ((GroupFragment) fragment).grfPlayerDB = this;

        }else if (fragment instanceof GameFragment){

        }else if (fragment instanceof SearchGameFieldFragment){
            //set listerenrerasdfgk
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        sharedPref = new CustomSharedPrefAdapter(this);


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



        //region Layout and views assignment

        //endregion

        //region firebase login status listener
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                View headerView = navigationView.getHeaderView(0); //title of drawer
                TextView loginTV = headerView.findViewById(R.id.login_tv);
                TextView userLoginTV = headerView.findViewById(R.id.user_login_tv);
                final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null){ //user is logged in
                    sharedPref.setUserId(currentUser.getUid());  //save user id
                    sharedPref.setLoginStatus(true);
                    getPlayerFromServer(currentUser.getUid(),TAB_PROFILE);
                    loginTV.setText("Welcome!!!");
                    userLoginTV.setText(currentUser.getDisplayName());
                    navigationView.getMenu().findItem(R.id.sign_in).setVisible(false);
                    navigationView.getMenu().findItem(R.id.sign_up).setVisible(false);
                    navigationView.getMenu().findItem(R.id.reset_password).setVisible(false);
                    navigationView.getMenu().findItem(R.id.sign_out).setVisible(true);

                }
                else { //signed out
                    sharedPref.setLoginStatus(false);
                    sharedPref.removeCurrentUserInfo();
                    loginTV.setText("Please Log in");
                    userLoginTV.setText("We are waiting for you");
                    navigationView.getMenu().findItem(R.id.sign_in).setVisible(true);
                    navigationView.getMenu().findItem(R.id.sign_up).setVisible(true);
                    navigationView.getMenu().findItem(R.id.reset_password).setVisible(true);
                    navigationView.getMenu().findItem(R.id.sign_out).setVisible(false);
                }

            }
        };
//endregion

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

    private void updateUserDataBase(){

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsAdapter adapter = new SectionsAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfileFragment());
        adapter.addFragment(new GroupFragment());
        adapter.addFragment(new GameFragment());
        adapter.addFragment(new SearchGameFieldFragment());
        viewPager.setAdapter(adapter);
        viewPager.getCurrentItem();


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
        int navBtnId = item.getItemId();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogSignView = getLayoutInflater().inflate(R.layout.dialog_sign_in,null);

        emailLayout = dialogSignView.findViewById(R.id.email_layout);
        userNameLayout = dialogSignView.findViewById(R.id.username_layout);
        passwordLayout = dialogSignView.findViewById(R.id.password_layout);
        passwordConfirmLayout = dialogSignView.findViewById(R.id.password_confirm_layout);
        regionLayout = dialogSignView.findViewById(R.id.region_layout);

        signUpBtn = dialogSignView.findViewById(R.id.sign_up_btn_logdia);
        signInBtn = dialogSignView.findViewById(R.id.sign_in_btn_logdia);

        switch (navBtnId){
            case R.id.sign_up: //sign up btn pressed
                signUpUser();
                builder.setView(dialogSignView);
                alertDialog = builder.create();
                alertDialog.show();

                break;

            case R.id.sign_in: //sign in btn pressed
                builder.setView(dialogSignView);
                alertDialog = builder.create();
                singInUser();
                alertDialog.show();
                break;

            case  R.id.sign_out:
                signOutUser(); //sign out btn pressed
                break;

            case R.id.reset_password:
                builder.setView(dialogSignView);
                alertDialog = builder.create();
                resetPassword();
                alertDialog.show();
                break;

            case  R.id.nav_send:

                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void resetPassword() {

        signInBtn.setVisibility(View.VISIBLE);
        signUpBtn.setVisibility(View.GONE);
        userNameLayout.setVisibility(View.GONE);
        passwordLayout.setVisibility(View.GONE);
        passwordConfirmLayout.setVisibility(View.GONE);
        regionLayout.setVisibility(View.GONE);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailInput = emailLayout.getEditText().getText().toString().trim();
                if (!validateEmail(emailLayout) ){
                    return;
                }
                else {
                    firebaseAuth.sendPasswordResetEmail(emailInput).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Snackbar.make(coordinatorLayout, "Check your email account to restore your user and sign in again",
                                        Snackbar.LENGTH_SHORT).show();
                            }
                            else {
                                String message = task.getException().getMessage();
                                Snackbar.make(coordinatorLayout, message,
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                alertDialog.dismiss();
            }
        });
    }

    private void signOutUser() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Snackbar.make(coordinatorLayout, "Bye bye " + currentUser.getDisplayName(), Snackbar.LENGTH_SHORT).show();
        currentUser = null;
        firebaseAuth.signOut();
        sendToPlayerFrag.onGetPlayerComplete(new Player());
        sharedPref.removeCurrentUserInfo();
    }

    private void singInUser() {

        signInBtn.setVisibility(View.VISIBLE);
        signUpBtn.setVisibility(View.GONE);
        userNameLayout.setVisibility(View.GONE);
        passwordConfirmLayout.setVisibility(View.GONE);
        regionLayout.setVisibility(View.GONE);
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
    }

    private void signUpUser() {
        signInBtn.setVisibility(View.GONE);
        signUpBtn.setVisibility(View.VISIBLE);
        emailLayout.setVisibility(View.VISIBLE);
        userNameLayout.setVisibility(View.VISIBLE);
        passwordLayout.setVisibility(View.VISIBLE);
        passwordConfirmLayout.setVisibility(View.VISIBLE);
        regionLayout.setVisibility(View.VISIBLE);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //sign up btn
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
                                    createNewProfileInServer();
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
    }

    private void createNewProfileInServer() {
        FirebaseUser curUser = firebaseAuth.getCurrentUser();

        curUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(userName).build())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Snackbar.make(coordinatorLayout, firebaseAuth.getCurrentUser().getDisplayName() + " Welcome",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
        String whereFrom = "City";
        try{whereFrom = regionLayout.getEditText().getText().toString();}
        catch (Exception e){ Log.e("not home adress",e.getMessage());}
        Player newProfile = new Player(curUser.getUid(),userName,whereFrom);
        db.collection(GlobConst.DB_USER_TABLE).document(curUser.getUid()).set(newProfile.toHashMap());

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

    @Override //receive info from tab and move to other tab
    public void goToFrag(int to, String... params) {
        Log.d("moving to tab: ", to + "\n"+ params.toString());
        switch (to){
            case TAB_PROFILE:
                //profile id in [0]
                sharedPref.setDisplayProfileId(params[0]);
                requestPlayerFromServer(params[0],TAB_PROFILE);
                viewPager.setCurrentItem(TAB_PROFILE,true);
                break;
            case TAB_GROUP:
                //getGroup , id is in params[0]
                sharedPref.setDisplayGroupId(params[0]);
                requestGroupFromServer(params[0], TAB_GROUP);
                viewPager.setCurrentItem(TAB_GROUP,true);
                break;
            case TAB_GAME:

                break;
            case TAB_MAP:

                break;
            default:

                break;
        }
    }
    //region interfaces with fragment for server data fetching
    @Override
    public String createNewGroupInServer(final GroupPlay group) {
        group.addMember(sharedPref.getUserId());
        group.addAdmins(sharedPref.getUserId());
        group.setId(db.collection(GlobConst.DB_GROUP_TABLE).document().getId());//auto create id
        db.collection(GlobConst.DB_GROUP_TABLE).document(group.getId())//add group
                .set(group.toHashMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Snackbar.make(coordinatorLayout, "New group create :  " + group.getName(), Snackbar.LENGTH_SHORT).show();
                        sharedPref.setDisplayGroup(group);
                        sendToPlayerFrag.onGetGroupComplete(group); //add to member in group view
                        sendToGroupFrag.onGetGroupComplete(group); //display in fragment
                    }
                });
        //add group to user`s profile
        db.collection(GlobConst.DB_USER_TABLE).document(sharedPref.getUserId())
                .update(GlobConst.DB_USER_GROUPS, FieldValue.arrayUnion(group.getId()));
        return group.getId();
    }

    @Override
    public String updateGroupInServer(final GroupPlay group) {

        db.collection(GlobConst.DB_GROUP_TABLE).document(group.getId())//add group
                .update(group.toHashMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("updateGroupInServer", "success! "+group.toString());
                        }else Log.d("updateGroupInServer", "FAIL! "+group.toString());
                        //                        Snackbar.make(coordinatorLayout, "Updated group :  " + group.getName(), Snackbar.LENGTH_SHORT).show();
//                        sharedPref.setDisplayGroup(group);
                    }
                });
        Log.d("updateGroupInServer", group.toString());
        return group.getName();
    }

    @Override
    public String updatePlayerInServer(final Player player) {
        db.collection(GlobConst.DB_USER_TABLE).document(player.get_id())
                .set(player.toHashMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Log.d("UPDATE SERVER ","success : "+player.toLogString());
                        else
                            Log.w("UPDATE SERVER ","FAILED!! : "+player.get_id());
                    }
                });
        return player.get_id();
    }

    @Override
    public String requestGroupFromServer(String group_id, final int frag) {
        db.collection(GlobConst.DB_GROUP_TABLE).document(group_id)//get group
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            if (frag == TAB_PROFILE)
                                sendToPlayerFrag.onGetGroupComplete(new GroupPlay(task.getResult()));
                            if (frag == TAB_GROUP)
                                sendToGroupFrag.onGetGroupComplete(new GroupPlay(task.getResult()));
                        }
                    }
                });
        return null;
    }

    @Override
    public String requestPlayerFromServer(String playerId,int frag) {
        getPlayerFromServer(playerId , frag);

        return playerId;
    }

    private void getPlayerFromServer(final String uid, final int frag){
        if (sharedPref.getLoginStatus()) { //enable only if user looged in
            db.collection(GlobConst.DB_USER_TABLE)  //fetch user info from server and store in share pref for further display
                    .document(uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot playerProfile = task.getResult();
                                if (playerProfile.exists()) {
                                    Player profile = new Player(playerProfile);
                                    profile.set_id(uid);

                                    if(frag==TAB_PROFILE){
                                        sharedPref.setDisplayProfile(profile);
                                        sendToPlayerFrag.onGetPlayerComplete(profile);}
                                    else if (frag==TAB_GROUP)
                                        sendToGroupFrag.onGetPlayerComplete(profile);
                                    Log.d("SuccesS Profile server ",profile.get_id());

                                } else
                                    Toast.makeText(MainActivity.this, "Profile" + uid + " Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    //endregion
}
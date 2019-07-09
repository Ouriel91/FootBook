package com.app.galnoriel.footbook;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.app.galnoriel.footbook.adapters.SectionsAdapter;
import com.app.galnoriel.footbook.classes.CustomSharedPrefAdapter;
import com.app.galnoriel.footbook.classes.Game;
import com.app.galnoriel.footbook.classes.GroupPlay;
import com.app.galnoriel.footbook.classes.Player;
import com.app.galnoriel.footbook.fragments.ProfileFragment;
import com.app.galnoriel.footbook.fragments.GroupFragment;
import com.app.galnoriel.footbook.fragments.GameFragment;
import com.app.galnoriel.footbook.fragments.SearchGameFieldFragment;

import com.app.galnoriel.footbook.interfaces.AccessGroupDB;
import com.app.galnoriel.footbook.interfaces.GetGameFromMain;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MoveToTab, AccessGroupDB, AccessPlayerDB, GetGameFromMain, ViewPager.OnPageChangeListener {
    //region interfaces

    public MainToPlayerFrag sendToPlayerFrag;
    public MainToGroupFrag sendToGroupFrag;
    public GetGameFromMain sendToGameFrag;
    public static final int TAB_PROFILE = 0;
    public static final int TAB_GROUP = 1;
    public static final int TAB_GAME = 2;
    public static final int TAB_MAP= 3;

    //endregion

    //region global declarations

    private Game nextGame;
    public GroupPlay displayingGroup;
    private SectionsAdapter sectionsAdapter;
    private ViewPager viewPager;
    private Context context;
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
    //    public MainToGameFrag sendGametoFrag;
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
            ((GameFragment)fragment). nextGameFromMain = this;


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
        context = this;


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
                    loginTV.setText(getResources().getString(R.string.wellcom));
                    userLoginTV.setText(currentUser.getDisplayName());
                    navigationView.getMenu().findItem(R.id.sign_in).setVisible(false);
                    navigationView.getMenu().findItem(R.id.sign_up).setVisible(false);
                    navigationView.getMenu().findItem(R.id.reset_password).setVisible(false);
                    navigationView.getMenu().findItem(R.id.sign_out).setVisible(true);

                }
                else { //signed out
                    sharedPref.setLoginStatus(false);
                    sharedPref.removeCurrentUserInfo();
                    loginTV.setText(getResources().getString(R.string.login_please));
                    userLoginTV.setText(getResources().getString(R.string.wait_for_you));
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
        viewPager.addOnPageChangeListener(this);

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
                                Snackbar.make(coordinatorLayout, getResources().getString(R.string.check_your_email),
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
        Snackbar.make(coordinatorLayout, getResources().getString(R.string.bye_bye) +" " + currentUser.getDisplayName(), Snackbar.LENGTH_SHORT).show();
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
                                    Snackbar.make(coordinatorLayout, getResources().getString(R.string.sign_in_succ), Snackbar.LENGTH_SHORT).show();
                                }
                                else {
                                    Snackbar.make(coordinatorLayout, getResources().getString(R.string.sign_in_fail), Snackbar.LENGTH_SHORT).show();
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
                                    Snackbar.make(coordinatorLayout, getResources().getString(R.string.sign_up_succ), Snackbar.LENGTH_SHORT).show();
                                }
                                else {
                                    Snackbar.make(coordinatorLayout, getResources().getString(R.string.sign_up_fail), Snackbar.LENGTH_SHORT).show();
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
                            Snackbar.make(coordinatorLayout, firebaseAuth.getCurrentUser().getDisplayName() + " "
                                    +getResources().getString(R.string.wellcom),Snackbar.LENGTH_SHORT).show();
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
            emailLayout.setError(getResources().getString(R.string.field_not_empty));
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            emailLayout.setError(getResources().getString(R.string.valid_email));
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
            userNameLayout.setError(getResources().getString(R.string.field_not_empty));
            return false;
        }
        else if (usernameInput.length() > 20){
            userNameLayout.setError(getResources().getString(R.string.name_too_long));
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
            passwordLayout.setError(getResources().getString(R.string.field_not_empty));
            return false;
        }else if(!PASSWORD_PATTERN.matcher(password).matches()){
            passwordLayout.setError(getResources().getString(R.string.password_must_be));
            return false;
        }
        else if (PASSWORD_PATTERN.matcher(password).matches() && !password.equals(confirmPassword)){
            passwordLayout.setError(null);
            passwordConfirmLayout.setError(getResources().getString(R.string.missmatch_passwords));
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
            passwordLayout.setError(getResources().getString(R.string.field_not_empty));
            return false;
        }else if(!PASSWORD_PATTERN.matcher(password).matches()){
            passwordLayout.setError(getResources().getString(R.string.password_must_be));
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

                viewPager.setCurrentItem(TAB_GAME,true);
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
                        Snackbar.make(coordinatorLayout, getResources().getString(R.string.group_created) + group.getName(), Snackbar.LENGTH_SHORT).show();
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
        displayingGroup = group;
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
        try {
            db.collection(GlobConst.DB_GROUP_TABLE).document(group_id)//get group
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult().exists()) {
                                GroupPlay group = new GroupPlay(task.getResult());
                                if (frag == TAB_PROFILE)
                                    sendToPlayerFrag.onGetGroupComplete(group);
                                if (frag == TAB_GROUP) {
                                    sendToGroupFrag.onGetGroupComplete(group);
                                    if (group.getNextGame() == null)
                                        group.setNextGame(new Game());
                                    nextGame = group.getNextGame();

                                    displayingGroup = group;
                                }
                            }
                        }
                    });
        }catch (Exception e){Log.e("requestGroupFromServer","FAILED For: "+frag);}
        return null;
    }

    @Override
    public void setDisplayGroupInMain(GroupPlay groupFromView) {
        this.displayingGroup = groupFromView;
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

    @Override
    public Game onNextGameRequset() {
        return nextGame;
    }

    @Override
    public void openGroupQueryDialog() {
        Log.d("openPlayerQueryDialog", "started querrying");
        //region prepare dialog
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_search, null);
        final List<Map<String, String>> resultList = new ArrayList<>();
        final EditText searchED = dialogView.findViewById(R.id.search_ed_sea_dia);
        ImageView searchBTN = dialogView.findViewById(R.id.search_ic_sea_dia);
        final ListView resultLV = dialogView.findViewById(R.id.result_lv_sea_dia);
        final String[] from = {GlobConst.DB_GROUP_ID,GlobConst.DB_GROUP_NAME,GlobConst.DB_GROUP_WHEREPLAY};
        final int[] to = {R.id.id_result_list,R.id.name_result_list,R.id.sub_title_result_list};
        //endregion
        //region watch on click from result list
        resultLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //clicking on item from result will display group
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String groupId = ((TextView)view.findViewById(R.id.id_result_list)).getText().toString();
//                requestGroupFromServer(groupId,TAB_GROUP);
                goToFrag(TAB_GROUP,groupId);
                alertDialog.dismiss();
            }
        });
        //endregion

// Create a query against the collection.
// search button start query
        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resultList.clear();
                Query query = db.collection(GlobConst.DB_GROUP_TABLE).whereEqualTo(GlobConst.DB_GROUP_NAME, searchED.getText()+"");
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                            for (DocumentSnapshot p:task.getResult().getDocuments()) {
                                HashMap<String,String> result = new HashMap<>();
                                result.put(GlobConst.DB_GROUP_NAME,p.getString(GlobConst.DB_USER_NAME));
                                result.put(GlobConst.DB_GROUP_ID,p.getId());
                                result.put(GlobConst.DB_GROUP_WHEREPLAY,p.getString(GlobConst.DB_GROUP_WHEREPLAY));
                                Log.d("QUERRY COMPLETED!",p.getData()+"  "+p.getId());
                                resultList.add(result);
                            }
                        SimpleAdapter adapter = new SimpleAdapter(context,resultList,R.layout.list_item_query,from,to);
                        resultLV.setAdapter(adapter);
                    }
                });
            }
        });
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void openPlayerQueryDialog() {
        Log.d("openPlayerQueryDialog", "started querrying");
        //region prepare dialog
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_search, null);
        final List<Map<String, String>> resultList = new ArrayList<>();
        final EditText searchED = dialogView.findViewById(R.id.search_ed_sea_dia);
        ImageView searchBTN = dialogView.findViewById(R.id.search_ic_sea_dia);
        final ListView resultLV = dialogView.findViewById(R.id.result_lv_sea_dia);
        final String[] from = {GlobConst.DB_USER_ID,GlobConst.DB_USER_NAME,GlobConst.DB_USER_POSITION};
        final int[] to = {R.id.id_result_list,R.id.name_result_list,R.id.sub_title_result_list};
        //endregion
        //region add member on click from result list
        resultLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //clicking on item from result will add to group
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sendToGroupFrag.addMemberToGroup(((TextView)view.findViewById(R.id.id_result_list)).getText().toString());
                alertDialog.dismiss();
            }
        });
        //endregion

// Create a query against the collection.
// search button start query
        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resultList.clear();
                Query query = db.collection(GlobConst.DB_USER_TABLE).whereEqualTo(GlobConst.DB_USER_NAME, searchED.getText()+"");
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                            for (DocumentSnapshot p:task.getResult().getDocuments()) {
                                HashMap<String,String> result = new HashMap<>();
                                result.put(GlobConst.DB_USER_NAME,p.getString(GlobConst.DB_USER_NAME));
                                result.put(GlobConst.DB_USER_ID,p.getId());
                                result.put(GlobConst.DB_USER_POSITION,p.getString(GlobConst.DB_USER_POSITION));
                                Log.d("QUERRY COMPLETED!",p.getData()+"  "+p.getId());
                                resultList.add(result);
                            }
                        SimpleAdapter adapter = new SimpleAdapter(context,resultList,R.layout.list_item_query,from,to);
                        resultLV.setAdapter(adapter);
                    }
                });
            }
        });
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.show();
    }

    //endregion

    @Override
    public void onPageScrolled(int i, float v, int i1) {

        switch (i){
            case TAB_GROUP: //save group to main from view
                sendToGroupFrag.callUpdateGroupFromMain();
                if (displayingGroup != null)
                    nextGame = displayingGroup.getNextGame();
                break;
            case TAB_PROFILE: //save profile from view
                sendToPlayerFrag.callUpdatePlayerFromMain();
                break;
            case TAB_GAME: break;

        }
    }

    @Override
    public void onPageSelected(int i) {
        if (displayingGroup != null)
            nextGame = displayingGroup.getNextGame();
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPref.clearDisplayProfileInfo();
        sharedPref.clearDisplayProfileInfo();
    }
}
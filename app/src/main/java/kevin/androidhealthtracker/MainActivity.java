package kevin.androidhealthtracker;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.web.client.RestTemplate;

import kevin.androidhealthtracker.fragments.FragmentThree;
import kevin.androidhealthtracker.fragments.FragmentTwo;
import kevin.androidhealthtracker.fragments.UserFeedFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static SharedPreferences prefs;
    public static WebClient client;
    private static final int LOGIN_REQUEST_CODE = 0;

    private FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
    private Fragment fragment;
    private String sessionToken;
    private String userName;
    private DrawerLayout drawer;
    private TextView name;
    private View headerView;
    private NavigationView navigationView;
    private int userId;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new WebClient(new RestTemplate(), "10.0.2.2", 8080);
        prefs = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        sessionToken = autoLoginPreviousUser();
        setContentView(R.layout.main);

        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        drawer = findViewById(R.id.drawer_layout);
        name = headerView.findViewById(R.id.userName);

        bottomNavigationView.setSelectedItemId(R.id.fragment_home);
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        headerView.setOnClickListener(loginOnClickListener);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        setSupportActionBar(toolbar);
        loadMainFragment();
        setUserToTextView();
    }

    /*
     * Fragment switcher
     */
    @NonNull
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();
            Class fragmentClass = null;
            if (id == R.id.fragment_home) {
                fragmentClass = UserFeedFragment.class;
                //Todo: From fragment home/news feed once you go into replies that will replace this fragment, therefore must
                //add the old fragment to backstack
            } else if (id == R.id.fragment2) {
                fragmentClass = FragmentTwo.class;
            } else if (id == R.id.fragment3) {
                fragmentClass = FragmentThree.class;
            }

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (IllegalAccessException | InstantiationException | NullPointerException e) {
                e.printStackTrace();
            }
            transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commit();
            return true;
        }
    };

    /*
     * Start login activity
     */
    private View.OnClickListener loginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Todo if user = logged in, enable log out option via pop up ok and cancel
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(login, LOGIN_REQUEST_CODE);
        }
    };

    private void loadMainFragment() {
        transaction = getFragmentManager().beginTransaction();
        try {
            transaction.replace(R.id.fragment_container, UserFeedFragment.class.newInstance()).commit();
        } catch (InstantiationException | IllegalAccessException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void setUserToTextView() {
        userName = prefs.getString("userName", null);
        userId = prefs.getInt("userId", 0);
        name.setText("Username: " + userName + " ID: " + userId);
    }

    private String autoLoginPreviousUser() {
        //Get user name from shared preferences
        //Get password from shared preferences
        //Authenticate user from server
        //Return session id
        return null;
    }

    /*
     * After user login success
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                userName = prefs.getString("userName", null);
                userId = prefs.getInt("userId", 0);
                Toast loggedIn = new Toast(this);
                loggedIn.makeText(this, userName + " " + userId, Toast.LENGTH_LONG).show();
                setUserToTextView();
                loadMainFragment();
            }
        }
    }

    /*
     * Opens drawer on hamburger icon press
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * Closes drawer when item is pressed
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
     * Closer drawer on back button press
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}


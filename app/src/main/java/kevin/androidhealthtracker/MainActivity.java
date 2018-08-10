package kevin.androidhealthtracker;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
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
import android.widget.Toast;


import kevin.androidhealthtracker.fragments.FragmentHome;
import kevin.androidhealthtracker.fragments.FragmentThree;
import kevin.androidhealthtracker.fragments.FragmentTwo;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
    private Fragment fragment;
    private String sessionToken;
    private Boolean loggedInStatus;
    private String userName;
    private int userId;
    private static final int LOGIN_REQUEST_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup items
        sessionToken = autoLoginPreviousUser();
        setContentView(R.layout.main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.fragment_home);
        View headerView = navigationView.getHeaderView(0);
        setSupportActionBar(toolbar);
        loadMainFragment();

        //Setup listeners
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        headerView.setOnClickListener(loginOnClickListener);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


    }

    private String autoLoginPreviousUser() {
        //Get user name from shared preferences
        //Get password from shared preferences
        //Authenticate user from server
        //Return session id
        return null;
    }

    private void loadMainFragment() {
        transaction = getFragmentManager().beginTransaction();
        try {
            transaction.replace(R.id.fragment_container, FragmentHome.class.newInstance()).commit();
        } catch (InstantiationException | IllegalAccessException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();
            Class fragmentClass = null;
            if (id == R.id.fragment_home) {
                fragmentClass = FragmentHome.class;
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

    /**
     * Start login activity
     */
    private View.OnClickListener loginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(login, LOGIN_REQUEST_CODE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                loggedInStatus = data.getBooleanExtra("userLoggedInStatus", false);
                userName = data.getStringExtra("userName");
                userId = data.getIntExtra("userId", 0);
                Toast loggedIn = new Toast(this);
                loggedIn.makeText(this, loggedInStatus.toString() + " " + userName + " " + userId, Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

}


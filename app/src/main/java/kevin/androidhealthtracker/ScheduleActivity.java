package kevin.androidhealthtracker;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.kevin.healthtracker.datamodels.Friend;
import com.kevin.healthtracker.datamodels.Schedule;
import com.kevin.healthtracker.datamodels.User;

import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import kevin.androidhealthtracker.adapters.FriendsListAdapter;
import kevin.androidhealthtracker.fragments.AddFriendFragment;
import kevin.androidhealthtracker.fragments.DeleteFriendFragment;
import kevin.androidhealthtracker.fragments.RespondFriendFragment;

public class ScheduleActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ListView scheduleListView;
    private WebClient client;
    private SharedPreferences prefs;

    private int userId;
    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO create layout
        setContentView(R.layout.activity_friend_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        client = MainActivity.client;
        prefs = MainActivity.prefs;
        userId = prefs.getInt("userId", 0);
        userName = prefs.getString("userName", null);

        bottomNavigationView = findViewById(R.id.scheduleList_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.scheduleItem);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        scheduleListView = findViewById(R.id.scheduleListView);
        scheduleListView.setOnItemClickListener(ListViewOnItemClickListener);
        populateListSchedule();
    }

    private void populateListSchedule() {
        GetAllSchedules getAllSchedules = new GetAllSchedules();
        getAllSchedules.execute();
        setTitle(R.string.Schedule);
    }

    private void populateListIncoming() {
        GetIncoming getIncoming = new GetIncoming();
        getIncoming.execute();
        setTitle(R.string.incoming_requests);
    }

    private void populateListOutgoing() {
        GetOutgoing getOutgoing = new GetOutgoing();
        getOutgoing.execute();
        setTitle(R.string.outgoing_requests);
    }

    private void showRespondDialog(int user1, int user2) {
        Bundle args = new Bundle();
        args.putInt("user1", user1);
        args.putInt("user2", user2);

        FragmentManager fragmentManager = getFragmentManager();
        RespondFriendFragment respondFriendFragment = new RespondFriendFragment();
        respondFriendFragment.show(fragmentManager, "respond");
        respondFriendFragment.setArguments(args);
    }

    private void showDeleteDialog(int user1, int user2) {
        Bundle args = new Bundle();
        args.putInt("user1", user1);
        args.putInt("user2", user2);

        FragmentManager fragmentManager = getFragmentManager();
        DeleteFriendFragment deleteFriendFragment = new DeleteFriendFragment();
        deleteFriendFragment.show(fragmentManager, "delete");

        deleteFriendFragment.setArguments(args);
    }

    private ListView.OnItemClickListener ListViewOnItemClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (bottomNavigationView.getSelectedItemId() == R.id.incomingFriendsItem) {
                User user = (User) adapterView.getItemAtPosition(i);
                showRespondDialog(userId, user.getId());
                populateListIncoming();
            }
            else if (bottomNavigationView.getSelectedItemId() == R.id.myFriendsItem) {
                User user = (User) adapterView.getItemAtPosition(i);
                showDeleteDialog(userId, user.getId());
            }
        }
    };

    @NonNull
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();
            if (id == R.id.scheduleItem) {
                populateListSchedule();
            } else if (id == R.id.incomingScheduleItem) {
                populateListIncoming();
            } else if (id == R.id.outgoingScheduleItem) {
                populateListOutgoing();
            }
            return true;
        }
    };

    public class GetAllSchedules extends AsyncTask<Void, Void, Boolean> {
        List<Schedule> schedules = new ArrayList<>();

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                schedules = Arrays.asList(client.getAllSchedule(userId));
            } catch (RestClientException e) {
                Toast error = new Toast(ScheduleActivity.this);
                Toast.makeText(ScheduleActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                FriendsListAdapter friendsListAdapter = new FriendsListAdapter(getApplicationContext(), sch);
                scheduleListView.setAdapter(friendsListAdapter);
            }
        }
    }

    public class GetIncoming extends AsyncTask<Void, Void, Boolean> {
        List<Schedule> schedules = new ArrayList<>();

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                schedules = Arrays.asList(client.getInboundSchedule(userId));

            } catch (RestClientException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                FriendsListAdapter friendsListAdapter = new FriendsListAdapter(getApplicationContext(), users);
                scheduleListView.setAdapter(friendsListAdapter);
            }
        }
    }

    public class GetOutgoing extends AsyncTask<Void, Void, Boolean> {
        List<Schedule> schedules = new ArrayList<>();
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                schedules = Arrays.asList(client.getOutboundSchedule(userId));

            } catch (RestClientException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                FriendsListAdapter friendsListAdapter = new FriendsListAdapter(getApplicationContext(), users);
                scheduleListView.setAdapter(friendsListAdapter);
            }
        }
    }

}

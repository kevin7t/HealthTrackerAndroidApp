package kevin.androidhealthtracker;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kevin.healthtracker.datamodels.RequestStatus;
import com.kevin.healthtracker.datamodels.Schedule;

import org.springframework.web.client.RestClientException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kevin.androidhealthtracker.adapters.ScheduleListAdapter;
import kevin.androidhealthtracker.fragments.RespondScheduleFragment;

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
        setContentView(R.layout.activity_schedule_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        client = MainActivity.client;
        prefs = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        userId = prefs.getInt("userId", 0);
        userName = prefs.getString("userName", null);

        bottomNavigationView = findViewById(R.id.scheduleList_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.scheduleItem);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        FloatingActionButton floatingActionButton = findViewById(R.id.scheduleFab);
        floatingActionButton.setOnClickListener(fabListener);

        scheduleListView = findViewById(R.id.scheduleListView);
        scheduleListView.setOnItemClickListener(listViewOnItemClickListener);
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

    private void showRespondDialog(int scheduleId) {
        Bundle args = new Bundle();
        args.putInt("scheduleId", scheduleId);

        FragmentManager fragmentManager = getFragmentManager();
        RespondScheduleFragment respondScheduleFragment = new RespondScheduleFragment();
        respondScheduleFragment.show(fragmentManager, "respond");
        respondScheduleFragment.setArguments(args);
    }

    private FloatingActionButton.OnClickListener fabListener = view -> {
        Intent createScheduleIntent = new Intent(ScheduleActivity.this, CreateScheduleActivity.class);
        startActivity(createScheduleIntent);
    };

    private ListView.OnItemClickListener listViewOnItemClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Schedule schedule = (Schedule) adapterView.getItemAtPosition(i);
            if (bottomNavigationView.getSelectedItemId() == R.id.incomingScheduleItem) {
                showRespondDialog(schedule.getId());
                populateListIncoming();
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
                Arrays.asList(client.getAllSchedule(userId)).forEach(schedule -> {
                    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                    if (schedule.getDateTime().after(currentTime)) {
                        schedules.add(schedule);
                    }
                });
            } catch (RestClientException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                ScheduleListAdapter scheduleListAdapter = new ScheduleListAdapter(getApplicationContext(), schedules, userId);
                scheduleListView.setAdapter(scheduleListAdapter);
            }
        }
    }

    public class GetIncoming extends AsyncTask<Void, Void, Boolean> {
        List<Schedule> schedules = new ArrayList<>();

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Arrays.asList(client.getInboundSchedule(userId)).forEach(schedule -> {
                    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                    if (schedule.getDateTime().after(currentTime)) {
                        schedules.add(schedule);
                    }
                });

            } catch (RestClientException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                ScheduleListAdapter scheduleListAdapter = new ScheduleListAdapter(getApplicationContext(), schedules, userId);
                scheduleListView.setAdapter(scheduleListAdapter);
            }
        }
    }

    public class GetOutgoing extends AsyncTask<Void, Void, Boolean> {
        List<Schedule> schedules = new ArrayList<>();

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Arrays.asList(client.getOutboundSchedule(userId)).forEach(schedule -> {
                    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                    if (schedule.getDateTime().after(currentTime)) {
                        schedules.add(schedule);
                    }
                });

            } catch (RestClientException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                ScheduleListAdapter scheduleListAdapter = new ScheduleListAdapter(getApplicationContext(), schedules, userId);
                scheduleListView.setAdapter(scheduleListAdapter);
            }
        }
    }


}

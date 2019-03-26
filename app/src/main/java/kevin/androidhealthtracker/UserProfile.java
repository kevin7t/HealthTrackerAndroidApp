package kevin.androidhealthtracker;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.kevin.healthtracker.datamodels.dto.StatusDTO;

import org.springframework.web.client.ResourceAccessException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kevin.androidhealthtracker.adapters.StatusListAdapter;
import kevin.androidhealthtracker.fragments.ProfileFeedFragment;

public class UserProfile extends AppCompatActivity {
    private List<StatusDTO> statusList;
    private ProfileFeedRefreshTask profileFeedRefreshTask;
    private WebClient client;
    private ListView listView;
    private int profileId;
    private String profileName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        profileId=getIntent().getExtras().getInt("profileId");
        profileName = getIntent().getExtras().getString("profileName");
        client = MainActivity.client;
        listView = findViewById(R.id.profileListview);
        setTitle(profileName);
        refreshUserFeed();
    }

    private void refreshUserFeed() {
        profileFeedRefreshTask = new ProfileFeedRefreshTask();
        profileFeedRefreshTask.execute();

    }

    private class ProfileFeedRefreshTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                statusList = Arrays.asList(client.getStatusFromUser(profileId, 1));
            } catch (ResourceAccessException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            try {
                Collections.sort(statusList, Comparator.comparing(StatusDTO::getId).reversed());
                StatusListAdapter customListViewAdapter = new StatusListAdapter(UserProfile.this, statusList);
                listView.setAdapter(customListViewAdapter);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }
}


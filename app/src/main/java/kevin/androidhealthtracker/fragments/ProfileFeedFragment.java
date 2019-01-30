package kevin.androidhealthtracker.fragments;

import android.os.AsyncTask;

import com.kevin.healthtracker.datamodels.dto.StatusDTO;

import org.springframework.web.client.ResourceAccessException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import kevin.androidhealthtracker.adapters.StatusListAdapter;

public class ProfileFeedFragment extends UserFeedFragment {
    protected ProfileFeedRefreshTask userFeedRefreshTask;


    @Override
    protected void refreshUserFeed() {
        if (prefs.getInt("userId", 0) != 0) {
            userFeedRefreshTask = new ProfileFeedRefreshTask();
            userFeedRefreshTask.execute();
        }
    }

    private class ProfileFeedRefreshTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                statusList = Arrays.asList(client.getStatusFromUser(prefs.getInt("userId", 0), 1));
            } catch (ResourceAccessException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            try {
                Collections.sort(statusList, Comparator.comparing(StatusDTO::getCreatedAt).reversed());
                StatusListAdapter customListViewAdapter = new StatusListAdapter(getActivity(), statusList);
                listView.setAdapter(customListViewAdapter);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }
}

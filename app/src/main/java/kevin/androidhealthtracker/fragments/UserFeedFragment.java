package kevin.androidhealthtracker.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kevin.healthtracker.datamodels.dto.StatusDTO;

import org.springframework.web.client.ResourceAccessException;

import java.util.Arrays;

import kevin.androidhealthtracker.MainActivity;
import kevin.androidhealthtracker.R;
import kevin.androidhealthtracker.WebClient;

public class UserFeedFragment extends Fragment {

    private UserFeedRefreshTask userFeedRefreshTask;
    private WebClient client;
    private SharedPreferences prefs;
    private StatusDTO[] statusList;

    private View view;
    private ListView listView;
    private ListAdapter listAdapter;

    private FloatingActionButton floatingActionButton;
    private SwipeRefreshLayout homeSwipeRefreshLayout;
    /*
     * Listview refresh actions
     */
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshUserFeed();
            homeSwipeRefreshLayout.setRefreshing(false);
        }
    };
    /*
     * Listview scrolling actions
     */
    private AbsListView.OnScrollListener listViewListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem > 0) {
                floatingActionButton.hide();
            }
            if (firstVisibleItem == 0) {
                floatingActionButton.show();
            }
        }
    };
    /*
     * Floating button actions
     */
    private View.OnClickListener floatingActionButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        client = MainActivity.client;
        prefs = MainActivity.prefs;

        view = inflater.inflate(R.layout.fragment_home, container, false);
        floatingActionButton = view.findViewById(R.id.fab);
        listView = view.findViewById(R.id.fragment_home_list);
        homeSwipeRefreshLayout = view.findViewById(R.id.home_swipe_layout);

        homeSwipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);
        floatingActionButton.setOnClickListener(floatingActionButtonListener);
        listView.setOnScrollListener(listViewListener);

        refreshUserFeed();
        return view;
    }

    private void refreshUserFeed() {
        if (prefs.getInt("userId", 0) != 0) {
            userFeedRefreshTask = new UserFeedRefreshTask();
            userFeedRefreshTask.execute();
        }
    }

    private class UserFeedRefreshTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                statusList = client.getStatusFromFriendsForUser(prefs.getInt("userId", 0), 1);
            } catch (ResourceAccessException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            try {
                listAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, Arrays.asList(statusList));
                listView.setAdapter(listAdapter);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }
}
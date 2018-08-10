package kevin.androidhealthtracker.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kevin.healthtracker.datamodels.dto.StatusDTO;

import java.util.Arrays;

import kevin.androidhealthtracker.MainActivity;
import kevin.androidhealthtracker.R;
import kevin.androidhealthtracker.WebClient;

public class FragmentHome extends Fragment {

    private UserFeedRefreshTask userFeedRefreshTask;
    private WebClient client;
    private SharedPreferences prefs;
    private ListView listView;
    private ListAdapter listAdapter;
    private StatusDTO[] statusList;
    private FloatingActionButton floatingActionButton;
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

        client = ((MainActivity) this.getActivity()).client;
        prefs = ((MainActivity) this.getActivity()).prefs;

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        floatingActionButton = view.findViewById(R.id.fab);
        listView = view.findViewById(R.id.fragment_home_list);


        floatingActionButton.setOnClickListener(floatingActionButtonListener);
        listView.setOnScrollListener(listViewListener);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        userFeedRefreshTask = new UserFeedRefreshTask();
        userFeedRefreshTask.execute();
    }

    public class UserFeedRefreshTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            statusList = client.getStatusFromFriendsForUser(prefs.getInt("userId", 0), 1);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            listAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, Arrays.asList(statusList));
            listView.setAdapter(listAdapter);
        }
    }
}

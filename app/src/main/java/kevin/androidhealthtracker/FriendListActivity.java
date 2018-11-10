package kevin.androidhealthtracker;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.kevin.healthtracker.datamodels.Friend;
import com.kevin.healthtracker.datamodels.User;

import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FriendListActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private SearchView searchView;
    private ListView friendListView;
    private List<Friend> friendsList;
    private WebClient client;
    private SharedPreferences prefs;

    private int userId;
    private String userName;

    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        client = MainActivity.client;
        prefs = MainActivity.prefs;
        userId = prefs.getInt("userId", 0);
        userName = prefs.getString("userName", null);

        bottomNavigationView = findViewById(R.id.friendlist_bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.myFriendsItem);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        friendListView = findViewById(R.id.friendsListview);
        populateListFriends();

    }

    private void populateListFriends() {
        GetAllFriendsTask getAllFriendsTask = new GetAllFriendsTask();
        getAllFriendsTask.execute();
        setTitle(R.string.my_friends);
    }

    private void populateListIncoming() {
        GetIncomingFriends getIncomingFriends = new GetIncomingFriends();
        getIncomingFriends.execute();
        setTitle(R.string.incoming_requests);
    }

    private void populateListOutgoing(){
        GetOutgoingFriends getOutgoingFriends = new GetOutgoingFriends();
        getOutgoingFriends.execute();
        setTitle(R.string.outgoing_requests);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friend_list, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(onQueryTextListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            GetUserByName getUserByName = new GetUserByName(query);
            getUserByName.execute();
            setTitle(R.string.search_friend);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    @NonNull
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();
            if (id == R.id.myFriendsItem) {
                populateListFriends();
            } else if (id == R.id.incomingFriendsItem) {
                populateListIncoming();
            }else if (id == R.id.outgoingFriendsItem){
                populateListOutgoing();
            }
            return true;
        }
    };

    public class GetAllFriendsTask extends AsyncTask<Void, Void, Boolean> {
         List<String> userNames = new ArrayList<>();

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                userNames = Arrays.asList(client.getAllFriends(userId)).stream().map(user -> user.getUserName()).collect(Collectors.toList());
            } catch (RestClientException e) {
                Toast error = new Toast(FriendListActivity.this);
                Toast.makeText(FriendListActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                arrayAdapter = new ArrayAdapter<>(FriendListActivity.this, R.layout.all_friends_listview_item, userNames);
                friendListView.setAdapter(arrayAdapter);
            }
        }
    }

    public class GetIncomingFriends extends AsyncTask<Void, Void, Boolean> {
        Map<Integer,String> user = new HashMap<>();

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                List<Friend> friends = Arrays.asList(client.getInboundOutboundRequests(userId));
                Iterator iterator = friends.iterator();
                while(iterator.hasNext()){
                    Friend friend = (Friend) iterator.next();
                    if (friend.getUserActionId() != userId && friend.getUser1().getId() != userId){
                        user.put(friend.getUser1().getId(),friend.getUser1().getUserName());
                    }else if (friend.getUserActionId() != userId && friend.getUser2().getId() != userId){
                        user.put(friend.getUser2().getId(),friend.getUser2().getUserName());
                    }
                }

            } catch (RestClientException e) {
                Toast error = new Toast(FriendListActivity.this);
                Toast.makeText(FriendListActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                final ArrayList<String> userNames = new ArrayList<>();
                userNames.addAll(user.values());
                arrayAdapter = new ArrayAdapter<>(FriendListActivity.this, R.layout.all_friends_listview_item, userNames);
                friendListView.setAdapter(arrayAdapter);
            }
        }
    }
    public class GetOutgoingFriends extends AsyncTask<Void, Void, Boolean> {
        Map<Integer,String> user = new HashMap<>();

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                List<Friend> friends = Arrays.asList(client.getInboundOutboundRequests(userId));
                Iterator iterator = friends.iterator();
                while(iterator.hasNext()){
                    Friend friend = (Friend) iterator.next();
                    if (friend.getUserActionId() == userId && friend.getUser1().getId() != userId){
                        user.put(friend.getUser1().getId(),friend.getUser1().getUserName());
                    }else if (friend.getUserActionId() == userId && friend.getUser2().getId() != userId){
                        user.put(friend.getUser2().getId(),friend.getUser2().getUserName());
                    }
                }

            } catch (RestClientException e) {
                Toast error = new Toast(FriendListActivity.this);
                Toast.makeText(FriendListActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                final ArrayList<String> userNames = new ArrayList<>();
                userNames.addAll(user.values());
                arrayAdapter = new ArrayAdapter<>(FriendListActivity.this, R.layout.all_friends_listview_item, userNames);
                friendListView.setAdapter(arrayAdapter);
            }
        }
    }

    public class GetUserByName extends AsyncTask<Void, Void, Boolean> {
        String userName;
        User user;
        GetUserByName(String userName) {
            this.userName = userName;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                user = client.getUserByUserName(userName);
            } catch (RestClientException e) {
                //Cannot call toast on thread that has not called looper prepare
//                Toast error = new Toast(FriendListActivity.this);
//                Toast.makeText(FriendListActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                final ArrayList<String> userNames = new ArrayList<>();
                userNames.add(user.getUserName());
                arrayAdapter = new ArrayAdapter<>(FriendListActivity.this, R.layout.all_friends_listview_item, userNames);
                friendListView.setAdapter(arrayAdapter);
            }
        }
    }
}

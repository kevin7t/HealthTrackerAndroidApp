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
import android.widget.SearchView;
import android.widget.Toast;

import com.kevin.healthtracker.datamodels.Friend;
import com.kevin.healthtracker.datamodels.User;

import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kevin.androidhealthtracker.adapters.FriendsListAdapter;
import kevin.androidhealthtracker.fragments.AddFriendFragment;
import kevin.androidhealthtracker.fragments.DeleteFriendFragment;
import kevin.androidhealthtracker.fragments.RespondFriendFragment;

public class FriendListActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private SearchView searchView;
    private ListView friendListView;
    private WebClient client;
    private SharedPreferences prefs;

    private int userId;
    private String userName;

    private Boolean searchViewActivated = false;

    private Map<Integer, User> searchFriends = new HashMap<>();


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
        friendListView.setOnItemClickListener(friendListOnItemClickListener);
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

    private void populateListOutgoing() {
        GetOutgoingFriends getOutgoingFriends = new GetOutgoingFriends();
        getOutgoingFriends.execute();
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

    private void showAddDialog(int user1, int user2) {
        Bundle args = new Bundle();
        args.putInt("user1", user1);
        args.putInt("user2", user2);

        FragmentManager fragmentManager = getFragmentManager();
        AddFriendFragment addFriendFragment = new AddFriendFragment();
        addFriendFragment.show(fragmentManager, "add");

        addFriendFragment.setArguments(args);
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

    private ListView.OnItemClickListener friendListOnItemClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (bottomNavigationView.getSelectedItemId() == R.id.incomingFriendsItem) {
                User user = (User) adapterView.getItemAtPosition(i);
                showRespondDialog(userId, user.getId());
                populateListIncoming();
            } else if (searchViewActivated) {
                User user = (User) adapterView.getItemAtPosition(i);
                showAddDialog(userId, user.getId());
            } else if (bottomNavigationView.getSelectedItemId() == R.id.myFriendsItem) {
                User user = (User) adapterView.getItemAtPosition(i);
                showDeleteDialog(userId, user.getId());
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friend_list, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(onQueryTextListener);
        searchView.setOnCloseListener(onCloseListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private SearchView.OnCloseListener onCloseListener = new SearchView.OnCloseListener() {
        @Override
        public boolean onClose() {
            System.out.println("SEARCHVIEW CLOSED ");
            searchViewActivated = false;
            return false;
        }
    };

    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            GetUserByName getUserByName = new GetUserByName(query);
            getUserByName.execute();
            setTitle(R.string.search_friend);
            searchViewActivated = true;
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
            } else if (id == R.id.outgoingFriendsItem) {
                populateListOutgoing();
            }
            return true;
        }
    };

    public class GetAllFriendsTask extends AsyncTask<Void, Void, Boolean> {
        List<User> users = new ArrayList<>();

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
//                userNames = Arrays.asList(client.getAllFriends(userId)).stream().map(user -> user.getUserName()).collect(Collectors.toList());
                users = Arrays.asList(client.getAllFriends(userId));
            } catch (RestClientException e) {
                Toast error = new Toast(FriendListActivity.this);
                Toast.makeText(FriendListActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Collections.sort(users, Comparator.comparing(User::getScore).reversed());
                FriendsListAdapter friendsListAdapter = new FriendsListAdapter(getApplicationContext(), users);
                friendListView.setAdapter(friendsListAdapter);
            }
        }
    }

    public class GetIncomingFriends extends AsyncTask<Void, Void, Boolean> {
        List<User> users = new ArrayList<>();

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                List<Friend> friends = Arrays.asList(client.getInboundOutboundRequests(userId));
                Iterator iterator = friends.iterator();
                while (iterator.hasNext()) {
                    Friend friend = (Friend) iterator.next();
                    if (friend.getUserActionId() != userId && friend.getUser1().getId() != userId) {
                        users.add(friend.getUser1());
                    } else if (friend.getUserActionId() != userId && friend.getUser2().getId() != userId) {
                        users.add(friend.getUser2());
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
                FriendsListAdapter friendsListAdapter = new FriendsListAdapter(getApplicationContext(), users);
                friendListView.setAdapter(friendsListAdapter);
            }
        }
    }

    public class GetOutgoingFriends extends AsyncTask<Void, Void, Boolean> {
        List<User> users = new ArrayList<>();
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                List<Friend> friends = Arrays.asList(client.getInboundOutboundRequests(userId));
                Iterator iterator = friends.iterator();
                while (iterator.hasNext()) {
                    Friend friend = (Friend) iterator.next();
                    if (friend.getUserActionId() == userId && friend.getUser1().getId() != userId) {
                        users.add(friend.getUser1());
                    } else if (friend.getUserActionId() == userId && friend.getUser2().getId() != userId) {
                        users.add(friend.getUser2());
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
                FriendsListAdapter friendsListAdapter = new FriendsListAdapter(getApplicationContext(), users);
                friendListView.setAdapter(friendsListAdapter);
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
                searchFriends.put(user.getId(), user);
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
                FriendsListAdapter friendsListAdapter = new FriendsListAdapter(getApplicationContext(), Collections.singletonList(user));
                friendListView.setAdapter(friendsListAdapter);
            }
        }
    }

}

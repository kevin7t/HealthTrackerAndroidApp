package kevin.androidhealthtracker.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kevin.healthtracker.datamodels.User;

import java.util.List;

import kevin.androidhealthtracker.R;


public class FriendsListAdapter extends ArrayAdapter<User> {

    private Activity context;
    private List<User> userList;

    public FriendsListAdapter(@NonNull Activity context, List<User> users) {
        super(context, R.layout.status_listview_item, users);
        this.userList = users;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        convertView = layoutInflater.inflate(R.layout.all_friends_listview_item, null, true);

        TextView userNameTextView = convertView.findViewById(R.id.userNameFriendTextView);
        TextView userScoreTextView = convertView.findViewById(R.id.userScoreView);

        userNameTextView.setText(userList.get(position).getUserName().toString());
        userScoreTextView.setText(userList.get(position).getScore());
        return convertView;
    }
}

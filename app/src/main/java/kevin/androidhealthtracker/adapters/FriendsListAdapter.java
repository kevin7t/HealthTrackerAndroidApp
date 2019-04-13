package kevin.androidhealthtracker.adapters;

import android.content.Context;
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

    private Context context;
    private List<User> userList;

    public FriendsListAdapter(@NonNull Context context, List<User> users) {
        super(context, R.layout.all_friends_listview_item , users);
        this.userList = users;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.all_friends_listview_item, null, true);

        TextView userNameTextView = convertView.findViewById(R.id.userNameFriendTextView);
        TextView userScoreTextView = convertView.findViewById(R.id.userScoreTextView);

        try{
            userNameTextView.setText(userList.get(position).getUserName());
            userScoreTextView.setText(String.valueOf(userList.get(position).getScore()));
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return convertView;
    }
}

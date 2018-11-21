package kevin.androidhealthtracker.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kevin.healthtracker.datamodels.Friend;

import java.util.List;

import kevin.androidhealthtracker.R;


public class FriendsListAdapter extends ArrayAdapter<Friend> {

    private Activity context;
    private List<Friend> friendList;

    public FriendsListAdapter(@NonNull Activity context, List<Friend> friends) {
        super(context, R.layout.status_listview_item, friends);
        this.friendList = friends;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        convertView = layoutInflater.inflate(R.layout.status_listview_item, null, true);

        ImageView profilePicture = convertView.findViewById(R.id.userProfilePicture);
        TextView userNameTextView = convertView.findViewById(R.id.userNameTextView);
        TextView statusTextView = convertView.findViewById(R.id.statusContentTextView);

        return convertView;
    }
}

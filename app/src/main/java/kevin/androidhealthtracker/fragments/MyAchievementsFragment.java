package kevin.androidhealthtracker.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kevin.healthtracker.datamodels.User;

import java.util.ArrayList;
import java.util.List;

import kevin.androidhealthtracker.MainActivity;
import kevin.androidhealthtracker.R;
import kevin.androidhealthtracker.WebClient;

import static android.content.Context.MODE_PRIVATE;


public class MyAchievementsFragment extends Fragment {
    public static SharedPreferences prefs;
    protected WebClient client;
    protected ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myachievements, container, false);
        client = MainActivity.client;
        prefs = getActivity().getSharedPreferences("SharedPreferences", MODE_PRIVATE);


        listView = view.findViewById(R.id.fragment_achievements_list);
        User user = client.getUser(prefs.getInt("userId", 0));

        List<String> achievements = new ArrayList<>();
        int score = user.getScore();

        //TODO Create list of achievements and then divide them to the user score


        return view;
    }

}

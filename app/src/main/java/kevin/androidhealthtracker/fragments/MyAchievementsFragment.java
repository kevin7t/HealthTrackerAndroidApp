package kevin.androidhealthtracker.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kevin.healthtracker.datamodels.User;

import org.springframework.web.client.ResourceAccessException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import kevin.androidhealthtracker.MainActivity;
import kevin.androidhealthtracker.R;
import kevin.androidhealthtracker.WebClient;

import static android.content.Context.MODE_PRIVATE;


public class MyAchievementsFragment extends Fragment {
    public static SharedPreferences prefs;
    protected WebClient client;
    protected ListView achievementsListView;
    protected User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myachievements, container, false);
        client = MainActivity.client;
        prefs = getActivity().getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        ExecutorService executor = Executors.newWorkStealingPool();
        Callable<User> task = () -> {
            User user = null;
            try {
                user = client.getUser(prefs.getInt("userId", 0));
            } catch (ResourceAccessException e) {
                e.printStackTrace();
            }
            return user;
        };
        Future<User> userFuture = executor.submit(task);
        try {
            user = userFuture.get(4, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        achievementsListView = view.findViewById(R.id.fragment_achievements_list);

        List<String> achievements = new ArrayList<>();
        try {
            achievements = createListOfAchievements(user.getScore());
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        Collections.reverse(achievements);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.list_item, achievements);
        achievementsListView.setAdapter(adapter);
        return view;
    }

    public List<String> createListOfAchievements(int score) throws IOException {
        HashMap<Integer, String> achievementsMap = new HashMap<>();
        List<String> result = new ArrayList<>();


        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getActivity().getApplicationContext().getAssets().open("achievements.txt")));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] values = line.split(",");
            Integer key = Integer.valueOf(values[0]);
            achievementsMap.put(key, values[1]);
        }

        for (Map.Entry<Integer, String> entry : achievementsMap.entrySet()) {
            if (entry.getKey() <= score) {
                result.add("Points: " + entry.getKey() + " - " + entry.getValue());
            }
        }
        return result;
    }


}

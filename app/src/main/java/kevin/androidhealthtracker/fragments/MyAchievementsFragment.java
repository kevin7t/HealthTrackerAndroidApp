package kevin.androidhealthtracker.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kevin.healthtracker.datamodels.User;

import org.springframework.web.client.RestClientException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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
    protected ListView listView;
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
            }
            catch (Exception e) {
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

        listView = view.findViewById(R.id.fragment_achievements_list);

        List<String> achievements = new ArrayList<>();
        int score = user.getScore();
        try {
            achievements = createListOfAchievements(score);
        } catch (IOException e) {
            e.printStackTrace();
        }
        achievements.forEach(System.out::println);
        //TODO Create list of achievements and then divide them to the user score


        return view;
    }

    public List<String> createListOfAchievements(int score) throws IOException {
        List<String> achievements = new ArrayList<>();
        List<String> result = new ArrayList<>();
        int fileCounter = 0;

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getActivity().getApplicationContext().getAssets().open("achievements.txt")));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            achievements.add(line);
        }

        while (score != 0 && fileCounter < result.size()) {
            achievements.add(result.get(fileCounter));
            score -= 100;
            fileCounter++;
        }

        return achievements;
    }

    public class GetUserTask extends AsyncTask<Void, Void, Boolean> {
        WebClient client = MainActivity.client;

        GetUserTask() {
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                user = client.getUser(prefs.getInt("userId", 0));
            } catch (RestClientException e) {
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
            }
        }
    }


}

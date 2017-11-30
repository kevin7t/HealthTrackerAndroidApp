package kevin.androidhealthtracker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

    private TextView mTextMessage;
    private FragmentOne fragmentOne;
    private FragmentTwo fragmentTwo;
    FragmentTransaction transaction = getFragmentManager().beginTransaction();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    transaction = getFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                    transaction.replace(R.id.fragment_container, fragmentOne);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    transaction = getFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                    transaction.replace(R.id.fragment_container, fragmentTwo);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentOne = new FragmentOne();
        fragmentTwo = new FragmentTwo();
    }

}

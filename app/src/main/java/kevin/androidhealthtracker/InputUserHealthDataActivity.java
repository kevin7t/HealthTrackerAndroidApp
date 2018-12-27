package kevin.androidhealthtracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.springframework.web.client.RestClientException;

import kevin.androidhealthtracker.datamodels.Gender;
import kevin.androidhealthtracker.datamodels.UserCalorieProfile;

public class InputUserHealthDataActivity extends AppCompatActivity {
    private SharedPreferences.Editor editor;
    private String USER_SETUP_STATUS = "user_setup_status";
    private Switch genderSwitchItem;
    private EditText ageEditText, weightEditText, heightEditText;
    private String gender = Gender.Male;
    private int age;
    private String weight,height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_user_health_data);
        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("Input Health Data");
        setSupportActionBar(toolbar);
        editor = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).edit();
        genderSwitchItem = findViewById(R.id.genderSwitchItem);
        ageEditText = findViewById(R.id.AgeEditText);
        weightEditText = findViewById(R.id.WeightEditText);
        heightEditText = findViewById(R.id.HeightEditText);
        genderSwitchItem.setOnCheckedChangeListener(onCheckedChangeListener);


    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if(isChecked){
                gender = Gender.Female;
            }else{
                gender = Gender.Male;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_post) {
            try {
                setupCalorieGoals(gender,
                        Integer.valueOf(ageEditText.getText().toString()),
                        Float.valueOf(weightEditText.getText().toString()),
                        Float.valueOf(heightEditText.getText().toString()));
                Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            } catch (RestClientException e) {
                e.printStackTrace();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private UserCalorieProfile setupCalorieGoals(String gender, int age, Float weight, Float height) {
        UserCalorieProfile profile = new UserCalorieProfile();
        profile.setGender(gender);
        profile.setAge(age);
        profile.setWeight(weight);
        profile.setHeight(height);
        editor.putString("gender", gender);
        editor.putInt("age", age);
        editor.putFloat("weight", weight);
        editor.putFloat("height", height);
        editor.putInt("lowcalories", profile.getLowCalories());
        editor.putInt("mediumcalories", profile.getMediumCalories());
        editor.putInt("highcalories", profile.getHighCalories());
        editor.putBoolean(USER_SETUP_STATUS, true);
        editor.apply();
        return profile;
    }
}

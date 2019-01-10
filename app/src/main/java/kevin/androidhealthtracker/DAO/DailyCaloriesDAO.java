package kevin.androidhealthtracker.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import kevin.androidhealthtracker.datamodels.DailyCalories;

@Dao
public interface DailyCaloriesDAO {

    @Query("SELECT * FROM dailycalories_table")
    List<DailyCalories> getAll();

    //TODO Must use type converter to convert date to a string and back
    @Query("SELECT * FROM dailycalories_table WHERE date = :date")
    DailyCalories getByDate(String date);

    @Insert
    void insert(DailyCalories dailyCalories);

    @Update
    void update(DailyCalories dailyCalories);
}

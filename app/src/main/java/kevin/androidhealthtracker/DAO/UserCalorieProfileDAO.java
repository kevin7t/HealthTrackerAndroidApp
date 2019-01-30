package kevin.androidhealthtracker.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import kevin.androidhealthtracker.datamodels.UserCalorieProfile;
import kevin.androidhealthtracker.datamodels.Weight;

@Dao
public interface UserCalorieProfileDAO {

    @Query("SELECT * FROM usercalorieprofile_table")
    List<UserCalorieProfile> getAll();

    @Query("SELECT * FROM usercalorieprofile_table ORDER BY date DESC LIMIT 1")
    UserCalorieProfile getLatest();

    @Insert
    void insert(UserCalorieProfile profile);

    @Update
    void update(UserCalorieProfile profile);
}

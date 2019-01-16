package kevin.androidhealthtracker.datamodels;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@Entity(tableName = "dailycalories_table")
public class DailyCalories {

    public DailyCalories(String date, int goalCalories){
        this.date = date;
        this.goalCalories = goalCalories;
        consumedCalories = 0;
        burntCalories = 0;
    }

    @PrimaryKey
    @ColumnInfo(name = "date")
    @NonNull
    private String date;

    @ColumnInfo(name = "goalcalories")
    private int goalCalories;

    @ColumnInfo(name = "consumedcalories")
    private int consumedCalories;

    @ColumnInfo(name = "burntcalories")
    private int burntCalories;

}

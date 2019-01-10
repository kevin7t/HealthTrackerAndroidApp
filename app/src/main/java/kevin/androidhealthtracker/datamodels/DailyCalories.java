package kevin.androidhealthtracker.datamodels;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(tableName = "dailycalories_table")
public class DailyCalories {
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

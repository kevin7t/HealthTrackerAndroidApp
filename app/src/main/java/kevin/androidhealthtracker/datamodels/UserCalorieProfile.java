package kevin.androidhealthtracker.datamodels;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(tableName = "usercalorieprofile_table")
public class UserCalorieProfile {
    @PrimaryKey
    @ColumnInfo(name = "date")
    @NonNull
    private String date;

    @ColumnInfo(name = "gender")
    private String gender;

    @ColumnInfo(name = "weight")
    private Float weight;

    @ColumnInfo(name = "height")
    private Float height;

    @ColumnInfo(name = "age")
    private int age;

    private Integer bmr;

    private Integer calculateBmr(){
        Double calculation = (10 * weight) + (6.25*height) - (5*age) + 5;
        bmr = calculation.intValue();
        return bmr;
    }

    public Integer getLowCalories(){
        //Sedentary or light activity
        Double calculation = calculateBmr()*1.53;
        return calculation.intValue();
    }

    public Integer getMediumCalories(){
        //Active or moderately active
        Double calculation = calculateBmr()*1.76;
        return calculation.intValue();
    }

    public Integer getHighCalories(){
        //Highly active
        Double calculation = calculateBmr()*2.25;
        return calculation.intValue();
    }
}




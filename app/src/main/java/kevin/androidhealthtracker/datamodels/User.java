package kevin.androidhealthtracker.datamodels;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private Gender gender;
    private Double weight;
    private Double height;
    private int age;
    private Integer bmr;
    private Integer maintenence;

    private Integer getBmr(){
        Double calculation = (10 * weight) + (6.25*height) - (5*age) + 5;
        bmr = calculation.intValue();
        return bmr;
    }

    public Integer getLowCalories(){
        //Sedentary or light activity
        Double calculation = getBmr()*1.53;
        maintenence = calculation.intValue();
        return maintenence;
    }

    public Integer getMediumCalories(){
        //Active or moderately active
        Double calculation = getBmr()*1.76;
        maintenence = calculation.intValue();
        return maintenence;
    }

    public Integer getHighCalories(){
        //Highly active
        Double calculation = getBmr()*2.25;
        maintenence = calculation.intValue();
        return maintenence;
    }
}




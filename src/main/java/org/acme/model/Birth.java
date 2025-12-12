package org.acme.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// JSONを扱うためにJava Beanの形式にする
public class Birth {
    // 💡 フィールドはpublicにしておくと、Quarkusが自動でJSONにシリアライズできます

    @NotBlank(message = "year is required")
    public String year;

    @NotBlank(message = "month is required")
    public String month;

    @NotBlank(message = "day is required")
    public String day;

    @Pattern(regexp = "male|female", message = "gender must be 'male' or 'female'")
    public String gender;
    

    // デフォルトコンストラクタは必須ではありませんが、良い慣習です
    public Birth() {}
    
    public Birth(String year,String month,String day,String gender) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.gender = gender;
    }
}
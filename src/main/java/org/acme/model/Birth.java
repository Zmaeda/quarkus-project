package org.acme.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// JSONを扱うためにJava Beanの形式にする
public class Birth {
    // 💡 フィールドはpublicにしておくと、Quarkusが自動でJSONにシリアライズできます

    @NotBlank(message = "year is required")
    private String year;

    @NotBlank(message = "month is required")
    private String month;

    @NotBlank(message = "day is required")
    private String day;

    @NotBlank(message = "gender is required")
    @Pattern(regexp = "male|female", message = "gender must be 'male' or 'female'")
    private String gender;
    

    // デフォルトコンストラクタは必須ではありませんが、良い慣習です
    public Birth() {}
    
    public Birth(String year,String month,String day,String gender) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.gender = gender;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    
}
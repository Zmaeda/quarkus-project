package org.acme.model;

// JSONを扱うためにJava Beanの形式にする
public class Birth {
    // 💡 フィールドはpublicにしておくと、Quarkusが自動でJSONにシリアライズできます

    public String year;
    public String month;
    public String day;
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
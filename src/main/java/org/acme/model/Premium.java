package org.acme.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity // JPAエンティティであることを示す
@Table(name = "premiums") // マッピングするテーブル名を指定
public class Premium extends PanacheEntityBase { // Panacheのベースクラスを継承

    @Id // 主キー
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQLのSERIALに対応
    public Long id;

    @Column(name = "age_group_min")
    public Integer ageGroupMin;

    // DBではCHAR(1)ですが、JavaではStringで扱い、'M'/'F'を格納します
    @Column(name = "gender") 
    public String gender; 

    @Column(name = "premium_amount")
    public Integer premiumAmount;

    // Panache EntityBaseを使用する場合、getter/setterやデフォルトコンストラクタは必須ではありません。
}
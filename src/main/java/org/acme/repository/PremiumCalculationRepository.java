package org.acme.repository;

import org.acme.model.Premium; // 💡 作成したPanache Entityをインポート

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PremiumCalculationRepository {

    private static final String GENDER_MALE = "male";
    private static final String GENDER_FEMALE = "female";
    private static final String DB_CODE_MALE = "M";
    private static final String DB_CODE_FEMALE = "F";

    /**
     * 年齢と性別に基づき、PostgreSQLの premiums テーブルから保険料を取得します。
     *
     * @param age 計算された年齢
     * @param gender リクエストされた性別 ("male" または "female")
     * @return 保険料額
     */
    public int getPremium(int age, String gender) {

        //  性別をDB格納値 ('M'/'F') に変換
        String dbGenderCode;
        if (GENDER_MALE.equalsIgnoreCase(gender)) {
            dbGenderCode = DB_CODE_MALE;
        } else if (GENDER_FEMALE.equalsIgnoreCase(gender)) {
            dbGenderCode = DB_CODE_FEMALE;
        } else {
            throw new IllegalArgumentException("InvalidGenderValue: The specified gender value '" + gender + "' is invalid. Accepted values are 'male' or 'female'.");
        }

        // DBからデータを検索 (Panache Query)
        // ユーザーの年齢以下の最大 age_group_min を持つレコードを探す
        // 例: 35歳の場合、age_group_min <= 35 かつ gender = 'M' のレコードを age_group_min の降順でソート (30, 20...)
        // 最初に見つかったレコード (age_group_min=30) を取得する
        Premium premiumEntity = Premium.find(
                "ageGroupMin <= ?1 AND gender = ?2 ORDER BY ageGroupMin DESC",
                age,
                dbGenderCode
        ).firstResult();

        // 4. 結果のチェック
        if (premiumEntity == null) {
            // DBにデータがない場合のフォールバック（通常は発生しない）
            throw new IllegalArgumentException("PremiumNotFound: No matching premium rate found for the given criteria (Age: " + age + ", Gender: " + gender + ").");
        }

        // 5. 保険料額を返却
        return premiumEntity.premiumAmount;
    }
}

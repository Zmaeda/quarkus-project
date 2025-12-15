package org.acme.repository;
import jakarta.enterprise.context.ApplicationScoped; // 💡 この行を追加

@ApplicationScoped
public class PremiumCalculationRepository{

private static final int[][] PREMIUM_TABLE = {
        // 年齢層:  男性(0) | 女性(1)
        /* 20代 */ { 8000,   6500 },
        /* 30代 */ { 12000,  10000 },
        /* 40代 */ { 18000,  15000 },
        /* 50代 */ { 25000,  21000 },
        /* 60代 */ { 35000,  30000 }
    };

    public int getPremium(int age, String gender) {
        
        // 1. 年齢層のインデックス計算 (Row Index)
        // 例: 35歳の場合 -> (35 / 10) - 2 = 3 - 2 = 1 (30代のインデックス)
        int ageIndex = (age / 10) - 2; 

        // 20歳未満、または69歳超は範囲外としてエラー処理
        if (ageIndex < 0 || ageIndex >= PREMIUM_TABLE.length) {
            throw new IllegalArgumentException("指定された年齢 " + age + " は保険料表の範囲外です (20-69歳)。");
        }

        // 2. 性別のインデックス計算 (Column Index)
        int genderIndex;
        if ("male".equalsIgnoreCase(gender)) {
            genderIndex = 0; // 男性は0列目
        } else if ("female".equalsIgnoreCase(gender)) {
            genderIndex = 1; // 女性は1列目
        } else {
            throw new IllegalArgumentException("無効な性別指定です: " + gender + " (許容値: male, female)");
        }

        // 3. 保険料の取得
        return PREMIUM_TABLE[ageIndex][genderIndex];
    }
}
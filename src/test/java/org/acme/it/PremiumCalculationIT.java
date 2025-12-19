package org.acme.it;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

@QuarkusTest // アプリ全体（DB・サーバー）を起動
public class PremiumCalculationIT {

    @Test
    @DisplayName("1. 結合テスト: 全レイヤーを通過し、DBから正しい保険料が返るか（正常系）")
    void testCalculate_FullFlow_Success() {
        // 1990/01/01生まれ（35歳想定）男性のリクエスト
        String json = "{\"year\":\"1990\", \"month\":\"01\", \"day\":\"01\", \"gender\":\"male\"}";

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/api/calculate")
                .then()
                .statusCode(200) // 全ての層を正常に通過
                .body("status", is(200))
                .body("estimate", is(12000)); // import.sqlの30代男性データ(12000円)と一致するか
    }

    @Test
    @DisplayName("2. 結合テスト: 不正な生年月日の場合、Validation層が400を返すか（異常系）")
    void testCalculate_FullFlow_InvalidDate() {
        // 存在しない2月30日のリクエスト
        String json = "{\"year\":\"1990\", \"month\":\"02\", \"day\":\"30\", \"gender\":\"male\"}";

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/api/calculate")
                .then()
                .statusCode(400) // CalcValidationで例外が発生しMapperで400変換される
                .body("errors", notNullValue());
    }

    @Test
    @DisplayName("3. IT分岐網羅: 20歳未満（19歳）で AgeOutOfRange エラーになるか")
    void testCalculate_AgeUnderRange() {
        // 今日が 2025/12/01 なら、2006/01/02 生まれは 19歳
        String json = "{\"year\":\"2006\", \"month\":\"01\", \"day\":\"02\", \"gender\":\"male\"}";

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/api/calculate")
                .then()
                .statusCode(400)
                .body("errors[0]", containsString("AgeOutOfRange")); // Service層の分岐を確認
    }

    @Test
    @DisplayName("4. IT分岐網羅: 未来の日付で InvalidDateInputException になるか")
    void testCalculate_FutureDate() {
        String json = "{\"year\":\"2099\", \"month\":\"12\", \"day\":\"31\", \"gender\":\"female\"}";

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/api/calculate")
                .then()
                .statusCode(400)
                .body("errors[0]", containsString("Birthday cannot be a future date")); // Validation層の分岐を確認
    }

    @Test
    @DisplayName("5. IT分岐網羅: 存在しない性別で 400 エラーになるか")
    void testCalculate_InvalidGender() {
        String json = "{\"year\":\"1990\", \"month\":\"01\", \"day\":\"01\", \"gender\":\"other\"}";

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/api/calculate")
                .then()
                .statusCode(400); // RepositoryまたはBean Validationの分岐を確認
    }
    @Test
    @DisplayName("6. 結合テスト: 生年月日が空欄の場合、Resource層が400を返すか（異常系）")
    void testCalculate_NullDate() {
        // 存在しない2月30日のリクエスト
        String json = "{\"year\":\"\", \"month\":\"02\", \"day\":\"30\", \"gender\":\"male\"}";

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/api/calculate")
                .then()
                .statusCode(400) // CalcValidationで例外が発生しMapperで400変換される
                .body("errors", notNullValue());
    }
}

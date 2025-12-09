package org.acme;

import java.time.LocalDate;
import java.time.Period; // 💡 JSONを受け取るために必要

import org.acme.model.Birth;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api")
public class GreetingResource {

    // (以前の GET /hello メソッドは残していても構いません)

    @POST // 💡 新しいデータを作成・送信するためのPOSTメソッド
    @Path("/calculate")
    // 💡 クライアントが JSON 形式のデータ (MediaType.APPLICATION_JSON) を送信することを指定
    @Consumes(MediaType.APPLICATION_JSON) 
    
    // 💡 応答形式も JSON (MediaType.APPLICATION_JSON) であることを指定
@Produces(MediaType.TEXT_PLAIN) // 💡 見積もり金額のみを数値(テキスト)で返す
  public int calculateEstimate(Birth request) {
       LocalDate birthday = LocalDate.of(
            Integer.parseInt(request.year),
            Integer.parseInt(request.month),
            Integer.parseInt(request.day)
        );
        int age = Period.between(birthday, LocalDate.now()).getYears();

        // 2. 保険料計算ロジック
        int basePrice =2000;
        int ageFactor = age * 100;

        // 💡 性別による計算の追加
        if ("male".equals(request.gender)) {
            basePrice += 500; // 男性は500円割増
        }
        
        int estimate = basePrice + ageFactor ;

        return estimate;
    }
}
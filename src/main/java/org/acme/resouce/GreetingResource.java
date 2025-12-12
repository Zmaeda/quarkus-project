package org.acme.resouce;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period; // 💡 JSONを受け取るために必要
import java.util.List;

import org.acme.model.Birth;
import org.acme.model.CalculateResponse;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
public class GreetingResource {

    @POST // 💡 新しいデータを作成・送信するためのPOSTメソッド
    @Path("/calculate")
    @Consumes(MediaType.APPLICATION_JSON)     // 💡 クライアントが JSON 形式のデータ (MediaType.APPLICATION_JSON) を送信することを指定
    @Produces(MediaType.APPLICATION_JSON)     // 💡 応答形式も JSON (MediaType.APPLICATION_JSON) であることを指定
  public Response calculateEstimate(@Valid Birth request) {
    try{
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

    return Response.ok(new CalculateResponse(estimate)).build();
            
      } catch (NumberFormatException | DateTimeException e) {
            
            // 💡 修正点: 開発者向けの詳細なエラーメッセージを構築 💡
            String errorType = e.getClass().getSimpleName(); // 例: "DateTimeException"
            String detailMessage;
            
            if (e instanceof NumberFormatException) {
                // NumberFormatException の場合、元の入力文字列を特定できればさらに良い
                detailMessage = errorType + "Invalid date value or format." + e.getMessage();
            
            } else {
                // DateTimeException の場合、Javaが返した詳細なメッセージを使用
                detailMessage =  errorType + "Invalid number format for date components." + e.getMessage();
            }
            
            // 発生したエラー詳細をリストとしてラップ
            // List.of(T...) は Java 9以降の機能です。
            List<String> errorDetails = List.of(detailMessage); 
            
            // 異常応答: HTTP 400 Bad Request と JSON を返す
            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new CalculateResponse(errorDetails)) 
                .build();
    }
    }
}
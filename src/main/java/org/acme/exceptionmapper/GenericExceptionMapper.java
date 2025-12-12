package org.acme.exceptionmapper;
// GenericExceptionMapper.java (新規作成)
import java.util.List; // 💡 JSON構文エラーの基底クラス

import org.acme.model.CalculateResponse; // JSONパース/マッピングエラー

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * すべての未処理の例外を捕捉し、カスタムのJSON応答を返す汎用マッパー。
 * JSONパースエラーを特に 400 Bad Request として処理します。
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public Response toResponse(Exception exception) {
        
        // ログ出力（開発者向けの詳細なスタックトレースをコンソールに出力）
        System.err.println("--- 🚨 汎用エラー捕捉 🚨 ---");
        exception.printStackTrace();
        
        // --- 1. JSONパースエラーの特定 (400 Bad Request) ---
        if (isJsonParseException(exception)) {
            String detail = "JSON構文またはデシリアライズエラー: リクエストのJSON形式が不正です。";
            
            // 開発者向けの詳細情報として、エラーの種類をerrorsリストに格納
            List<String> errorDetails = List.of(
                detail,
                "詳細: " + exception.getCause().getMessage() // 例外の根本原因メッセージを取得
            );

            CalculateResponse errorResponse = new CalculateResponse(errorDetails);

            return Response
                .status(Response.Status.BAD_REQUEST) // 400
                .type(MediaType.APPLICATION_JSON)
                .entity(errorResponse)
                .build();
        }

        // --- 2. その他のエラー (500 Internal Server Error) ---
        
        // 開発者向けの詳細をerrorsリストに格納
        List<String> errorDetails = List.of(
            "予期せぬサーバー内部エラーが発生しました。",
            "詳細: " + exception.getClass().getName()
        );
        
        // 500用のコンストラクタがDTOにない場合、errorsリストを渡すコンストラクタを使用
        // DTOの status フィールドを 500 に設定するために、500用のコンストラクタが必要です。
        // ここでは、DTOに 500 を設定するコンストラクタを追加することを前提とします。
        CalculateResponse errorResponse = new CalculateResponse(500, errorDetails); // 500用のコンストラクタが必要

        return Response
            .status(Response.Status.INTERNAL_SERVER_ERROR) // 500
            .type(MediaType.APPLICATION_JSON)
            .entity(errorResponse)
            .build();
    }
    
    // Jacksonのエラーを安全に特定するためのヘルパーメソッド
    // private boolean isJsonParseException(Exception e) {
    //     // Jacksonのエラーは通常、WebApplicationExceptionなどにラップされている
    //     Throwable rootCause = e;
    //     while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
    //         if (rootCause instanceof JsonParseException || rootCause instanceof JsonMappingException) {
    //             return true;
    //         }
    //         rootCause = rootCause.getCause();
    //     }
    //     return false;
    // }

    // 現在のrootCause自体が、json Exceptionの可能性を考慮する
private boolean isJsonParseException(Exception e) {
    Throwable current = e;
    
    // ループ条件: currentがnullになるまで、原因をたどり続ける
    while (current != null) {
        // 現在のThrowableインスタンスがJsonParseExceptionまたはJsonMappingExceptionであるかをチェック
        if (current instanceof com.fasterxml.jackson.core.JsonParseException || 
            current instanceof com.fasterxml.jackson.databind.JsonMappingException) {
            return true;
        }
        
        // 次の根本原因を取得。getCause()がnullを返したらループ終了
        current = current.getCause();
        
        // 無限ループを防ぐため、currentがnullにならず、かつ自分自身に戻ってしまった場合もループを終了
        if (current == e) break; 
    }
    return false;
}
}

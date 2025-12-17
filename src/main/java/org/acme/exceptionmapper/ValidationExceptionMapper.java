package org.acme.exceptionmapper;

import java.util.List;
import java.util.stream.Collectors;

import org.acme.model.CalculateResponse;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        
        // 1. エラーの詳細情報 (ConstraintViolation) を抽出し、リストに格納
        List<String> violationDetails = exception.getConstraintViolations().stream()
            .map(v -> {
                // パスからフィールド名とエラーメッセージを抽出
                String fieldPath = v.getPropertyPath().toString();
                String fieldName = fieldPath.substring(fieldPath.lastIndexOf('.') + 1);
                
                // 💡 フィールド名とエラーメッセージのみを結合し、構造化された詳細情報とする
                return fieldName + ": " + v.getMessage();
            })
            .collect(Collectors.toList());

        // 2. カスタム応答DTOを作成し、エラー詳細リストを渡す
        // (このコンストラクタは status=400 を設定します)
        CalculateResponse errorResponse = new CalculateResponse(violationDetails);

        // 3. HTTP 400 Bad Request を返却
        return Response
            .status(Response.Status.BAD_REQUEST)
            .type(MediaType.APPLICATION_JSON)
            .entity(errorResponse)
            .build();
    }
}
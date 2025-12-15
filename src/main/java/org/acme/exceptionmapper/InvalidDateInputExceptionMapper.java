package org.acme.exceptionmapper;

import java.util.List;

import org.acme.exception.InvalidDateInputException;
import org.acme.model.CalculateResponse;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidDateInputExceptionMapper implements ExceptionMapper<InvalidDateInputException> {

    @Override
    public Response toResponse(InvalidDateInputException exception) {
        
        // 開発者向けの詳細情報として、エラーの種類とメッセージをerrorsリストに格納
       List<String> errorDetails = List.of(
            exception.getMessage() 
        );

        // CalculateResponse(List<String> errors) は status=400 を設定
        CalculateResponse errorResponse = new CalculateResponse(errorDetails);

        return Response
            .status(Response.Status.BAD_REQUEST) // 400
            .type(MediaType.APPLICATION_JSON)
            .entity(errorResponse)
            .build();
    }
}
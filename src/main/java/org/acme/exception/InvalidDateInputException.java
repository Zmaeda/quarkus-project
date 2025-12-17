package org.acme.exception;

/**
 * 日付のパース（NumberFormatException）または妥当性チェック（DateTimeException, IllegalArgumentException）
 * に失敗した場合にスローされる例外。
 */
public class InvalidDateInputException extends RuntimeException {
    
    private final String errorType;

    public InvalidDateInputException(String message, String errorType, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }
    
    public String getErrorType() {
        return errorType;
    }
}
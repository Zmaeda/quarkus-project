package org.acme.validation;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;

import org.acme.exception.InvalidDateInputException;
import org.acme.model.Birth; // 💡 新しい例外をimport

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped // 💡 CDI Beanとして注入可能にする
public class CalcValidation {

    /**
     * BirthリクエストからLocalDateをパースし、有効性をチェックする。 無効な場合は InvalidDateInputException
     * をスローする。
     * @param request Birth DTO
     * @return 計算された年齢
     */
    public int validateAndCalculateAge(Birth request) {
        try {
            LocalDate birthday = LocalDate.of(
                    Integer.parseInt(request.getYear()),
                    Integer.parseInt(request.getMonth()),
                    Integer.parseInt(request.getDay())
            );

            // 将来の日付チェック
            if (birthday.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Birthday cannot be a future date.");
            }

            // 年齢を計算
            return Period.between(birthday, LocalDate.now()).getYears();

        } catch (NumberFormatException e) {
            String message = "Invalid number format for date component. Detail: " + e.getMessage();
            throw new InvalidDateInputException(message, e.getClass().getSimpleName(), e);

        } catch (DateTimeException e) {
            String message = "Invalid date value or format. Input: " + request.getYear() + "-" + request.getMonth() + "-" + request.getDay() + ". Detail: " + e.getMessage();
            throw new InvalidDateInputException(message, e.getClass().getSimpleName(), e);

        } catch (IllegalArgumentException e) {
            // 未来の日付チェックでスローされた例外を捕捉
            String message = e.getMessage();
            throw new InvalidDateInputException(message, e.getClass().getSimpleName(), e);
        }
    }
}

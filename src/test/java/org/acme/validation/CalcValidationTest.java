package org.acme.validation;

import org.acme.exception.InvalidDateInputException;
import org.acme.model.Birth;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class CalcValidationTest {

    @Inject
    CalcValidation calcValidation;


      @Test
    @DisplayName("1. 正常系: 20歳以上70歳未満かつデータで、年齢を返すこと")
    void testCalcValidation_Success() {
        Birth birth = new Birth("2000", "01", "01", "male");

        assertEquals(25,calcValidation.validateAndCalculateAge(birth) );
    }

    @Test
    @DisplayName("NumberFormatExceptionの分岐: 数値以外の入力")
    void testNumberFormatException() {
        Birth birth = new Birth("YYYY", "01", "01", "male");
        InvalidDateInputException ex = assertThrows(InvalidDateInputException.class, () -> 
            calcValidation.validateAndCalculateAge(birth)
        );
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("DateTimeExceptionの分岐: 不正な日付(2月30日)")
    void testDateTimeException() {
        Birth birth = new Birth("1990", "02", "30", "male");
        InvalidDateInputException ex = assertThrows(InvalidDateInputException.class, () -> 
            calcValidation.validateAndCalculateAge(birth)
        );
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("IllegalArgumentExceptionの分岐: 未来の日付")
    void testFutureDate() {
        Birth birth = new Birth("2099", "12", "31", "male");
        InvalidDateInputException ex = assertThrows(InvalidDateInputException.class, () -> 
            calcValidation.validateAndCalculateAge(birth)
        );
        assertNotNull(ex.getMessage());
    }
}
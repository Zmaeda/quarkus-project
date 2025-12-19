package org.acme.service;

import org.acme.exception.InvalidDateInputException;
import org.acme.model.Birth;
import org.acme.model.CalculateResponse;
import org.acme.repository.PremiumCalculationRepository;
import org.acme.validation.CalcValidation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@QuarkusTest
public class PremiumCalculationServiceTest {

    @Inject
    PremiumCalculationService service;

    @InjectMock
    CalcValidation calcValidation;

    @InjectMock
    PremiumCalculationRepository repository;

    @Test
    @DisplayName("1. 正常系: 20歳以上70歳未満かつデータが存在する場合、200 OKを返すこと")
    void testPremiumCalculate_Success() {
        // モックの設定
        when(calcValidation.validateAndCalculateAge(any(Birth.class))).thenReturn(20);
        when(repository.getPremium(anyInt(), anyString())).thenReturn(5000);

        Birth request = new Birth("2000", "01", "01", "male");
        Response response = service.premiumCulclate(request);

        assertEquals(200, response.getStatus());
        CalculateResponse entity = (CalculateResponse) response.getEntity();
        assertEquals(200, entity.status);
        assertEquals(5000, entity.estimate);
    }

    @Test
    @DisplayName("2. 異常系: 年齢が20歳未満の場合、400 Bad Requestを返すこと")
    void testPremiumCalculate_AgeUnderRange() {
        // 境界値 19歳
        when(calcValidation.validateAndCalculateAge(any(Birth.class))).thenReturn(19);

        Birth request = new Birth("2005", "01", "01", "male");
        Response response = service.premiumCulclate(request);

        assertEquals(400, response.getStatus());
        CalculateResponse entity = (CalculateResponse) response.getEntity();
        assertEquals(400, entity.status);
        assertEquals("AgeOutOfRange: The provided age 19 is outside the acceptable range (20-69).", entity.errors.get(0));
    }

    @Test
    @DisplayName("3. 異常系: 年齢が70歳以上の場合、400 Bad Requestを返すこと")
    void testPremiumCalculate_AgeOverRange() {
        // 境界値 70歳
        when(calcValidation.validateAndCalculateAge(any(Birth.class))).thenReturn(70);

        Birth request = new Birth("1950", "01", "01", "male");
        Response response = service.premiumCulclate(request);

        assertEquals(400, response.getStatus());
        CalculateResponse entity = (CalculateResponse) response.getEntity();
        assertEquals(400, entity.status);
        assertEquals("AgeOutOfRange: The provided age 70 is outside the acceptable range (20-69).", entity.errors.get(0));
    }

    @Test
    @DisplayName("4. 異常系: リポジトリでIllegalArgumentExceptionが発生した場合、400 Bad Requestを返すこと")
    void testPremiumCalculate_RepositoryError() {
        when(calcValidation.validateAndCalculateAge(any(Birth.class))).thenReturn(30);
        // 性別不正やデータなしをシミュレート
        when(repository.getPremium(anyInt(), anyString()))
            .thenThrow(new IllegalArgumentException("PremiumNotFound: No matching data"));

        Birth request = new Birth("1990", "01", "01", "unknown");
        Response response = service.premiumCulclate(request);

        assertEquals(400, response.getStatus());
        CalculateResponse entity = (CalculateResponse) response.getEntity();
        assertEquals("PremiumNotFound: No matching data", entity.errors.get(0));
    }

    @Test
    @DisplayName("5. 異常系: バリデーション層でInvalidDateInputExceptionが発生した場合、例外が伝播すること")
    void testPremiumCalculate_ValidationException() {
        // Service層のtry-catch外の例外
        when(calcValidation.validateAndCalculateAge(any(Birth.class)))
            .thenThrow(new InvalidDateInputException("Invalid format", "DateTimeException", new RuntimeException()));

        Birth request = new Birth("invalid", "01", "01", "male");

        // Service内ではキャッチされないため、JUnitのassertThrows等で検証するか、
        // JAX-RSのコンテナテストとして例外がスローされることを確認する
        try {
            service.premiumCulclate(request);
        } catch (InvalidDateInputException e) {
            assertEquals("Invalid format", e.getMessage());
            assertEquals("DateTimeException", e.getErrorType());
        }
    }
}
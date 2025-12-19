package org.acme.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class PremiumCalculationRepositoryTest {

    @Inject
    PremiumCalculationRepository repository;

    @Test
    @DisplayName("性別変換の分岐: male -> M")
    void testGetPremiumMale() {
        int amount = repository.getPremium(25, "male");
        assertEquals(8000, amount);
    }

    @Test
    @DisplayName("性別変換の分岐: female -> F")
    void testGetPremiumFemale() {
        // import.sqlでは20代女性は7000
        int amount = repository.getPremium(25, "female");
        assertEquals(7000, amount);
    }

    @Test
    @DisplayName("DBから結果が返ってこない: IllegalArgumentException")
    void testInvalidMinAge() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> 
            repository.getPremium(19, "female")
        );
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("DBから結果が返ってこない: IllegalArgumentException")
    void testInvalidAge() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> 
            repository.getPremium(75, "female")
        );
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("無効な性別の分岐: IllegalArgumentException")
    void testInvalidGender() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> 
            repository.getPremium(25, "unknown")
        );
        assertNotNull(ex.getMessage());
    }
}
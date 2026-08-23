package com.homeservices.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordHasherTest {

    @Test
    void protegeYVerificaUnaPassword() {
        String encoded = PasswordHasher.encode("ClaveSegura123");

        assertNotEquals("ClaveSegura123", encoded);
        assertTrue(PasswordHasher.isEncoded(encoded));
        assertTrue(PasswordHasher.matches("ClaveSegura123", encoded));
        assertFalse(PasswordHasher.matches("incorrecta", encoded));
    }

    @Test
    void conservaCompatibilidadConPasswordsLegadas() {
        assertTrue(PasswordHasher.matches("123456", "123456"));
        assertFalse(PasswordHasher.matches("otra", "123456"));
    }
}

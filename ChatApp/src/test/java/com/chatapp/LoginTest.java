package com.chatapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    // Testing Data
    Login login = new Login("ky_l1", "Ch&<sec@ke99!", "Kyle", "Smith", "+27838968976");

    @Test
    public void testCheckUserName() {
        // Test: Username contains underscore and is <= 5 chars
        assertTrue(login.checkUserName());

        // Negative test based on kyle!!!!!!!
        Login failUser = new Login("kyle!!!!!!!", "password", "Kyle", "Smith", "08966553");
        assertFalse(failUser.checkUserName());
    }

    @Test
    public void testCheckPasswordComplexity() {
        // Test Password meets requirements
        assertTrue(login.checkPasswordComplexity());

        // Negative test: "password"
        Login failPass = new Login("kyl_1", "password", "Kyle", "Smith", "08966553");
        assertFalse(failPass.checkPasswordComplexity());
    }

    @Test
    public void testCheckCellPhoneNumber() {
        // Test: Correct format +27...
        assertTrue(login.checkCellPhoneNumber());

        // Negative test based on 08966553
        Login failPhone = new Login("kyle_1", "Ch&<sec@ke99!", "Kyle", "Smith", "08966553");
        assertFalse(failPhone.checkCellPhoneNumber());
    }
}
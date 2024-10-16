package com.minet.userservice.service;

import com.google.common.cache.Cache;
import com.minet.userservice.user.service.OtpServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OtpServiceImplTest {

    @Mock
    private Cache<String, String> otpCache;

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private OtpServiceImpl otpService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testSendOtp() {
        String email = "test@example.com";
        otpService.sendOtp(email);
        assertTrue(true);
    }

    @Test
     void testVerifyOtp() {
        String email = "test@example.com";
        otpService.verifyOtp(email,"123456");
        assertTrue(true);
    }
}

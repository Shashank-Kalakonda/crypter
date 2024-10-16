package com.minet.userservice.user.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class OtpServiceImpl implements OtpService{
    private static final long OTP_EXPIRY_DURATION = 2;
    private Cache<String, String> otpCache = CacheBuilder.newBuilder()
            .expireAfterWrite(OTP_EXPIRY_DURATION, TimeUnit.MINUTES)
            .build();

    @Autowired
    private JavaMailSender javaMailSender;


    @Override
    public void sendOtp(String email) {
        String otp= generateRandomOtp(6);
        otpCache.put(email,otp);
        sendOtpToEmail(email,otp);
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        String cachedOtp= otpCache.getIfPresent(email);
        return cachedOtp != null && cachedOtp.equals(otp);
    }

    public String generateRandomOtp(int length){
        SecureRandom random=new SecureRandom();

        StringBuilder otp= new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();

    }

    public void sendOtpToEmail(String email, String otp){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Otp Verification of Minet Application");
        message.setText("Your Otp is : "+ otp);
        javaMailSender.send(message);
    }
}

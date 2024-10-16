package com.minet.userservice.user.service;


public interface OtpService {
    public void sendOtp(String email);
    public boolean verifyOtp(String email,String otp);
}

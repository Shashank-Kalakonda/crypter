package com.minet.userservice.user.controller;

import com.minet.userservice.user.dto.*;
import com.minet.userservice.user.exception.NotFoundException;
import com.minet.userservice.user.service.JwtGenerator;
import com.minet.userservice.user.service.OtpService;
import com.minet.userservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private OtpService otpService;

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody LoginDto loginDto){
        try {
            UserDto userDto= userService.getUser(loginDto);
            Map<String,String> token= jwtGenerator.generateToken(userDto);
            UserResponseDto responseDto= new UserResponseDto();
            responseDto.setUser(userDto);
            responseDto.setToken(token);

            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }catch (NotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody SignupDto signupDto){
        try {
            UserDto createdUser= userService.createUser(signupDto);
            Map<String, String> token= jwtGenerator.generateToken(createdUser);

            UserResponseDto responseDto = new UserResponseDto();
            responseDto.setUser(createdUser);
            responseDto.setToken(token);
            return new ResponseEntity<>(responseDto,HttpStatus.CREATED);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        boolean isValid=userService.validateToken(token);
        if (isValid){
            return new ResponseEntity<>("Valid Request",HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Invalid Request",HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<UserDto> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            throw new NotFoundException("Failed to retrieve user details");
        }
    }

    @PostMapping("/auth0")
    public ResponseEntity<UserResponseDto> auth0Register(@RequestBody AuthDto authDto){


        UserDto user= userService.getUserDetailsFromToken(authDto);
        Map<String, String> token= jwtGenerator.generateToken(user);
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setUser(user);
        responseDto.setToken(token);
        return new  ResponseEntity<>(responseDto,HttpStatus.CREATED);
    }


    @GetMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam String email){
        boolean verifiedEmail= userService.verifyEmail(email);
        if(!verifiedEmail){
            return new ResponseEntity<>("user not found",HttpStatus.NOT_FOUND);
        }
        otpService.sendOtp(email);
        return new ResponseEntity<>("Otp Sent to mail Successfully",HttpStatus.OK);
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<Map<String,String>> verifyOtp(@RequestBody OtpDto otpDto){
        boolean isValidOtp= otpService.verifyOtp(otpDto.getEmail(),otpDto.getOtp());
        UserDto userDto=new UserDto();
        userDto.setEmail(otpDto.getEmail());
        if(isValidOtp){
            Map<String,String> token=jwtGenerator.generateToken(userDto);

            return new ResponseEntity<>(token,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody LoginDto loginDto){
        userService.resetPassword(loginDto);
        return new ResponseEntity<>("Password Reset Successfully",HttpStatus.OK);
    }
}

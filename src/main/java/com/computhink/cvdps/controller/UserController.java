package com.computhink.cvdps.controller;


import com.computhink.cvdps.model.Users.AuthRequest;
import com.computhink.cvdps.model.Users.UserInfo;
import com.computhink.cvdps.repository.CustomUserIdforRepository;
import com.computhink.cvdps.repository.UserInfoRepository;
import com.computhink.cvdps.service.UserService.JwtService;
import com.computhink.cvdps.service.UserService.UserInfoServiceImpl;
import com.computhink.cvdps.service.UserService.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserInfoServiceImpl service;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    CustomUserIdforRepository userIdforRepository;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/addNewUser")
    public ResponseEntity<String> addNewUser(@RequestBody UserInfo userInfo,
                                             HttpServletRequest httpServletRequest) {
        try {
            String token = jwtService.generateToken(userInfo.getEmail());
            userInfo.setClientIpAddress(httpServletRequest.getRemoteAddr());
            userInfo.setToken(token);
            if(userService.addUser(userInfo)){
                userIdforRepository.updateTokenGenerated(userInfo.getEmail(),token);
                return new ResponseEntity<>(token,HttpStatus.OK);
            }
            return new ResponseEntity<>("Unable to create User. Please contact System Admin.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/userProfile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String userProfile(Authentication authentication,
                              @RequestHeader ("authorization") String token) {
        Optional<UserInfo> userInfoOptional = userInfoRepository.findByEmail(authentication.getName());
        token = token.substring(7);
        if(userInfoOptional.isPresent()){
            if(token.equals(userInfoOptional.get().getToken())){
                return "Welcome to User Profile: " + authentication.getName();
            }
        }
        return "Authentication Failed ";
    }

    @GetMapping("/admin/adminProfile")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminProfile(Authentication authentication,
                               @RequestHeader ("authorization") String token) {
        Optional<UserInfo> userInfoOptional = userInfoRepository.findByEmail(authentication.getName());
        token = token.substring(7);
        if(userInfoOptional.isPresent()){
            if(token.equals(userInfoOptional.get().getToken())){
                return "Welcome to Admin Profile: " + authentication.getName();
            }
        }
        return "Authentication Failed ";
    }

    @PostMapping("/generateToken")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            String token=  jwtService.generateToken(authRequest.getUsername());
            userIdforRepository.updateTokenGenerated(authRequest.getUsername(),token);
            return token;
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    @PostMapping("/tokenExpire")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String expireToken(Authentication authentication ){
        try {
            userIdforRepository.updateTokenGenerated(authentication.getName(), "");
            return "Logout Successful!!";
        } catch (Exception ex){
            return "Exception Occurred while logging out.";
        }
    }

}


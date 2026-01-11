package org.demo.oems.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.payload.request.LoginRequest;
import org.demo.oems.payload.response.AuthResponse;
import org.demo.oems.service.AuthService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthRest {

    private final Logger logger = LogManager.getLogger(AuthRest.class);

    private final AuthService authService;

    public AuthRest(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticationUser(@RequestBody LoginRequest loginRequest){
        AuthResponse apiResponse = new AuthResponse();
        try{
            logger.info("Start authentication user controller with request :: {}", loginRequest);
            apiResponse = authService.loginAuthentication(loginRequest);

        }catch (Exception e){
            logger.error("Exception in authentication user controller :: {}", e.getMessage());
            apiResponse.setMessages(e.getMessage());
            apiResponse.setStatus("1");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
        return  new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> userLogoutRequest(){
        return null;
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshTokenRequest(){
        return null;
    }

    @PostMapping("/userProfiles")
    public ResponseEntity<?> getUserProfiles(){
        return null;
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(){
        return null;
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(){
        return null;
    }

}

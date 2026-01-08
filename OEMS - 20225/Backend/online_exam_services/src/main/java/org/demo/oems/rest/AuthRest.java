package org.demo.oems.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.payload.request.LoginRequest;
import org.demo.oems.payload.response.LoginResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthRest {

    private final Logger logger = LogManager.getLogger(AuthRest.class);

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticationUser(@RequestBody LoginRequest loginRequest){
        LoginResponse apiResponse = new LoginResponse();
        try{
            logger.info("Start authentication user controller with request :: " + loginRequest);


        }catch (Exception e){
            logger.error("Exception in authentication user controller :: {}", e.getMessage());
            apiResponse.setMessages(e.getMessage());
            apiResponse.setStatus("1");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
        return  new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(0));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> userLogoutRequest(){
        return null;
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshTokenRequest(){
        return null;
    }

}

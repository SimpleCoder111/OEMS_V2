package org.demo.oems.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.UserInfoDomain;
import org.demo.oems.payload.request.LoginRequest;
import org.demo.oems.payload.response.AuthResponse;
import org.demo.oems.utils.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtUtils jwtUtil;

    private final AuthenticationManager authenticationManager;

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    public AuthService(JwtUtils jwtUtil, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }


    public AuthResponse loginAuthentication(LoginRequest request) {
        AuthResponse authResponse = new AuthResponse();
        try {
            logger.debug("Start login authentication services :: {}", request);


            // Spring Security authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserId(),
                            request.getPassword()
                    )
            );

            // If successful, load UserDetails (your custom UserDetailsService will be used)
            UserInfoDomain userDetails = (UserInfoDomain) authentication.getPrincipal();

            // Generate JWT
            assert userDetails != null;
            String accessToken = jwtUtil.generateToken(userDetails);

            logger.debug("Successfully authenticate user");
            authResponse.setMessages("success");
            authResponse.setStatus("0");
            authResponse.setAccessToken(accessToken);
            return authResponse;
        } catch (BadCredentialsException e) {
           logger.error("Bad Credential Exception :: {}", e.getMessage());
           authResponse.setStatus("400");
           authResponse.setMessages("Either User or Password Not Correct!!!");
           return authResponse;
        }catch (Exception e){
            logger.error("Exception :: {}", e.getMessage());
            authResponse.setStatus("500");
            authResponse.setMessages(e.getMessage());
            return authResponse;
        }
    }
}

package org.demo.oems.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.ClassDomain;
import org.demo.oems.domain.UserInfoDomain;
import org.demo.oems.payload.request.CreateClassInfoRequest;
import org.demo.oems.payload.request.CreateUserRequest;
import org.demo.oems.payload.response.UserProfileResponse;
import org.demo.oems.repository.UserInfoRepo;
import org.demo.oems.service.UserService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class UserRest {

    private final Logger logger = LogManager.getLogger(UserRest.class);

    private final UserService userService;

    public UserRest(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request){
        try{
            logger.info("Start Create user Request :: {}", request);
            JSONObject finalResponse = userService.createUser(request);
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception Happen While Create Class Info {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    @GetMapping("/getUserListsByRoleId/{roleId}")
    public ResponseEntity<?> getUserListsByRoleId(@PathVariable int roleId){
        try{
            logger.info("Start Get User Lists By Role ID :: {}", roleId);
            List<UserInfoDomain> userInfoDomainList = userService.getUserInfoListsByRoleId(roleId);
            return new ResponseEntity<>(userInfoDomainList, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception Happen While Get User Lists By Role ID {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    @GetMapping("/getAllUserLists")
    public ResponseEntity<?> getAllUserLists() {
        try{
            logger.info("Start Get All User Info Lists Request");
            List<UserInfoDomain> userInfoDomainList = userService.getAllUserInfoLists();
            return new ResponseEntity<>(userInfoDomainList, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception Happen While Get All User Info Lists {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }



    @PostMapping(value = "/profile/image", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file, @RequestParam String userId) {
        try {
            UserProfileResponse userProfileResponse = userService.uploadProfileImage(file, userId);
            return ResponseEntity.ok(userProfileResponse);
        }catch (Exception e){
            logger.error("Exception Happen While Start Get User Profile Controller {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    @GetMapping("/getUserProfile")
    public ResponseEntity<?> getUserProfile(@RequestParam String userId) {
        try{
            logger.info("Start Get User Profile Controller :: {}", userId);
            UserProfileResponse userProfileResponse = userService.getUserProfile(userId);
            return new ResponseEntity<>(userProfileResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception Happen While Start Get User Profile Controller {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

}

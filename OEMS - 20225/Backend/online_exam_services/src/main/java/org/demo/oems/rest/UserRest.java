package org.demo.oems.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.UserInfoDomain;
import org.demo.oems.payload.request.*;
import org.demo.oems.payload.response.UserProfileResponse;
import org.demo.oems.service.UserService;
import org.demo.oems.utils.ResponseUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
public class UserRest {

    private final Logger logger = LogManager.getLogger(UserRest.class);

    private final UserService userService;

    public UserRest(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/admin/user/create")
    @Operation(summary = "Admin User Service - Create User", description = "Create User by Admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody CreateUserRequest request){
        Map<String, Object> apiResponse = new HashMap<>();
        try{
            logger.info("Start Create user Request :: {}", request);
            apiResponse = userService.createUser(request);
            logger.debug("API Response :: {}", apiResponse);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception Happen While Create Class Info {}", e.getMessage());
            apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @GetMapping("/admin/user/{roleId}")
    @Operation(summary = "Admin User Service - Get Users By Role ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> getUserListsByRoleId(@PathVariable Long roleId){
        try{
            logger.info("Start Get User Lists By Role ID :: {}", roleId);
            List<UserInfoDomain> userInfoDomainList = userService.getUserInfoListsByRoleId(roleId);
            return new ResponseEntity<>(userInfoDomainList, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception Happen While Get User Lists By Role ID {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin User Service - Get All Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/admin/users")
    public ResponseEntity<Map<String, Object>> getAllUserLists() {
        Map<String, Object> apiResponse = new HashMap<>();
        try{
            logger.info("Start Get All User Info Lists Request");
            apiResponse = userService.getAllUserInfoLists();
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception Happen While Get All User Info Lists {}", e.getMessage());
            apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin User Service - Update User Info by User ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping("/admin/user/{userId}/update")
    public ResponseEntity<Map<String, Object>> updateUserInfo(@PathVariable String userId, @RequestBody CreateUserRequest createUserRequest) {
        logger.info("Start - updateUserInfo API");
        Map<String, Object> apiResponse = new HashMap<>();
        try{
            apiResponse = userService.updateUserInfo(userId, createUserRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception Happen While Get All User Info Lists {}", e.getMessage());
            apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin User Service - Update User Status (Active/Inactive)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping("/admin/user/{userId}/toggle-status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(@PathVariable String userId, @RequestBody ToggleUserStatusRequest toggleUserStatusRequest){
        Map<String, Object> apiResponse = new HashMap<>();
        try{
            logger.info("Start - updateUserStatus API");
            apiResponse = userService.updateUserStatus(userId, toggleUserStatusRequest);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception Happen While Update User Status {}", e.getMessage());
            apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "General User Service - Upload User Profile Image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping(value = "/user/profile/image", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file, @RequestParam String userId) {
        try {
            UserProfileResponse userProfileResponse = userService.uploadProfileImage(file, userId);
            return ResponseEntity.ok(userProfileResponse);
        }catch (Exception e){
            logger.error("Exception Happen While Start Get User Profile Controller {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "General User Service - Get User Profile by User ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/user/{userId}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable String userId) {
        try{
            logger.info("Start Get User Profile Controller :: {}", userId);
            UserProfileResponse userProfileResponse = userService.getUserProfile(userId);
            return new ResponseEntity<>(userProfileResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception Happen While Start Get User Profile Controller {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    //======================================================================
    //Teacher User Service APIs Service
    //======================================================================
    @Operation(summary = "Teacher User Service - Get All Students Lists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/teacher/students")
    public ResponseEntity<Map<String, Object>> getStudentListsByTeacherId(@RequestParam String teacherId) {
        try{
            logger.info("Start - getStudentListsByTeacherId Controller :: {}", teacherId);
            Map<String, Object> apiResponse = userService.getStudentListsByTeacherId(teacherId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception - getStudentListsByTeacherId Controller :: {}", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher User Service - Get All Students Lists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/teacher/students/class/{classId}")
    public ResponseEntity<Map<String, Object>> getStudentListsByClassId(@PathVariable long classId) {
        try{
            logger.info("Start - getStudentListsByClassId API :: {}", classId);
            Map<String, Object> apiResponse = userService.getStudentListsByClassId(classId);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception - getStudentListsByClassId Controller :: {}", e.getMessage());
            Map<String, Object> apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(500));
        }
    }


}

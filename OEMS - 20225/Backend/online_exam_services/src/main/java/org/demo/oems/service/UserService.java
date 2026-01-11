package org.demo.oems.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.RoleDomain;
import org.demo.oems.domain.UserInfoDomain;
import org.demo.oems.payload.request.CreateUserRequest;
import org.demo.oems.payload.response.UserProfileResponse;
import org.demo.oems.repository.UserInfoRepo;
import org.demo.oems.utils.ResponseUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class UserService implements UserDetailsService {
    private final UserInfoRepo userInfoRepo;

    private final PasswordEncoder passwordEncoder;

    private final Logger logger = LogManager.getLogger(UserService.class);

    private static final String LOG_PREFIX_FINAL_SERVICES_RESPONSE = "Final Service Response :: {}";

    @Value("${app.upload.profile-dir}")
    private String profileUploadDir;

    public UserService(UserInfoRepo userInfoRepo, PasswordEncoder passwordEncoder) {
        this.userInfoRepo = userInfoRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Load the full UserInfoDomain entity (role is fetched EAGERLY, so it's already available)
        return userInfoRepo.findUserInfoDomainByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }


    public JSONObject createUser(CreateUserRequest request){
        JSONObject apiResponse = new JSONObject();

        try{
            logger.debug("Creat User Service :: {}", request);

            boolean isUserExist = checkIfUserIdExist(request.getUserId());

            if(isUserExist){
                logger.debug("User Already Exists :: {}", request.getUserId());
                apiResponse = ResponseUtils.responseFormatUtils("1", "User Already Exists");
                return apiResponse;
            }

            String hashedPassword = passwordEncoder.encode(request.getPassword());
            request.setPassword(hashedPassword);

            UserInfoDomain newUser = getUserInfoDomain(request);

            userInfoRepo.save(newUser);

            apiResponse = ResponseUtils.responseFormatUtils("0", "Successfully Create User");
            logger.debug(LOG_PREFIX_FINAL_SERVICES_RESPONSE, apiResponse);
            return apiResponse;
        }catch (Exception e){
            logger.error("Exception while trying to added new subject :: {}", e.getMessage());
            apiResponse = ResponseUtils.responseFormatUtils("1", e.getMessage());
            return apiResponse;
        }
    }

    private static UserInfoDomain getUserInfoDomain(CreateUserRequest request) {
        UserInfoDomain newUser = new UserInfoDomain();

        newUser.setUserId(request.getUserId());
        newUser.setName(request.getName());
        newUser.setPassword(request.getPassword());
        newUser.setDateOfBirth(request.getDateOfBirth());
        newUser.setGender(request.getGender());

        RoleDomain roleDomain = new RoleDomain();
        roleDomain.setId(request.getRoleId());

        newUser.setRole(roleDomain);
        newUser.setEmail(request.getEmail());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setAddress(request.getAddress());
        return newUser;
    }

    public List<UserInfoDomain> getUserInfoListsByRoleId(int roleId){
        List<UserInfoDomain> userInfoDomainList = new ArrayList<>();
        try{
            logger.debug("Get User Info Lists By Role ID Service :: {}", roleId);
            userInfoDomainList = userInfoRepo.findUserInfoDomainsByRoleId(roleId);

        }catch (Exception e){
            logger.error("Exception while trying to Get User Info Lists By Role ID :: {}", e.getMessage());
        }

        logger.debug(LOG_PREFIX_FINAL_SERVICES_RESPONSE, userInfoDomainList);
        return userInfoDomainList;
    }

    public List<UserInfoDomain> getAllUserInfoLists(){
        List<UserInfoDomain> userInfoDomainList = new ArrayList<>();
        try{
            logger.debug("Get All User Info Lists");
            userInfoDomainList = userInfoRepo.findAll();
        }catch (Exception e){
            logger.error("Exception while trying to Get All User Info Lists :: {}", e.getMessage());
        }
        logger.debug(LOG_PREFIX_FINAL_SERVICES_RESPONSE, userInfoDomainList);
        return userInfoDomainList;
    }

    public Boolean checkIfUserIdExist(String userId){
        try{
            logger.debug("Trying to check if user Exists");
            Optional<UserInfoDomain> userInfoDomainOptional = userInfoRepo.findUserInfoDomainByUserId(userId);
            return userInfoDomainOptional.isPresent();
        }catch (Exception e){
            logger.error("Exception while trying to Check if user Exists :: {}", e.getMessage());
            return true;
        }
    }

    @Transactional
    public UserProfileResponse uploadProfileImage(MultipartFile file, String userId) {

        UserProfileResponse userProfileResponse = new UserProfileResponse();

        try {
            Optional<UserInfoDomain> userInfoOptional = userInfoRepo.findUserInfoDomainByUserId(userId);

            if (userInfoOptional.isEmpty()) {
                return userProfileResponse;
            }

            UserInfoDomain user = userInfoOptional.get();

            String extension = getString(file);
            String filename = user.getUserId() + "_" + UUID.randomUUID() + extension;

            // Save file
            Path uploadPath = Paths.get(profileUploadDir);
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Delete old image if exists
            if (user.getProfileImageUrl() != null) {
                Path oldPath = uploadPath.resolve(Paths.get(user.getProfileImageUrl()).getFileName());
                Files.deleteIfExists(oldPath);
            }

            // Save URL (relative for frontend)
            user.setProfileImageUrl("/uploads/profile/" + filename);
            userInfoRepo.save(user);

            // Return updated profile
            return new UserProfileResponse(
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getAddress(),
                    user.getDateOfBirth(),
                    user.getGender(),
                    user.getRoleName(),
                    user.getProfileImageUrl()
            );
        }catch (Exception e){
            logger.error("Exception while upload user profile image :: " + userProfileResponse);
            return userProfileResponse;
        }
    }

    private static String getString(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }
        if (file.getSize() > 2 * 1024 * 1024) {  // 2MB limit
            throw new IllegalArgumentException("Image size exceeds 2MB");
        }
        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            throw new IllegalArgumentException("Only JPEG or PNG images allowed");
        }

        // Generate unique filename
        return contentType.equals("image/jpeg") ? ".jpg" : ".png";
    }

    public UserProfileResponse getUserProfile(String userId){
        UserProfileResponse userProfileResponse = new UserProfileResponse();
        try{
            logger.debug("Start Get User Profile Service :: {}", userId);

            Optional<UserInfoDomain> userInfoOptional = userInfoRepo.findUserInfoDomainByUserId(userId);

            if(userInfoOptional.isEmpty()){
                logger.debug("Cannot find user info for the user ID :: {}", userId);
                return userProfileResponse;
            }

            UserInfoDomain userInfo = userInfoOptional.get();

            // Return updated profile
            return new UserProfileResponse(
                    userInfo.getUserId(),
                    userInfo.getName(),
                    userInfo.getEmail(),
                    userInfo.getPhoneNumber(),
                    userInfo.getAddress(),
                    userInfo.getDateOfBirth(),
                    userInfo.getGender(),
                    userInfo.getRoleName(),
                    userInfo.getProfileImageUrl()
            );
        }catch (Exception e){
            logger.error("Exception happen :: {}", e.getMessage());
            return userProfileResponse;
        }
    }
}

package org.demo.oems.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.RoleDomain;
import org.demo.oems.domain.UserInfoDomain;
import org.demo.oems.payload.request.CreateUserRequest;
import org.demo.oems.payload.request.ToggleUserStatusRequest;
import org.demo.oems.payload.response.UserProfileResponse;
import org.demo.oems.repository.UserInfoRepo;
import org.demo.oems.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserInfoRepo userInfoRepo;

    private final PasswordEncoder passwordEncoder;

    private final UserIdGeneratorService userIdGeneratorService;

    private final Logger logger = LogManager.getLogger(UserService.class);

    private static final String LOG_PREFIX_FINAL_SERVICES_RESPONSE = "Final Service Response :: {}";

    @Value("${app.upload.profile-dir}")
    private String profileUploadDir;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Load the full UserInfoDomain entity (role is fetched EAGERLY, so it's already available)
        return userInfoRepo.findUserInfoDomainByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public Map<String, Object> createUser(CreateUserRequest request){
        Map<String, Object> apiResponse = new HashMap<>();

        try{
            logger.debug("Creat User Service :: {}", request);

            String randomUniqueUserId =  userIdGeneratorService.generateStudentId();
            logger.debug("Generate Random User ID :: {}", randomUniqueUserId);

            boolean isUserExist = checkIfUserIdExist(randomUniqueUserId);

            if(isUserExist){
                logger.debug("User Already Exists :: {}", randomUniqueUserId);
                apiResponse = ResponseUtils.formatAPIResponse("400", "User Already Exists", "");
                return apiResponse;
            }

            String hashedPassword = passwordEncoder.encode(request.getPassword());
            request.setPassword(hashedPassword);
            request.setUserId(randomUniqueUserId);

            UserInfoDomain newUser = getUserInfoDomain(request);
            userInfoRepo.save(newUser);

            apiResponse = ResponseUtils.formatAPIResponse("200", "Successfully Create User", newUser);
            logger.debug(LOG_PREFIX_FINAL_SERVICES_RESPONSE, apiResponse);
            return apiResponse;
        }catch (Exception e){
            logger.error("Exception while trying to create new user :: {}", e.getMessage());
            apiResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return apiResponse;
        }
    }

    private static UserInfoDomain getUserInfoDomain(CreateUserRequest request) {
        UserInfoDomain newUser = new UserInfoDomain();

        createOrUpdateUserInfo(request, newUser);
        return newUser;
    }

    public List<UserInfoDomain> getUserInfoListsByRoleId(Long roleId){
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

    public Map<String, Object> getAllUserInfoLists(){
        Map<String, Object> finalServiceResponse = new HashMap<>();
        List<UserInfoDomain> userInfoDomainList = new ArrayList<>();
        try{
            logger.debug("Get All User Info Lists");
            userInfoDomainList = userInfoRepo.findAll();
            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Successfully Get All User Info Lists", userInfoDomainList);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception while trying to Get All User Info Lists :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }

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
            logger.error("Exception while upload user profile image :: {}", userProfileResponse);
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

    public Map<String, Object> updateUserInfo(String userId, CreateUserRequest request) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            logger.debug("Update User Service :: {}", request);

            Optional<UserInfoDomain> userInfoDomain = userInfoRepo.findUserInfoDomainByUserId(userId);
            if(userInfoDomain.isEmpty()){
                finalServiceResponse = ResponseUtils.formatAPIResponse("400", "User ID Not Found", "");
                return finalServiceResponse;
            }

            UserInfoDomain currentUser = userInfoDomain.get();

            createOrUpdateUserInfo(request, currentUser);

            userInfoRepo.save(currentUser);

            finalServiceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Create User", currentUser);
            logger.debug(LOG_PREFIX_FINAL_SERVICES_RESPONSE, finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception while trying to added new subject :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500",e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public static void createOrUpdateUserInfo(CreateUserRequest request, UserInfoDomain currentUser) {
        currentUser.setUserId(request.getUserId());

        currentUser.setName(request.getName());
        currentUser.setPassword(request.getPassword());
        currentUser.setDateOfBirth(request.getDob());
        currentUser.setGender(request.getGender());

        String status = request.getStatus() == Boolean.TRUE ? "ACTIVE" : "INACTIVE";

        currentUser.setStatus(status);

        RoleDomain roleDomain = new RoleDomain();
        roleDomain.setId(request.getRoleId());
        roleDomain.setRoleName(request.getRole());

        currentUser.setRole(roleDomain);
        currentUser.setEmail(request.getEmail());
        currentUser.setPhoneNumber(request.getPhoneNumber());
        currentUser.setAddress(request.getAddress());
    }

    public Map<String, Object> updateUserStatus(String userId, ToggleUserStatusRequest toggleUserStatusRequest){
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            logger.info("Start - updateUserStatus Service :: {}, {}", userId, toggleUserStatusRequest);

            Optional<UserInfoDomain> userInfoDomainOptional = userInfoRepo.findUserInfoDomainByUserId(userId);
            if(userInfoDomainOptional.isEmpty()){
                logger.error("User ID Not Found :: {}", userId);
                finalServiceResponse = ResponseUtils.formatAPIResponse("400", "User ID Not Found", "");
                return finalServiceResponse;
            }

            UserInfoDomain userInfoDomain = userInfoDomainOptional.get();

            String userStatus = toggleUserStatusRequest.getStatus() == Boolean.TRUE ? "ACTIVE" : "INACTIVE";

            userInfoDomain.setStatus(userStatus);

            userInfoRepo.save(userInfoDomain);
            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Successfully Update User Status", userInfoDomain);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception while trying to update user status :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500",e.getMessage(), "");
            return finalServiceResponse;
        }
    }


}

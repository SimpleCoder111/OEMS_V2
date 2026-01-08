package org.demo.oems.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.UserInfoDomain;
import org.demo.oems.payload.request.CreateUserRequest;
import org.demo.oems.repository.UserInfoRepo;
import org.demo.oems.utils.ResponseUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserInfoRepo userInfoRepo;

    private final Logger logger = LogManager.getLogger(UserService.class);


    public UserService(UserInfoRepo userInfoRepo) {
        this.userInfoRepo = userInfoRepo;
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

            UserInfoDomain newUser = new UserInfoDomain();

            newUser.setUserId(request.getUserId());
            newUser.setName(request.getName());
            newUser.setPassword(request.getPassword());
            newUser.setDateOfBirth(request.getDateOfBirth());
            newUser.setGender(request.getGender());
            newUser.setRoleId(request.getRoleId());
            newUser.setEmail(request.getEmail());
            newUser.setPhoneNumber(request.getPhoneNumber());
            newUser.setAddress(request.getAddress());

            userInfoRepo.save(newUser);

            apiResponse = ResponseUtils.responseFormatUtils("0", "Successfully Create User");
            logger.debug("final service response :: {}", apiResponse);
            return apiResponse;
        }catch (Exception e){
            logger.error("Exception while trying to added new subject :: {}", e.getMessage());
            apiResponse = ResponseUtils.responseFormatUtils("1", e.getMessage());
            return apiResponse;
        }
    }

    public List<UserInfoDomain> getUserInfoListsByRoleId(int roleId){
        List<UserInfoDomain> userInfoDomainList = new ArrayList<>();
        try{
            logger.debug("Get User Info Lists By Role ID Service :: {}", roleId);
            userInfoDomainList = userInfoRepo.findUserInfoDomainsByRoleId(roleId);

        }catch (Exception e){
            logger.error("Exception while trying to Get User Info Lists By Role ID :: {}", e.getMessage());
        }

        logger.debug("final service response :: {}", userInfoDomainList);
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
        logger.debug("final service response :: {}", userInfoDomainList);
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
}

package org.demo.oems.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.ClassDomain;
import org.demo.oems.domain.ClassGroupDomain;
import org.demo.oems.payload.request.CreateClassInfoRequest;
import org.demo.oems.payload.request.CreateNewClassRequest;
import org.demo.oems.repository.ClassGroupRepo;
import org.demo.oems.repository.ClassRepo;
import org.demo.oems.utils.CommonConstantUtils;
import org.demo.oems.utils.DateUtils;
import org.demo.oems.utils.ResponseUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassRepo classRepo;

    private final ClassGroupRepo classGroupRepo;

    private static final Logger logger = LogManager.getLogger(ClassroomService.class);
    

    public JSONObject createClassInfo(CreateClassInfoRequest request) {
        JSONObject finalResponse = new JSONObject();
        try{
            logger.debug("Start Create Class Info Service :: {}", request);
            ClassDomain newClassInfo = new ClassDomain();
            newClassInfo.setClassName(request.getClassName());
            newClassInfo.setTeacherId(request.getTeacherId());

            LocalDateTime classStart = DateUtils.formatDate(request.getClassStart());
            LocalDateTime classEnd = DateUtils.formatDate(request.getClassEnd());

            newClassInfo.setClassStart(classStart);
            newClassInfo.setClassEnd(classEnd);

            String classStatus = DateUtils.getClassStatus(classStart, classEnd);
            logger.debug("Class Status :: {}", classStatus);


            classRepo.save(newClassInfo);
            finalResponse = ResponseUtils.formatServiceResponse("0", "success");
            logger.debug("Successfully Insert Class Info to DB");
        }catch (Exception e){
            logger.error("Exception while add question banks :: {}" , e.getMessage() );
            finalResponse = ResponseUtils.formatServiceResponse("1", e.getMessage());
        }
        return  finalResponse;
    }

    public List<ClassDomain> getAllClassesInfo(){
        List<ClassDomain> classDomainList = new ArrayList<>();
        try{
            classDomainList = classRepo.findAll();
        }catch (Exception e){
            logger.error("Exception While Getting Classes Info Lists :: {}" , e.getMessage());
        }
        return classDomainList;
    }

    @Transactional
    public ClassDomain createNewClasses(CreateNewClassRequest createNewClassRequest) {
        ClassDomain newClassDomain = null;

        try{
            // 1. Validate request (e.g., classGroupId must exist)
            if (createNewClassRequest.getClassGroupId() == null) {
                throw new IllegalArgumentException("Class group ID is required");
            }

            // 2. Fetch the ClassGroupDomain entity by ID
            ClassGroupDomain classGroup = classGroupRepo.findById(createNewClassRequest.getClassGroupId())
                    .orElseThrow(() -> new IllegalArgumentException("Class group not found with ID: " + createNewClassRequest.getClassGroupId()));
            
            ClassDomain classDomain = new ClassDomain();

            classDomain.setClassName(createNewClassRequest.getClassName());
            classDomain.setSubjectId(createNewClassRequest.getSubjectId());
            classDomain.setClassGroup(classGroup);
            classDomain.setTeacherId(createNewClassRequest.getTeacherId());

            // Handle date parsing safely
            try {
                LocalDateTime classStart = DateUtils.formatDate(createNewClassRequest.getClassStart());
                LocalDateTime classEnd = DateUtils.formatDate(createNewClassRequest.getClassEnd());

                assert classEnd != null;
                if (classEnd.isBefore(classStart)) {
                    throw new IllegalArgumentException("Class end time cannot be before start time");
                }

                classDomain.setClassStart(classStart);
                classDomain.setClassEnd(classEnd);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format: " + e.getMessage());
            }

            newClassDomain = classRepo.save(classDomain);

            // Log success
            logger.info("Successfully created new class: {} for group ID: {}",
                    newClassDomain.getClassName(), classGroup.getGroupId());

        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "create new class", e.getMessage());
        }
        
        return newClassDomain;

    }
}

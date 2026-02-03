package org.demo.oems.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.*;
import org.demo.oems.payload.request.CreateClassInfoRequest;
import org.demo.oems.payload.request.CreateNewClassRequest;
import org.demo.oems.payload.response.ClassListsResponse;
import org.demo.oems.payload.response.ClassSummaryResponse;
import org.demo.oems.payload.response.StudentFilterResponse;
import org.demo.oems.payload.response.TeacherFilterResponse;
import org.demo.oems.repository.*;
import org.demo.oems.utils.CommonConstantUtils;
import org.demo.oems.utils.DateUtils;
import org.demo.oems.utils.ResponseUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ClassroomService {
    private static final Logger logger = LogManager.getLogger(ClassroomService.class);

    private final ClassRepo classRepo;

    private final ClassroomRepo classroomRepo;

    private final UserInfoRepo userInfoRepo;
    private final SubjectRepo subjectRepo;


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


    public Map<String, Object> getAllClassesInfo(){
        logger.debug("Get All Classes Info Start");
        Map<String, Object> serviceResponse = new HashMap<>();
        List<ClassListsResponse> data = new ArrayList<>();
        try{
            List<ClassDomain> classDomainList = classRepo.findAll();

            logger.debug("Loop through entire class lists :: {}", classDomainList.size());
            for(ClassDomain classDomain: classDomainList){
                ClassListsResponse classInfo = new ClassListsResponse();
                classInfo.setClassId(classDomain.getClassId());

//                classInfo.setClassYear(classDomain.getClassGroup().getAcademicYear());
                classInfo.setClassName(classDomain.getClassName());
                classInfo.setTeacherId(classDomain.getTeacherId());

                Optional<UserInfoDomain> optionalUser = userInfoRepo.findUserInfoDomainByUserId(classDomain.getTeacherId());
                optionalUser.ifPresent(userInfoDomain -> classInfo.setTeacherName(userInfoDomain.getName()));


                long studentCount = classroomRepo.countByClassId(classDomain.getClassId());

                classInfo.setStudentCount(studentCount);

                String classStatus = DateUtils.getClassStatus(classDomain.getClassStart(), classDomain.getClassEnd());

                String classStart = DateUtils.convertDateToString(classDomain.getClassStart());
                String classEnd = DateUtils.convertDateToString(classDomain.getClassEnd());

                classInfo.setClassStatus(classStatus);
                classInfo.setClassStart(classStart);
                classInfo.setClassEnd(classEnd);

                data.add(classInfo);
            }

            serviceResponse = ResponseUtils.formatAPIResponse("0", "success", data);

        }catch (Exception e){
            logger.error("Exception While Getting Classes Info Lists :: {}" , e.getMessage());

            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }
        logger.debug("Final Service Response :: {}", serviceResponse);
        return serviceResponse;
    }

    @Transactional
    public Map<String, Object> createNewClasses(CreateNewClassRequest createNewClassRequest) {
        Map<String, Object> serviceResponse = new HashMap<>();
        try{
            logger.debug("Start Create New Classes Information");
            ClassDomain newclass = new ClassDomain();

            //Step 1: Subject Name Inquiry
            Optional<SubjectDomain> subjectDomainOptional = subjectRepo.getSubjectDomainsById(createNewClassRequest.getSubjectId());
            if(subjectDomainOptional.isEmpty()){
                serviceResponse = ResponseUtils.formatAPIResponse("1", "Subject ID not found",  "");
                return serviceResponse;
            }

            SubjectDomain subjectDomain = subjectDomainOptional.get();
            String subjectName = subjectDomain.getSubjectName();

            String className = createNewClassRequest.getClassName() + " - " + subjectName;

            newclass.setClassName(className);
            newclass.setSubjectId(createNewClassRequest.getSubjectId());
            newclass.setTeacherId(createNewClassRequest.getTeacherId());
            newclass.setAcademicYear(createNewClassRequest.getAcademicYear());
            newclass.setClassStatus(createNewClassRequest.getClassStatus());

            // Handle date parsing safely
            try {
                LocalDateTime classStart = DateUtils.formatDate(createNewClassRequest.getClassStart());
                LocalDateTime classEnd = DateUtils.formatDate(createNewClassRequest.getClassEnd());

                assert classEnd != null;
                if (classEnd.isBefore(classStart)) {
                    logger.error("Exception :: class end time cannot be before start time");
                    serviceResponse = ResponseUtils.formatAPIResponse("1", "class end time cannot be before start time", "");
                    return serviceResponse;
                }

                newclass.setClassStart(classStart);
                newclass.setClassEnd(classEnd);
            } catch (Exception e) {
                logger.error("Exception :: {}", e.getMessage());
                serviceResponse = ResponseUtils.formatAPIResponse("1", "Invalid Date Format", "");
                return serviceResponse;
            }

            newclass = classRepo.save(newclass);
            serviceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Create New Class", newclass);

        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "create new class", e.getMessage());
            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }

        logger.debug("Final Service Response :: {}", serviceResponse);
        return serviceResponse;
    }

    public Map<String, Object> getClassesDashboard() {
        logger.debug("Get Classes Dashboard Summary");
        Map<String, Object> serviceResponse = new HashMap<>();
        ClassSummaryResponse classSummaryResponse = new ClassSummaryResponse();
        try{
            logger.debug("Counting Classes Summary");

            long totalClassesCount = classRepo.count();
            classSummaryResponse.setTotalClasses(totalClassesCount);

            LocalDateTime currentDate = LocalDateTime.now();

            long onGoingClassesCount = classRepo.countByClassStartLessThanEqualAndClassEndGreaterThanEqual(currentDate, currentDate);
            long completedClassesCount = classRepo.countByClassEndLessThan(currentDate);

            classSummaryResponse.setOnGoing(onGoingClassesCount);
            classSummaryResponse.setCompleted(completedClassesCount);

            long totalEnrollment = classroomRepo.count();
            classSummaryResponse.setTotalEnrollment(totalEnrollment);

            serviceResponse = ResponseUtils.formatAPIResponse("0", "success", classSummaryResponse);

        }catch (Exception e){
            logger.error("Exception While Getting Classes Info Lists :: {}" , e.getMessage());

            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }
        logger.debug("Final Service Response :: {}", serviceResponse);
        return serviceResponse;
    }

    @Transactional
    public Map<String, Object> editClassInfo(long classId, CreateNewClassRequest createNewClassRequest) {
        Map<String, Object> serviceResponse = new HashMap<>();
        try{
            logger.debug("Edit Class Information");

            Optional<ClassDomain> classDomainOptional = classRepo.findById(classId);
            if(classDomainOptional.isEmpty()){
                serviceResponse = ResponseUtils.formatAPIResponse("1", "Class ID not found",  "");
                return serviceResponse;
            }

            //Step 1: Subject Name Inquiry
            Optional<SubjectDomain> subjectDomainOptional = subjectRepo.getSubjectDomainsById(createNewClassRequest.getSubjectId());
            if(subjectDomainOptional.isEmpty()){
                serviceResponse = ResponseUtils.formatAPIResponse("1", "Subject ID not found",  "");
                return serviceResponse;
            }

            SubjectDomain subjectDomain = subjectDomainOptional.get();
            String subjectName = subjectDomain.getSubjectName();

            String className = createNewClassRequest.getClassName() + " - " + subjectName;

            ClassDomain classInfo = classDomainOptional.get();
            classInfo.setClassName(className);
            classInfo.setSubjectId(createNewClassRequest.getSubjectId());
            classInfo.setTeacherId(createNewClassRequest.getTeacherId());
            classInfo.setAcademicYear(createNewClassRequest.getAcademicYear());
            classInfo.setClassStatus(createNewClassRequest.getClassStatus());

            // Handle date parsing safely
            try {
                LocalDateTime classStart = DateUtils.formatDate(createNewClassRequest.getClassStart());
                LocalDateTime classEnd = DateUtils.formatDate(createNewClassRequest.getClassEnd());

                assert classEnd != null;
                if (classEnd.isBefore(classStart)) {
                    logger.error("Exception :: class end time cannot be before start time");
                    serviceResponse = ResponseUtils.formatAPIResponse("1", "class end time cannot be before start time", "");
                    return serviceResponse;
                }

                classInfo.setClassStart(classStart);
                classInfo.setClassEnd(classEnd);
            } catch (Exception e) {
                logger.error("Exception :: {}", e.getMessage());
                serviceResponse = ResponseUtils.formatAPIResponse("1", "Invalid Date Format", "");
                return serviceResponse;
            }

            classInfo = classRepo.save(classInfo);
            serviceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Create New Class", classInfo);

        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "create new class", e.getMessage());
            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }

        logger.debug("Final Service Response :: {}", serviceResponse);
        return serviceResponse;
    }

    public Map<String, Object> getTeacherLists() {
        Map<String, Object> serviceResponse = new HashMap<>();
        List<TeacherFilterResponse> teacherLists = new ArrayList<>();
        try{
            logger.debug("Get Teacher LIst");
            List<UserInfoDomain> userInfoDomainList = userInfoRepo.findByRole_RoleName(CommonConstantUtils.VALUE_TEACHER);

            for(UserInfoDomain userInfo: userInfoDomainList){
                TeacherFilterResponse teacherInfo = new TeacherFilterResponse();
                teacherInfo.setId(userInfo.getUserId());
                teacherInfo.setName(userInfo.getName());
                teacherLists.add(teacherInfo);
            }

            serviceResponse = ResponseUtils.formatAPIResponse("0", "success", teacherLists);

            return serviceResponse;
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "getTeacherLists", e.getMessage());
            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return serviceResponse;
        }
    }

    public Map<String, Object> getStudentLists() {
        Map<String, Object> serviceResponse = new HashMap<>();
        List<StudentFilterResponse> studentLists = new ArrayList<>();
        try{
            logger.debug("Get Teacher LIst");
            List<UserInfoDomain> userInfoDomainList = userInfoRepo.findByRole_RoleName(CommonConstantUtils.VALUE_STUDENT);

            for(UserInfoDomain userInfo: userInfoDomainList){
                StudentFilterResponse studentInfo = new StudentFilterResponse();
                studentInfo.setId(userInfo.getUserId());
                studentInfo.setName(userInfo.getName());
                studentInfo.setEmail(userInfo.getEmail());
                studentLists.add(studentInfo);
            }

            serviceResponse = ResponseUtils.formatAPIResponse("0", "success", studentLists);
            return serviceResponse;
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "getTeacherLists", e.getMessage());
            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return serviceResponse;
        }
    }


}

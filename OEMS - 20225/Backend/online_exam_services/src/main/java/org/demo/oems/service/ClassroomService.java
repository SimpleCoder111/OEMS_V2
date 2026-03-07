package org.demo.oems.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.*;
import org.demo.oems.payload.request.CreateClassInfoRequest;
import org.demo.oems.payload.request.CreateNewClassRequest;
import org.demo.oems.payload.request.UpdateEnrollmentStatusRequest;
import org.demo.oems.payload.response.*;
import org.demo.oems.repository.*;
import org.demo.oems.utils.*;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.demo.oems.utils.CommonConstantUtils.*;

@Service
@RequiredArgsConstructor
public class ClassroomService {
    private static final Logger logger = LogManager.getLogger(ClassroomService.class);

    private final ClassRepo classRepo;

    private final ClassroomRepo classroomRepo;

    private final UserInfoRepo userInfoRepo;

    private final SubjectRepo subjectRepo;

    private final QRCodeUtils qrCodeUtils;

    private final TokenGeneratorUtils tokenGeneratorUtils;


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
            serviceResponse = getStringObjectMap(data, classDomainList);

        }catch (Exception e){
            logger.error("Exception While Getting Classes Info Lists :: {}" , e.getMessage());
            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }
        logger.debug("Final Service Response :: {}", serviceResponse);
        return serviceResponse;
    }

    public Map<String, Object> getAllClassesInfoByTeacherId(String teacherId){
        logger.debug("Get All Classes Info By Teacher ID :: {}", teacherId);
        Map<String, Object> serviceResponse = new HashMap<>();
        List<ClassListsResponse> data = new ArrayList<>();
        try{
            List<ClassDomain> classDomainList = classRepo.getClassDomainsByTeacherIdEqualsIgnoreCase(teacherId);
            serviceResponse = getStringObjectMap(data, classDomainList);
        }catch (Exception e){
            logger.error("Exception While Getting Classes Info Lists :: {}" , e.getMessage());
            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }
        logger.debug("Final Service Response :: {}", serviceResponse);
        return serviceResponse;
    }

    private Map<String, Object> getStringObjectMap(List<ClassListsResponse> data, List<ClassDomain> classDomainList) {
        Map<String, Object> serviceResponse;
        logger.debug("Loop through entire class lists :: {}", classDomainList.size());
        for(ClassDomain classDomain: classDomainList){
            ClassListsResponse classInfo = new ClassListsResponse();
            classInfo.setClassId(classDomain.getClassId());

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

    //==========================================================
    //Teacher Class Service - Generate QR
    //==========================================================
    public Map<String, Object> generateQRforClass(Long classId) {
        logger.debug("Start - generateQRforClass :: {}", classId);
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            Optional<ClassDomain> classDomainOptional = classRepo.findById(classId);

            if(classDomainOptional.isEmpty()){
                logger.error("Class not found");
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Class not found", "");
                return finalServiceResponse;
            }

            ClassDomain classDomain = classDomainOptional.get();
            String classToken = tokenGeneratorUtils.generateStaticToken(classDomain.getClassId(),10);

            classDomain.setClassToken(classToken);
            classRepo.save(classDomain);

            String joinUrl = qrCodeUtils.generateJoinUrl(classDomain.getClassId(), classToken);

            String base64 = qrCodeUtils.generateBase64Qr(joinUrl, 380);

            String qrBase64 = "data:image/png;base64," + base64;

            GenerateQRResponse data = new GenerateQRResponse();
            data.setToken(classToken);
            data.setJoinUrl(joinUrl);
            data.setQrBase64(qrBase64);

            finalServiceResponse = ResponseUtils.formatAPIResponse("0", "success", data);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public Map<String, Object> requestJoinClass(String studentId, String token) {
        logger.debug("Start - requestJoinClass :: {}", token);
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            Optional<ClassDomain> classDomainOptional = classRepo.getClassDomainByClassToken(token);
            if(classDomainOptional.isEmpty()){
                logger.error("Class token not found");
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Class not found", "");
                return finalServiceResponse;
            }

            Optional<UserInfoDomain> userInfoDomainOptional = userInfoRepo.findUserInfoDomainByUserId(studentId);
            if(userInfoDomainOptional.isEmpty()){
                logger.error("User info not found");
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "User info not found", "");
                return finalServiceResponse;
            }


            ClassDomain classDomain = classDomainOptional.get();
            Optional<ClassroomDomain> classEnrollmentOptional = classroomRepo.findClassroomDomainByClassIdAndStudentId(classDomain.getClassId(), studentId);

            if(classEnrollmentOptional.isPresent()){
                logger.debug("Already request to join class");
                ClassroomDomain classEnrollment = classEnrollmentOptional.get();
                if(classEnrollment.getStatus().equalsIgnoreCase(VALUE_APPROVED)){
                    finalServiceResponse = ResponseUtils.formatAPIResponse("0", "You have already joined the class.", "");
                }else if(classEnrollment.getStatus().equalsIgnoreCase(VALUE_PENDING)){
                    finalServiceResponse = ResponseUtils.formatAPIResponse("0", "You have already request to join the class. Please wait for the teacher to approve for join request", "");
                }
                return finalServiceResponse;
            }

            ClassroomDomain classEnrollment = new ClassroomDomain();
            classEnrollment.setClassId(classDomain.getClassId());
            classEnrollment.setEnrolledAt(LocalDateTime.now());
            classEnrollment.setStatus(VALUE_PENDING);
            classEnrollment.setStudentId(studentId);

            classroomRepo.save(classEnrollment);

            finalServiceResponse = ResponseUtils.formatAPIResponse("0", "success", classEnrollment);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public Map<String, Object> getPendingEnrollmentsList(long classId) {
        logger.debug("Start - getPendingEnrollmentsList :: {}", classId);
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            logger.debug("Validate Class ID :: {}", classId);
            ClassDomain classDomain = this.findClassDomainById(classId);
            if(classDomain == null){
               finalServiceResponse = ResponseUtils.formatAPIResponse("400", "Class not found", "");
               return finalServiceResponse;
            }

            logger.debug("Find Class Enroll Lists with Pending Status");
            List<ClassroomDomain> classEnrollmentLists = classroomRepo.findClassroomDomainsByClassIdAndStatusEqualsIgnoreCase(classId, VALUE_PENDING);

            List<PendingEnrollStudentResponse> pendingEnrollStudentResponseList = new ArrayList<>();
            for(ClassroomDomain classEnrollment: classEnrollmentLists){

                logger.debug("Get Student Info with Pending Status");

                String studentId = classEnrollment.getStudentId();
                Optional<UserInfoDomain> studentInfoOptional = userInfoRepo.findUserInfoDomainByUserId(studentId);

                if(studentInfoOptional.isPresent()){
                    UserInfoDomain studentInfo = studentInfoOptional.get();
                    PendingEnrollStudentResponse pendingEnrollStudentResponse = new PendingEnrollStudentResponse();
                    pendingEnrollStudentResponse.setStudentEmail(studentInfo.getEmail());
                    pendingEnrollStudentResponse.setStudentName(studentInfo.getName());

                    String requestAt = DateUtils.convertDateToString(classEnrollment.getEnrolledAt());
                    pendingEnrollStudentResponse.setRequestAt(requestAt);
                    pendingEnrollStudentResponse.setStatus(classEnrollment.getStatus());
                    pendingEnrollStudentResponse.setClassEnrolledId(classEnrollment.getEnrollmentId());

                    pendingEnrollStudentResponseList.add(pendingEnrollStudentResponse);
                }
            }

            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "success", pendingEnrollStudentResponseList);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public ClassDomain findClassDomainById(long classId){
        Optional<ClassDomain> classDomainOptional = classRepo.findById(classId);
        return classDomainOptional.orElse(null);
    }

    public Map<String, Object> approveOrRejectStudentEnrollment(UpdateEnrollmentStatusRequest updateEnrollmentStatusRequest) {
        logger.debug("Start - approveOrRejectStudentEnrollment :: {}", updateEnrollmentStatusRequest);
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            ClassroomDomain classroomDomain = findClassroomDomainById(updateEnrollmentStatusRequest.getClassEnrolledId());
            if(classroomDomain == null){
                finalServiceResponse = ResponseUtils.formatAPIResponse("400", "Enrollment not found", "");
                return finalServiceResponse;
            }

            String updateEnrollStatus = updateEnrollmentStatusRequest.getIsApproved() ? VALUE_APPROVED : VALUE_REJECTED;
            classroomDomain.setStatus(updateEnrollStatus);

            classroomRepo.save(classroomDomain);
            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "success", classroomDomain);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public ClassroomDomain findClassroomDomainById(long classEnrollmentId){
        Optional<ClassroomDomain> classDomainOptional = classroomRepo.findById(classEnrollmentId);
        return classDomainOptional.orElse(null);
    }

    public Map<String, Object> getAllClassesByStudentId(String studentId) {
        Map<String, Object> finalServiceResponse = new HashMap<>();
        List<ClassDomain> classesListResponse = new ArrayList<>();
        try{
            logger.debug("Get All Classes By Student ID :: {}", studentId);
            //Step 1: Validate User ID
            Optional<UserInfoDomain> userInfoDomainOptional = userInfoRepo.findUserInfoDomainByUserId(studentId);

            if(userInfoDomainOptional.isEmpty()){
                logger.error("User info not exists");
                finalServiceResponse = ResponseUtils.formatAPIResponse("400", "User not found", "");
                return finalServiceResponse;
            }


            //Step 2: find all class enrollment with user ID
            List<ClassroomDomain> classEnrollmentLists = classroomRepo.findClassroomDomainsByStudentIdAndStatus(studentId, VALUE_APPROVED);

            //Step 3: Loop through classEnrollmentLists and get Class Info
            for(ClassroomDomain classroomDomain : classEnrollmentLists){
                long classId = classroomDomain.getClassId();

                Optional<ClassDomain> classDomainOptional = classRepo.findById(classId);

                classDomainOptional.ifPresent(classesListResponse::add);

            }
            finalServiceResponse = ResponseUtils.formatAPIResponse("200", "Success", classesListResponse);
            return finalServiceResponse;

        }catch (Exception e){
            logger.error("Exception :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("500", e.getMessage(), "");
            return finalServiceResponse;
        }
    }

    public ClassDomain getClassInfoById(long classId){
        Optional<ClassDomain> classDomainOptional = classRepo.findById(classId);
        return classDomainOptional.orElse(null);
    }
}

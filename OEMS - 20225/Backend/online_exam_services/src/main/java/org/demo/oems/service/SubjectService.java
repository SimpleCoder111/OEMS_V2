package org.demo.oems.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.ChapterDomain;
import org.demo.oems.domain.ClassDomain;
import org.demo.oems.domain.SubjectDomain;
import org.demo.oems.payload.request.*;
import org.demo.oems.payload.response.ChapterResponse;
import org.demo.oems.payload.response.GetChaptersBySubjectResponse;
import org.demo.oems.payload.response.SubjectResponse;
import org.demo.oems.payload.response.SubjectSummaryResponse;
import org.demo.oems.repository.ClassRepo;
import org.demo.oems.repository.QuestionBankRepo;
import org.demo.oems.repository.SubjectChapterRepo;
import org.demo.oems.repository.SubjectRepo;
import org.demo.oems.utils.CommonConstantUtils;
import org.demo.oems.utils.DateUtils;
import org.demo.oems.utils.ResponseUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class SubjectService {
    private final Logger logger = LogManager.getLogger(SubjectService.class);

    private final SubjectRepo subjectRepo;

    private final ClassRepo classRepo;

    private final SubjectChapterRepo chapterRepo;
    private final QuestionBankRepo questionBankRepo;

    public List<SubjectDomain> getSubjectList(){
        return subjectRepo.findAll();
    }

    public JSONObject addNewSubject(CreateSubjectInfoRequest request){
        JSONObject apiResponse = new JSONObject();
        try{
            logger.debug("Try to added new subject");
            SubjectDomain subjectDomain = new SubjectDomain();
            subjectDomain.setSubjectName(request.getSubjectName());
            subjectDomain.setDescription(request.getDescription());

            subjectRepo.save(subjectDomain);
            apiResponse = ResponseUtils.formatServiceResponse("0", "success");
            logger.debug("final service response :: {}", apiResponse);
            return apiResponse;
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "added new subject", e.getMessage());
            apiResponse = ResponseUtils.formatServiceResponse("1", e.getMessage());
            return apiResponse;
        }
    }

    public JSONObject addChaptersToSubject(List<CreateSubjectChapter> chapterLists){
        JSONObject apiResponse = new JSONObject();
        try{
            logger.debug("Add Chapters To Subject Services :: {}", chapterLists);
            int listSize = chapterLists.size();

            if (listSize == 0){
                apiResponse = ResponseUtils.formatServiceResponse("1", "No Record to insert");
                return apiResponse;
            }

            int insertSize = 0;

            for (int i = 0; i < listSize; i++){
                CreateSubjectChapter chapterDomain = chapterLists.get(i);

                long subjectId = chapterDomain.getSubjectId();

                boolean isSubjectExist = checkIfSubjectExists(subjectId);
                logger.debug("Is Subject Exist :: {}", isSubjectExist);

                if(!isSubjectExist){
                    apiResponse = ResponseUtils.formatServiceResponse("1", "Please Create Subject Info First");
                    return apiResponse;
                }

                ChapterDomain newChapterDomain = new ChapterDomain();
                newChapterDomain.setChapter(chapterDomain.getChapter());

                Optional<SubjectDomain> subjectOpt = subjectRepo.getSubjectDomainsById(subjectId);

                subjectOpt.ifPresent(newChapterDomain::setSubject);
                chapterRepo.save(newChapterDomain);

                insertSize++;
                logger.debug("Successfully Insert records :: {}", i + 1);
            }

            String responseMessage = "Successfully Insert " + insertSize + " records";
            apiResponse = ResponseUtils.formatServiceResponse("0", responseMessage);
            logger.debug("final service response :: {}", apiResponse);
            return apiResponse;
        }catch (Exception e){

            logger.error("Exception while trying to added new subject :: {}", e.getMessage());
            apiResponse = ResponseUtils.formatServiceResponse("1", e.getMessage());
            return apiResponse;
        }
    }

    boolean checkIfSubjectExists(long subjectId){
        logger.debug("Trying to check subject info exists :: {}", subjectId);
        Optional<SubjectDomain> subjectInfo = subjectRepo.getSubjectDomainsById(subjectId);
        return subjectInfo.isPresent();
    }

    public JSONObject getChaptersBySubject(long subjectId){
        JSONObject apiResponse = new JSONObject();
        try{
            logger.debug("Get Chapters By Subject ID :: {}", subjectId);
            Optional<SubjectDomain> subjectInfoOpt = subjectRepo.getSubjectDomainsById(subjectId);

            if(subjectInfoOpt.isEmpty()){
                apiResponse = ResponseUtils.formatServiceResponse("1", "Please Create Subject Info First");
                return apiResponse;
            }

            SubjectDomain subjectInfo = subjectInfoOpt.get();

            List<ChapterDomain> subjectChaptersList = chapterRepo.findSubjectChapterDomainsBySubjectIdOrderByChapterIndexAsc(subjectId);

            apiResponse = ResponseUtils.formatServiceResponse("0", "Success");

            GetChaptersBySubjectResponse dataResponse = new GetChaptersBySubjectResponse();
            dataResponse.setSubjectId(subjectInfo.getId());
            dataResponse.setSubjectName(subjectInfo.getSubjectName());
            dataResponse.setChapterDomainList(subjectChaptersList);

            apiResponse.put("data", dataResponse);

            return apiResponse;
        }catch (Exception e){
            logger.error("Exception while trying to added new subject :: {}", e.getMessage());
            apiResponse = ResponseUtils.formatServiceResponse("1", e.getMessage());
            return apiResponse;
        }
    }


    public List<SubjectResponse> getAllSubjects() {
        List<SubjectResponse> subjectResponseList = new ArrayList<>();
        try{
            logger.debug("Start Get All Subjects Service");

            List<SubjectDomain> subjectDomainList = subjectRepo.findAll();
            logger.debug("Found total subject :: {}", subjectDomainList.size());
            for (SubjectDomain subjectDomain : subjectDomainList) {
                SubjectResponse subjectResponse = new SubjectResponse();

                subjectResponse.setId(subjectDomain.getId());
                subjectResponse.setDescription(subjectDomain.getDescription());
                subjectResponse.setCode(subjectDomain.getSubjectCode());
                subjectResponse.setName(subjectDomain.getSubjectName());

                boolean isActive = subjectDomain.getStatus() != null && subjectDomain.getStatus().equalsIgnoreCase("ACTIVE");
                subjectResponse.setActive(isActive);

                String createdAt = DateUtils.convertTimestampToString(subjectDomain.getCreatedAt());
                String updatedAt =  DateUtils.convertTimestampToString(subjectDomain.getUpdatedAt());

                subjectResponse.setCreatedAt(createdAt);
                subjectResponse.setUpdatedAt(updatedAt);

                List<ChapterResponse> chapterResponseList = new ArrayList<>();
                List<ChapterDomain> chapterDomainLists = chapterRepo.findSubjectChapterDomainsBySubjectIdOrderByChapterIndexAsc(subjectDomain.getId());

                logger.debug("Found total chapter for the subject {} :: {}", subjectDomain.getSubjectName(), chapterDomainLists.size());
                for(ChapterDomain chapterDomain : chapterDomainLists){
                    ChapterResponse chapterResponse = getChapterResponse(chapterDomain);
                    chapterResponseList.add(chapterResponse);
                }

                subjectResponse.setChapterResponseList(chapterResponseList);
                subjectResponseList.add(subjectResponse);

            }

        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "Get All Subjects Services", e.getMessage());
        }
        
        return subjectResponseList;

    }

    private static ChapterResponse getChapterResponse(ChapterDomain chapterDomain) {
        ChapterResponse chapterResponse = new ChapterResponse();

        chapterResponse.setId(chapterDomain.getId());
        chapterResponse.setName(chapterDomain.getChapter());
        chapterResponse.setOrderIndex(chapterDomain.getChapterIndex());
        boolean isActive = chapterDomain.getChapterStatus() != null && chapterDomain.getChapterStatus().equalsIgnoreCase("ACTIVE");
        chapterResponse.setActive(isActive);

        chapterResponse.setQuestionCount(0);
        return chapterResponse;
    }

    public List<SubjectResponse> getAllSubjectsByTeacherId(String teacherId) {
        List<SubjectResponse> subjectResponseList = new ArrayList<>();
        try{
            logger.debug("Start Get All Subjects By Teacher ID Service :: {}", teacherId);

            List<ClassDomain> classLists = classRepo.getClassDomainsByTeacherIdEqualsIgnoreCase(teacherId);
            logger.debug("Found {} classes teaching by teacher ID :: {}", classLists.size(), teacherId);

            for(ClassDomain classDomain : classLists){

                Long subjectId = classDomain.getSubjectId();
                logger.debug("Fetch subject info with subject ID :: {}", subjectId);
                Optional<SubjectDomain> subjectDomainOpt = subjectRepo.findById(subjectId);

                if(subjectDomainOpt.isPresent()) {
                    logger.debug("Subject info is found for :: {}", subjectId);
                    SubjectDomain subjectDomain = subjectDomainOpt.get();
                    SubjectResponse subjectResponse = new SubjectResponse();
                    subjectResponse.setId(subjectDomain.getId());
                    subjectResponse.setName(subjectDomain.getSubjectName());
                    subjectResponse.setCode(subjectDomain.getSubjectCode());
                    subjectResponse.setDescription(subjectDomain.getDescription());

                    boolean isActive = subjectDomain.getStatus() != null && subjectDomain.getStatus().equalsIgnoreCase("ACTIVE");
                    subjectResponse.setActive(isActive);

                    String createdAt = DateUtils.convertTimestampToString(subjectDomain.getCreatedAt());
                    String updatedAt =  DateUtils.convertTimestampToString(subjectDomain.getUpdatedAt());


                    subjectResponse.setUpdatedAt(updatedAt);
                    subjectResponse.setCreatedAt(createdAt);

                    logger.debug("Subject Response :: {}", subjectResponse);
                    subjectResponseList.add(subjectResponse);
                }

            }

        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "Get Subjects for Teacher", e.getMessage());
        }

        logger.debug("Final Subject Response :: " + subjectResponseList);
        return subjectResponseList;
    }

    public Map<String, Object> createNewSubject(CreateSubjectRequest createSubjectRequest) {
        Map<String, Object> serviceResponse = new HashMap<>();
        try{
            Optional<SubjectDomain> subjectOptional = subjectRepo.getSubjectDomainBySubjectCodeEqualsIgnoreCase(createSubjectRequest.getCode());
            if(subjectOptional.isPresent()){
                logger.error("Subject Code is alreayd existed");
                serviceResponse = ResponseUtils.formatAPIResponse("1", "Subject Code Already Exists", "");
            }else{

                logger.debug("Prepared Data for create new subject Info");
                SubjectDomain newSubject = new SubjectDomain();

                newSubject.setSubjectName(createSubjectRequest.getName());
                newSubject.setSubjectCode(createSubjectRequest.getCode());
                newSubject.setDescription(createSubjectRequest.getDescription());

                newSubject.setStatus(Boolean.TRUE.equals(createSubjectRequest.getIsActive()) ? "ACTIVE": "INACTIVE");

                LocalDateTime currentTime = LocalDateTime.now();
                newSubject.setCreatedAt(currentTime);
                newSubject.setUpdatedAt(currentTime);

                subjectRepo.save(newSubject);
                serviceResponse = ResponseUtils.formatAPIResponse("0", "success", newSubject);
            }
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "Create New Subject", e.getMessage());
            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }
        logger.debug("Final Service Response {}", serviceResponse);
        return serviceResponse;
    }

    public Map<String, Object> editSubjectInfo(long subjectId, CreateSubjectRequest createSubjectRequest) {
        Map<String, Object> serviceResponse = new HashMap<>();
        try{
            Optional<SubjectDomain> subjectOptional = subjectRepo.getSubjectDomainsById(subjectId);
            if(subjectOptional.isEmpty()){
                logger.error("Cannot find the subject ID");
                serviceResponse = ResponseUtils.formatAPIResponse("1", "Cannot find subject info", "");
            }else{

                SubjectDomain subjectDomain = subjectOptional.get();
                logger.debug("Prepared Data for edit subject Info");

                subjectDomain.setSubjectName(createSubjectRequest.getName());
                subjectDomain.setSubjectCode(createSubjectRequest.getCode());
                subjectDomain.setDescription(createSubjectRequest.getDescription());

                subjectDomain.setStatus(Boolean.TRUE.equals(createSubjectRequest.getIsActive()) ? "ACTIVE": "INACTIVE");

                LocalDateTime currentTime = LocalDateTime.now();
                subjectDomain.setUpdatedAt(currentTime);

                subjectRepo.save(subjectDomain);
                serviceResponse = ResponseUtils.formatAPIResponse("0", "success", subjectDomain);
            }
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "Create New Subject", e.getMessage());
            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }
        logger.debug("Final Service Response {}", serviceResponse);
        return serviceResponse;
    }


    @Transactional
    public Map<String, Object> deleteSubjectAndChapterRelated(long subjectId) {
        Map<String, Object> serviceResponse = new HashMap<>();
        try{
            Optional<SubjectDomain> subjectOptional = subjectRepo.getSubjectDomainsById(subjectId);
            if(subjectOptional.isEmpty()){
                logger.error("Cannot find the subject ID");
                serviceResponse = ResponseUtils.formatAPIResponse("1", "Cannot find subject info", "");
            }else{
                SubjectDomain subjectDomain = subjectOptional.get();

                int questionCount = questionBankRepo.countBySubject_Id(subjectId);
                logger.debug("Going to delete {} questions related to the subject", questionCount);
                questionBankRepo.deleteBySubject_Id(subjectId);

                int chapterCount = chapterRepo.countBySubject_id(subjectId);
                logger.debug("Going to delete {} chapters related to the subject", chapterCount);
                chapterRepo.deleteBySubject_Id(subjectDomain.getId());

                logger.debug("Going to delete subject info :: {}", subjectDomain.getId());
                subjectRepo.deleteById(subjectId);
                serviceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Delete", subjectDomain);
            }
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "Delete Subject", e.getMessage());
            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }
        logger.debug("Final Service Response {}", serviceResponse);
        return serviceResponse;
    }

    public Map<String, Object> updateSubjectStatus(long subjectId, Boolean isActive) {
        Map<String, Object> serviceResponse = new HashMap<>();
        try{
            Optional<SubjectDomain> subjectOptional = subjectRepo.getSubjectDomainsById(subjectId);
            if(subjectOptional.isEmpty()){
                logger.error("Cannot find the subject ID");
                serviceResponse = ResponseUtils.formatAPIResponse("1", "Cannot find subject info", "");
            }else{

                SubjectDomain subjectDomain = subjectOptional.get();
                logger.debug("Going to delete subjcet info :: {}", subjectDomain.getId());

                subjectDomain.setStatus(Boolean.TRUE.equals(isActive) ? "ACTIVE": "INACTIVE");
                LocalDateTime currentTimestamp = LocalDateTime.now();
                subjectDomain.setUpdatedAt(currentTimestamp);

                subjectRepo.save(subjectDomain);
                serviceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Update Subject Status", subjectDomain);
            }
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "Delete Subject", e.getMessage());
            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }
        logger.debug("Final Service Response {}", serviceResponse);
        return serviceResponse;
    }

    @Transactional
    public Map<String, Object> insertNewChaptersForSubject(long subjectId, List<CreateChapterRequest> chapterLists) {
        Map<String, Object> serviceResponse = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        List<ChapterDomain> chapterListResponse = new ArrayList<>();

        try{
            Optional<SubjectDomain> subjectOptional = subjectRepo.getSubjectDomainsById(subjectId);
            if(subjectOptional.isEmpty()){
                logger.error("Cannot find the subject ID");
                serviceResponse = ResponseUtils.formatAPIResponse("1", "Subject Info not found. Please Create Subject Info First !!!", "");
            }else{
                SubjectDomain subjectDomain = subjectOptional.get();

                int count = 0;
                for (CreateChapterRequest chapter : chapterLists) {
                    ChapterDomain newChapter = new ChapterDomain();

                    newChapter.setSubject(subjectDomain);
                    newChapter.setChapterIndex(chapter.getIndex());
                    newChapter.setChapter(chapter.getName());
                    newChapter.setChapterStatus(checkStatusAndReturnString(chapter.getIsActive()));
                    newChapter.setChapterDescription(chapter.getDescription());

                    chapterRepo.save(newChapter);


                    count++;
                    logger.debug("successfully insert record :: {}", count);
                    chapterListResponse.add(newChapter);
                }

                data.put("subjectId", subjectId);
                data.put("totalInsert", count);
                data.put("chapterLists", chapterListResponse);
                data.put("subjectName", subjectDomain.getSubjectName());


                serviceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Update Subject Status", data);
            }
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "Delete Subject", e.getMessage());
            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }
        logger.debug("Final Service Response {}", serviceResponse);
        return serviceResponse;

    }

    public static String checkStatusAndReturnString(Boolean isActive){
        return Boolean.TRUE.equals(isActive) ? "ACTIVE": "INACTIVE";
    }

    public Map<String, Object> editChapterInfo(long chapterId, CreateChapterRequest newChapterInfo) {

        Map<String, Object> serviceResponse = new HashMap<>();
        try{
            Optional<ChapterDomain> chapterOptional = chapterRepo.findSubjectChapterDomainById(chapterId);
            if(chapterOptional.isEmpty()){
                logger.error("Cannot find the chapter ID");
                serviceResponse = ResponseUtils.formatAPIResponse("1", "Record not found", "");
            }else{
                ChapterDomain chapterDomain = chapterOptional.get();
                logger.debug("Prepared Data for edit chapter Info");

                chapterDomain.setChapter(newChapterInfo.getName());
                chapterDomain.setChapterIndex(newChapterInfo.getIndex());

                chapterDomain.setChapterStatus(checkStatusAndReturnString(newChapterInfo.getIsActive()));
                chapterDomain.setChapterDescription(newChapterInfo.getDescription());

                chapterRepo.save(chapterDomain);
                serviceResponse = ResponseUtils.formatAPIResponse("0", "Successfully updated", chapterDomain);
            }
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "Create New Subject", e.getMessage());
            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }
        logger.debug("Final Service Response {}", serviceResponse);
        return serviceResponse;
    }

    public Map<String, Object> updateChapterStatus(long chapterId, Boolean isActive) {
        Map<String, Object> serviceResponse = new HashMap<>();
        try{
            Optional<ChapterDomain> chapterOptional = chapterRepo.findSubjectChapterDomainById(chapterId);
            if(chapterOptional.isEmpty()){
                logger.error("Cannot chapter info with ID :: {}", chapterId);
                serviceResponse = ResponseUtils.formatAPIResponse("1", "Record not found", "");
            }else{

                ChapterDomain chapterDomain = chapterOptional.get();
                logger.debug("Prepared Data for edit chapter Info");

                chapterDomain.setChapterStatus(checkStatusAndReturnString(isActive));
                chapterRepo.save(chapterDomain);
                serviceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Update Chapter Status", chapterDomain);
            }
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "Toggle Subject Status", e.getMessage());
            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }
        logger.debug("Final Service Response {}", serviceResponse);
        return serviceResponse;
    }


    @Transactional
    public Map<String, Object> deleteChapterAndQuestionRelated(long chapterId) {
        Map<String, Object> serviceResponse = new HashMap<>();
        try{
            Optional<ChapterDomain> chapterOptional = chapterRepo.findSubjectChapterDomainById(chapterId);
            if(chapterOptional.isEmpty()){
                logger.error("Cannot find the subject ID");
                serviceResponse = ResponseUtils.formatAPIResponse("1", "Cannot find subject info", "");
            }else{

                ChapterDomain chapterDomain = chapterOptional.get();
                logger.debug("Going to delete chapter info and question related :: {}", chapterDomain.getId());

                int questionCount = questionBankRepo.countByChapter_Id(chapterId);
                logger.debug("Going to delete {} questions related to the chapters", questionCount);
                questionBankRepo.deleteByChapter_Id(chapterId);

                logger.debug("Delete the chapter ID :: {}", chapterId);
                chapterRepo.deleteById(chapterId);

                serviceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Delete", chapterDomain);
            }
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "Delete Subject", e.getMessage());
            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }
        logger.debug("Final Service Response {}", serviceResponse);
        return serviceResponse;
    }

    public Map<String, Object> orderChapterIndex(Long subjectId, List<OrderChapterRequest> orderChapterRequestList) {
        Map<String, Object> serviceResponse = new HashMap<>();
        try{
            logger.debug("Reorder chapter index");
            Optional<SubjectDomain> subjectOptional = subjectRepo.findById(subjectId);
            if(subjectOptional.isEmpty()){
                serviceResponse = ResponseUtils.formatAPIResponse("1", "Please insert subject info first", "");
                return serviceResponse;
            }

            logger.debug("Start loop through chapter order");
            for (OrderChapterRequest newChapterOrder : orderChapterRequestList) {
                Optional<ChapterDomain> chapterOptional = chapterRepo.findSubjectChapterDomainById(newChapterOrder.getId());

                if(chapterOptional.isPresent()){
                    ChapterDomain chapterDomain = chapterOptional.get();
                    chapterDomain.setChapterIndex(newChapterOrder.getIndex());
                    logger.debug("Update chapter id {} to order {}", newChapterOrder.getId(), newChapterOrder.getIndex());
                    chapterRepo.save(chapterDomain);
                }
            }

            List<ChapterDomain> chapterDomainList = chapterRepo.findSubjectChapterDomainsBySubjectIdOrderByChapterIndexAsc(subjectId);
            serviceResponse =  ResponseUtils.formatAPIResponse("0", "Successfully Order the chapter", chapterDomainList);

        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "Toggle Subject Status", e.getMessage());
            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }
        logger.debug("Final Service Response {}", serviceResponse);
        return serviceResponse;
    }

    public Map<String, Object> getSubjectSummaryDashboard() {
        logger.debug("Start subject summary dashboard services");
        Map<String, Object> serviceResponse = new HashMap<>();
        SubjectSummaryResponse summaryResponse = new SubjectSummaryResponse();

        try{

            long activeSubject = subjectRepo.countByStatusEqualsIgnoreCase("ACTIVE");
            long totalSubject = subjectRepo.count();
            long totalChapter = chapterRepo.count();
            long totalQuestions = questionBankRepo.count();

            summaryResponse.setActiveSubject(activeSubject);
            summaryResponse.setTotalSubject(totalSubject);
            summaryResponse.setTotalChapter(totalChapter);
            summaryResponse.setTotalQuestion(totalQuestions);

            serviceResponse = ResponseUtils.formatAPIResponse("0", "Success", summaryResponse);

        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "Toggle Subject Status", e.getMessage());
            serviceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }
        logger.debug("Final Service Response {}", serviceResponse);
        return serviceResponse;



    }
}

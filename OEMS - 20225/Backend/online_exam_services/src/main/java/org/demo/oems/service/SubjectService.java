package org.demo.oems.service;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.ChapterDomain;
import org.demo.oems.domain.ClassDomain;
import org.demo.oems.domain.SubjectDomain;
import org.demo.oems.payload.request.CreateSubjectChapter;
import org.demo.oems.payload.request.CreateSubjectInfoRequest;
import org.demo.oems.payload.response.ChapterResponse;
import org.demo.oems.payload.response.GetChaptersBySubjectResponse;
import org.demo.oems.payload.response.SubjectResponse;
import org.demo.oems.repository.ClassRepo;
import org.demo.oems.repository.SubjectChapterRepo;
import org.demo.oems.repository.SubjectRepo;
import org.demo.oems.utils.CommonConstantUtils;
import org.demo.oems.utils.DateUtils;
import org.demo.oems.utils.ResponseUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SubjectService {
    private final Logger logger = LogManager.getLogger(SubjectService.class);

    private final SubjectRepo subjectRepo;

    private final ClassRepo classRepo;

    private final SubjectChapterRepo chapterRepo;

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

            List<ChapterDomain> subjectChaptersList = chapterRepo.findSubjectChapterDomainsBySubjectId(subjectId);

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
                List<ChapterDomain> chapterDomainLists = chapterRepo.findSubjectChapterDomainsBySubjectId(subjectDomain.getId());

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
}

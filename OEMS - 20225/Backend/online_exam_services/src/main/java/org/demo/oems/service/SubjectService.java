package org.demo.oems.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.SubjectChapterDomain;
import org.demo.oems.domain.SubjectDomain;
import org.demo.oems.payload.request.CreateSubjectChapter;
import org.demo.oems.payload.request.CreateSubjectInfoRequest;
import org.demo.oems.payload.response.GetChaptersBySubjectResponse;
import org.demo.oems.repository.SubjectChapterRepo;
import org.demo.oems.repository.SubjectRepo;
import org.demo.oems.utils.ResponseUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    private final Logger logger = LogManager.getLogger(SubjectService.class);

    private final SubjectRepo subjectRepo;

    private final SubjectChapterRepo chapterRepo;

    public SubjectService(SubjectRepo subjectRepo, SubjectChapterRepo chapterRepo) {
        this.subjectRepo = subjectRepo;
        this.chapterRepo = chapterRepo;
    }

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
            apiResponse = ResponseUtils.responseFormatUtils("0", "success");
            logger.debug("final service response :: {}", apiResponse);
            return apiResponse;
        }catch (Exception e){
            logger.error("Exception while trying to added new subject :: {}", e.getMessage());
            apiResponse = ResponseUtils.responseFormatUtils("1", e.getMessage());
            return apiResponse;
        }
    }

    public JSONObject addChaptersToSubject(List<CreateSubjectChapter> chapterLists){
        JSONObject apiResponse = new JSONObject();
        try{
            logger.debug("Add Chapters To Subject Services :: {}", chapterLists);
            int listSize = chapterLists.size();

            if (listSize == 0){
                apiResponse = ResponseUtils.responseFormatUtils("1", "No Record to insert");
                return apiResponse;
            }

            int insertSize = 0;

            for (int i = 0; i < listSize; i++){
                CreateSubjectChapter chapterDomain = chapterLists.get(i);

                long subjectId = chapterDomain.getSubjectId();

                boolean isSubjectExist = checkIfSubjectExists(subjectId);
                logger.debug("Is Subject Exist :: {}", isSubjectExist);

                if(!isSubjectExist){
                    apiResponse = ResponseUtils.responseFormatUtils("1", "Please Create Subject Info First");
                    return apiResponse;
                }

                SubjectChapterDomain newChapterDomain = new SubjectChapterDomain();
                newChapterDomain.setChapter(chapterDomain.getChapter());
                newChapterDomain.setSubjectId(chapterDomain.getSubjectId());
                chapterRepo.save(newChapterDomain);

                insertSize++;
                logger.debug("Successfully Insert records :: {}", i + 1);
            }

            String responseMessage = "Successfully Insert " + insertSize + " records";
            apiResponse = ResponseUtils.responseFormatUtils("0", responseMessage);
            logger.debug("final service response :: {}", apiResponse);
            return apiResponse;
        }catch (Exception e){

            logger.error("Exception while trying to added new subject :: {}", e.getMessage());
            apiResponse = ResponseUtils.responseFormatUtils("1", e.getMessage());
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
                apiResponse = ResponseUtils.responseFormatUtils("1", "Please Create Subject Info First");
                return apiResponse;
            }

            SubjectDomain subjectInfo = subjectInfoOpt.get();

            List<SubjectChapterDomain> subjectChaptersList = chapterRepo.findSubjectChapterDomainsBySubjectId(subjectId);

            apiResponse = ResponseUtils.responseFormatUtils("0", "Success");

            GetChaptersBySubjectResponse dataResponse = new GetChaptersBySubjectResponse();
            dataResponse.setSubjectId(subjectInfo.getId());
            dataResponse.setSubjectName(subjectInfo.getSubjectName());
            dataResponse.setSubjectChapterDomainList(subjectChaptersList);

            apiResponse.put("data", dataResponse);

            return apiResponse;
        }catch (Exception e){
            logger.error("Exception while trying to added new subject :: {}", e.getMessage());
            apiResponse = ResponseUtils.responseFormatUtils("1", e.getMessage());
            return apiResponse;
        }
    }



}

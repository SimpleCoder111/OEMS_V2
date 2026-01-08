package org.demo.oems.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.SubjectDomain;
import org.demo.oems.payload.request.AddSubjectAndChapterRequest;
import org.demo.oems.repository.SubjectRepo;
import org.demo.oems.utils.ResponseUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    private final Logger logger = LogManager.getLogger(SubjectService.class);

    private final SubjectRepo subjectRepo;

    public SubjectService(SubjectRepo subjectRepo) {
        this.subjectRepo = subjectRepo;
    }

    public List<SubjectDomain> getSubjectList(){
        return subjectRepo.findAll();
    }

    public JSONObject addNewSubject(AddSubjectAndChapterRequest request){
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
}

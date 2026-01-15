package org.demo.oems.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.ClassDomain;
import org.demo.oems.payload.request.CreateClassInfoRequest;
import org.demo.oems.repository.ClassRepo;
import org.demo.oems.utils.DateUtils;
import org.demo.oems.utils.ResponseUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClassroomService {

    private final ClassRepo classRepo;

    private static final Logger logger = LogManager.getLogger(ClassroomService.class);

    public ClassroomService(ClassRepo classRepo) {
        this.classRepo = classRepo;
    }

    public JSONObject createClassInfo(CreateClassInfoRequest request) {
        JSONObject finalResponse = new JSONObject();
        try{
            logger.debug("Start Create Class Info Service :: {}", request);
            ClassDomain newClassInfo = new ClassDomain();
            newClassInfo.setClassName(request.getClassName());
            newClassInfo.setTeacherId(request.getTeacherId());
            newClassInfo.setClassYear(request.getClassYear());

            LocalDateTime classStart = DateUtils.formatDate(request.getClassStart());
            LocalDateTime classEnd = DateUtils.formatDate(request.getClassEnd());

            newClassInfo.setClassStart(classStart);
            newClassInfo.setClassEnd(classEnd);

            String classStatus = DateUtils.getClassStatus(classStart, classEnd);
            logger.debug("Class Status :: {}", classStatus);


            classRepo.save(newClassInfo);
            finalResponse = ResponseUtils.responseFormatUtils("0", "success");
            logger.debug("Successfully Insert Class Info to DB");
        }catch (Exception e){
            logger.error("Exception while add question banks :: {}" , e.getMessage() );
            finalResponse = ResponseUtils.responseFormatUtils("1", e.getMessage());
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
}

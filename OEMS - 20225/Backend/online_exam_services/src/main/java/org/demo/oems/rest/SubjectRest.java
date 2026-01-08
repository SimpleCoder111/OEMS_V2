package org.demo.oems.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.SubjectDomain;
import org.demo.oems.payload.request.AddSubjectAndChapterRequest;
import org.demo.oems.service.SubjectService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SubjectRest {

    private final Logger logger = LogManager.getLogger(SubjectRest.class);

    private final SubjectService subjectService;

    public SubjectRest(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping("/getAllSubject")
    public ResponseEntity<?> getAllSubject(){
        try{
            logger.info("Start Get All Subject Rest");
            List<SubjectDomain> subjectList = subjectService.getSubjectList();
            return new ResponseEntity<>(subjectList, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception happen while retrieving question banks :: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    @PostMapping("/addSubjectAndChapter")
    public ResponseEntity<?> addNewSubject(@RequestBody AddSubjectAndChapterRequest request){
        try{
            logger.info("Start Add Subject Rest");
            JSONObject response = subjectService.addNewSubject(request);
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception happen while retrieving question banks :: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }
    




}

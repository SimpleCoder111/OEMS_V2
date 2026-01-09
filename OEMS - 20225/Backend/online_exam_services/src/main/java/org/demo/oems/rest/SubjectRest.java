package org.demo.oems.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.SubjectDomain;
import org.demo.oems.payload.request.CreateSubjectChapter;
import org.demo.oems.payload.request.CreateSubjectInfoRequest;
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

    @PostMapping("/addSubjectInfo")
    public ResponseEntity<?> addNewSubject(@RequestBody CreateSubjectInfoRequest request){
        try{
            logger.info("Start Add Subject Rest");
            JSONObject response = subjectService.addNewSubject(request);
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception happen while retrieving question banks :: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    @PostMapping("createSubjectChapters")
    public ResponseEntity<?> addChaptersToSubject(@RequestBody List<CreateSubjectChapter> request){
        try{
            logger.info("Start Add Subject Rest");
            JSONObject response = subjectService.addChaptersToSubject(request);
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception happen while retrieving question banks :: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    @GetMapping("getChaptersBySubjectId/{subjectId}")
    public ResponseEntity<?> addChaptersToSubject(@PathVariable long subjectId){
        try{
            logger.info("Get All Chapters By Subject ID Rest :: {}", subjectId);
            JSONObject response = subjectService.getChaptersBySubject(subjectId);
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception happen while retrieving question banks :: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }



}

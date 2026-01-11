package org.demo.oems.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.ClassDomain;
import org.demo.oems.payload.request.CreateClassInfoRequest;
import org.demo.oems.service.ClassroomService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class ClassroomRest {
    private final Logger logger = LogManager.getLogger(ClassroomRest.class);
    private final ClassroomService classroomService;

    public ClassroomRest(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @PostMapping("/createClassInfo")
    public ResponseEntity<?> createClassInfo(@RequestBody CreateClassInfoRequest request){
        try{
            logger.info("Start Create Class Info Request");
            JSONObject finalResponse = classroomService.createClassInfo(request);
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception Happen While Create Class Info {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }

    @GetMapping("/getAllClassesInfo")
    public ResponseEntity<?> getAllClassesInfo(){
        try{
            logger.info("Start Create Class Info Request");
            List<ClassDomain> classDomainList = classroomService.getAllClassesInfo();
            return new ResponseEntity<>(classDomainList, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception Happen While Get All Classes Info {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(500));
        }
    }






}

package org.demo.oems.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.ClassDomain;
import org.demo.oems.payload.response.ClassListsResponse;
import org.demo.oems.payload.response.EnrollmentsResponse;
import org.demo.oems.payload.response.StudentListResponse;
import org.demo.oems.payload.response.TeacherListResponse;
import org.demo.oems.payload.request.CreateNewClassRequest;
import org.demo.oems.payload.request.CreateClassInfoRequest;
import org.demo.oems.service.ClassroomService;
import org.demo.oems.utils.CommonConstant;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/v1/classroom")
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

    /*
        Purpose: Load all classes on page load
        Query params: Optional filters (status, year, search)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/classes")
    public List<ClassListsResponse> getAllClasses(){
        List<ClassListsResponse> classListsResponses = new ArrayList<>();
        return classListsResponses;
    }

    /*
    Purpose: Populate teacher dropdown in create/edit dialogs
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/teachers")
    public List<TeacherListResponse> getAllTeacherLists(){
        List<TeacherListResponse> teacherListResponses = new ArrayList<>();

        return teacherListResponses;
    }

    /*
    Purpose: Populate student list in enrollment dialog
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/students")
    public List<StudentListResponse> getAllStudentLists(){
        List<StudentListResponse> studentListResponses = new ArrayList<>();

        return studentListResponses;
    }

    /*
    Purpose: Get all students enrolled in a specific class
     */
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/classes/{classId}/enrollments")
    public List<EnrollmentsResponse> getAllStudentEnrolledInClass(@PathVariable long classId){
        List<EnrollmentsResponse> enrollmentLists = new ArrayList<>();

        return enrollmentLists;
    }

    /*
    Purpose: Create a new class
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/classes")
    public String createNewClass(@RequestBody CreateNewClassRequest createNewClassRequest){
        return "OK";
    }

    /*
    Purpose: Edit class details
    */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/classes/{classId}")
    public String updateClassInfo(@PathVariable long classId, @RequestBody CreateNewClassRequest createNewClassRequest){
        return "OK";
    }

    /*
    Purpose: Delete class details
    */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/classes/{classId}")
    public String deleteClass(@PathVariable long classId){
        return "OK";
    }

    /*
    Purpose: Manage student enrollment (replaces all enrollments for the class)
   */
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @PutMapping("/classes/{classId}/enrollments")
    public ResponseEntity<List<EnrollmentsResponse>> updateClassEnrollments(@RequestBody long classId){
        List<EnrollmentsResponse> enrollmentsResponses = new ArrayList<>();
        try {


            return ResponseEntity.ok(enrollmentsResponses);
        }catch (Exception e){
            logger.error(CommonConstant.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Update Class Enrollment", e.getMessage());
            return ResponseEntity.internalServerError().body(enrollmentsResponses);
        }
    }










}

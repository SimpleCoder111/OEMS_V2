package org.demo.oems.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.payload.request.JoinClassRequest;
import org.demo.oems.payload.request.UpdateEnrollmentStatusRequest;
import org.demo.oems.payload.response.*;
import org.demo.oems.payload.request.CreateNewClassRequest;
import org.demo.oems.service.ClassroomService;
import org.demo.oems.utils.CommonConstantUtils;
import org.demo.oems.utils.ResponseUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/classes")
public class ClassroomRest {
    private final Logger logger = LogManager.getLogger(ClassroomRest.class);
    private final ClassroomService classroomService;

    public ClassroomRest(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }


    @Operation(summary = "Admin Classes Service - Create Classes Info", description = "Create Classes Info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("")
    public ResponseEntity<Map<String, Object>> createNewClass(@RequestBody CreateNewClassRequest createNewClassRequest) {
        try {
            Map<String, Object> finalResponse = classroomService.createNewClasses(createNewClassRequest);
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "createNewClass", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Classes Service - Get All Classes Info", description = "Get All Classes Info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllClassesInfo() {
        try {
            logger.debug("Get All Classes Info");
            Map<String, Object> getAllClassesInfoResponse = classroomService.getAllClassesInfo();
            logger.debug("Final API Response :: {}", getAllClassesInfoResponse);
            return new ResponseEntity<>(getAllClassesInfoResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getAllClassesInfo", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Classes Service - Edit Classes Info", description = "Edit Classes Info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping("/{classId}")
    public ResponseEntity<Map<String, Object>> editClassInfo(@PathVariable long classId, @RequestBody CreateNewClassRequest createNewClassRequest) {
        try {
            logger.debug("Edit Classes Info");
            Map<String, Object> editClassInfoResponse = classroomService.editClassInfo(classId, createNewClassRequest);
            logger.debug("Final API Response :: {}", editClassInfoResponse);
            return new ResponseEntity<>(editClassInfoResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "editClassInfo", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Classes Service - Get Classes Dashboard", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getClassesDashboard() {
        try {
            logger.debug("Get Classes Dashboard");
            Map<String, Object> getClassesDashboardResponse = classroomService.getClassesDashboard();
            logger.debug("Final API Response :: {}", getClassesDashboardResponse);
            return new ResponseEntity<>(getClassesDashboardResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getAllClassesInfo", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Classes Service - Get Teacher Lists for Classes", description = "Response a filters lists of teacher to assign to class")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/teachers")
    public ResponseEntity<Map<String, Object>> getAllTeacherLists() {
        try {
            logger.debug("Get Teacher Lists");
            Map<String, Object> teacherListsResponse = classroomService.getTeacherLists();
            logger.debug("Final API Response :: {}", teacherListsResponse);
            return new ResponseEntity<>(teacherListsResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getAllClassesInfo", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Classes Service - Get Student Lists for Classes", description = "Response a filters lists of student to assign to class")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/students")
    public ResponseEntity<Map<String, Object>> getAllStudentLists() {
        try {
            logger.debug("Get Student Lists");
            Map<String, Object> studentListsResponse = classroomService.getStudentLists();
            logger.debug("Final API Response :: {}", studentListsResponse);
            return new ResponseEntity<>(studentListsResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getAllClassesInfo", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    /*
    Purpose: Get all students enrolled in a specific class
     */
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/{classId}/enrollments")
    public List<EnrollmentsResponse> getAllStudentEnrolledInClass(@PathVariable long classId) {
        List<EnrollmentsResponse> enrollmentLists = new ArrayList<>();
        return enrollmentLists;
    }

    /*
    Purpose: Manage student enrollment (replaces all enrollments for the class)
   */
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @PutMapping("/{classId}/enrollments")
    public ResponseEntity<List<EnrollmentsResponse>> updateClassEnrollments(@RequestBody long classId) {
        List<EnrollmentsResponse> enrollmentsResponses = new ArrayList<>();
        try {
            return ResponseEntity.ok(enrollmentsResponses);
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Update Class Enrollment", e.getMessage());
            return ResponseEntity.internalServerError().body(enrollmentsResponses);
        }
    }

    @Operation(summary = "Teacher Classes Service - Get All Classes Info", description = "Get All Classes Info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/{teacherId}")
    public ResponseEntity<Map<String, Object>> getAllClassesInfoForTeacher(@PathVariable String teacherId) {
        try {
            logger.debug("Get All Classes Info");
            Map<String, Object> getAllClassesInfoResponse = classroomService.getAllClassesInfoByTeacherId(teacherId);
            logger.debug("Final API Response :: {}", getAllClassesInfoResponse);
            return new ResponseEntity<>(getAllClassesInfoResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getAllClassesInfo", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher Classes Service - Generate Invite QR Code", description = "Generate Invite QR Code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/{classId}/generate-qr")
    public ResponseEntity<Map<String, Object>> generateQrForClass(@PathVariable Long classId) {
        try {
            logger.debug("Generate QR for Class");
            Map<String, Object> generateQRResponse = classroomService.generateQRforClass(classId);
            logger.debug("Final API Response :: {}", generateQRResponse);
            return new ResponseEntity<>(generateQRResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getAllClassesInfo", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Student Classes Service - Request Join Class Service", description = "Join Class via Scan or Enter Class Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> requestJoinClass(@RequestBody JoinClassRequest joinClassRequest) {
        try {
            logger.debug("Generate QR for Class :: {}", joinClassRequest);
            Map<String, Object> requestJoinClassResponse = classroomService.requestJoinClass(joinClassRequest.getStudentId(), joinClassRequest.getToken());
            logger.debug("Final API Response :: {}", requestJoinClassResponse);
            return new ResponseEntity<>(requestJoinClassResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getAllClassesInfo", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Teacher Classes Service - Get Pending Class Enrollment Lists", description = "Join Class via Scan or Enter Class Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/enrollment/pending")
    public ResponseEntity<Map<String, Object>> getPendingEnrollmentsList(@RequestParam long classId) {
        try {
            logger.debug("Generate QR for Class");
            Map<String, Object> getPendingEnrollmentsListResponse = classroomService.getPendingEnrollmentsList(classId);
            logger.debug("Final API Response :: {}", getPendingEnrollmentsListResponse);
            return new ResponseEntity<>(getPendingEnrollmentsListResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getAllClassesInfo", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }


    @Operation(summary = "Teacher Classes Service - Approve or Reject Student Join Class Request", description = "Approve or Reject Student Join Class Request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping("/enrollment/update_status")
    public ResponseEntity<Map<String, Object>> approveOrRejectStudentEnrollment(@RequestBody UpdateEnrollmentStatusRequest updateEnrollmentStatusRequest) {
        try{
            logger.debug("Update Status Class Enrollment Id :: {}", updateEnrollmentStatusRequest);
            Map<String, Object> getPendingEnrollmentsListResponse = classroomService.approveOrRejectStudentEnrollment(updateEnrollmentStatusRequest);
            logger.debug("Final API Response :: {}", getPendingEnrollmentsListResponse);
            return new ResponseEntity<>(getPendingEnrollmentsListResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getAllClassesInfo", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Student Services :: Get All Classes By Student ID", description = "Generate Invite QR Code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/student")
    public ResponseEntity<Map<String, Object>> getAllClassesByStudentId(@RequestParam String studentId) {
        try {
            logger.debug("Start - getAllClassesByStudentId API {}", studentId);
            Map<String, Object> getAllClassesByStudentIdResponse = classroomService.getAllClassesByStudentId(studentId);
            logger.debug("Final API Response :: {}", getAllClassesByStudentIdResponse);
            return new ResponseEntity<>(getAllClassesByStudentIdResponse, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "getAllClassesByStudentId", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }
}

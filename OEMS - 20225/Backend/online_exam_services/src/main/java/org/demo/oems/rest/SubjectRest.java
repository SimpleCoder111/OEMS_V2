package org.demo.oems.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.payload.request.*;
import org.demo.oems.payload.response.SubjectResponse;
import org.demo.oems.service.SubjectService;
import org.demo.oems.utils.CommonConstantUtils;
import org.demo.oems.utils.ResponseUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class SubjectRest {

    private final Logger logger = LogManager.getLogger(SubjectRest.class);

    private final SubjectService subjectService;

    public SubjectRest(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping("/admin/subjects")
    @Operation(summary = "Admin Subject Service - Get All Subjects Info", description = "Get All Subject Information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<String, Object>> getAllSubjects(){
        try{
            List<SubjectResponse> subjectResponseList = subjectService.getAllSubjects();
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("0", "success", subjectResponseList);
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error("Exception happen while retrieving question banks :: {}", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @PostMapping("admin/subject")
    @Operation(summary = "Admin Subject Service - Create Subject Info", description = "Create New Subject Info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    //Admin Service
    public ResponseEntity<Map<String, Object>> createNewSubject(@RequestBody CreateSubjectRequest createSubjectRequest){
        try{
            Map<String, Object> createSubjectResponse = subjectService.createNewSubject(createSubjectRequest);
            logger.debug("Final API Response :: {}", createSubjectResponse);
            return new ResponseEntity<>(createSubjectResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER,"Create New Subject", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @PutMapping("/admin/subject/{subjectId}")
    @Operation(summary = "Admin Subject Service - Update Subject Info", description = "Update Subject Info by Subject ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public  ResponseEntity<Map<String, Object>> updateSubjectInfo(@PathVariable long subjectId, @RequestBody CreateSubjectRequest createSubjectRequest){
        try{
            Map<String, Object> createSubjectResponse = subjectService.editSubjectInfo(subjectId, createSubjectRequest);
            logger.debug("Final API Response :: {}", createSubjectResponse);
            return new ResponseEntity<>(createSubjectResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER,"Update New Subject", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @PutMapping("/admin/subject/{subjectId}/status")
    @Operation(summary = "Admin Subject Service - Toggle Subject Status", description = "Update Subject Status to ACTIVE OR INACTIVE by Subject ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<String, Object>> updateSubjectStatus(@PathVariable long subjectId, @RequestParam Boolean isActive){
        try{
            Map<String, Object> updateSubjectStatusResponse = subjectService.updateSubjectStatus(subjectId, isActive);
            logger.debug("Final API Response :: {}", updateSubjectStatusResponse);
            return new ResponseEntity<>(updateSubjectStatusResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER,"Toggle Subject Status", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }


    @Operation(summary = "Admin Subject Service - Delete Subject Info", description = "Delete Subject, Chapters, Questions Related to Subject ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping("/admin/subject/{subjectId}")
    public ResponseEntity<Map<String, Object>> deleteSubjectInfo(@PathVariable long subjectId){
        try{
            Map<String, Object> deleteSubjectResponse = subjectService.deleteSubjectAndChapterRelated(subjectId);
            logger.debug("Final API Response :: {}", deleteSubjectResponse);
            return new ResponseEntity<>(deleteSubjectResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER,"Delete Subject Info", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @PostMapping("/admin/subject/{subjectId}/chapters")
    @Operation(summary = "Admin Subject Service - Create Chapter Info", description = "Insert Multiple Lists of Chapter to Subject with Subject ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<String, Object>> createChapterInfo(@PathVariable long subjectId, @RequestBody List<CreateChapterRequest> createChapterListRequest){
        try{
            Map<String, Object> addChaptersListsResponse = subjectService.insertNewChaptersForSubject(subjectId, createChapterListRequest);
            logger.debug("Final API Response :: {}", addChaptersListsResponse);
            return new ResponseEntity<>(addChaptersListsResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER,"Delete Subject Info", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @PutMapping("/admin/subject/chapters/{chapterId}")
    @Operation(summary = "Admin Subject Service - Update Chapter Info", description = "Update Chapter Info By Chapter ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<String, Object>> updateChapterInfo(@PathVariable long chapterId, @RequestBody CreateChapterRequest createChapterRequest){
        try{
            Map<String, Object> addChaptersListsResponse = subjectService.editChapterInfo(chapterId, createChapterRequest);
            logger.debug("Final API Response :: {}", addChaptersListsResponse);
            return new ResponseEntity<>(addChaptersListsResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER,"Edit Chapter Info", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @PutMapping("/admin/subject/chapters/{chapterId}/status")
    @Operation(summary = "Admin Subject Service - Toggle Chapter Status", description = "Update Chapter Status to ACTIVE or INACTIVE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<String, Object>> updateChapterStatus(@PathVariable long chapterId, @RequestParam Boolean isActive){
        try{
            Map<String, Object> updateChapterStatus = subjectService.updateChapterStatus(chapterId, isActive);
            logger.debug("Final API Response :: {}", updateChapterStatus);
            return new ResponseEntity<>(updateChapterStatus, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER,"updateChapterStatus", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @DeleteMapping("/admin/subject/chapters/{chapterId}")
    @Operation(summary = "Admin Subject Service - Delete Chapter Info", description = "Delete chapter and questions related to chapter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<String, Object>> deleteChapterInfo(@PathVariable long chapterId){
        try{
            Map<String, Object> deleteChapterResponse = subjectService.deleteChapterAndQuestionRelated(chapterId);
            logger.debug("Final API Response :: {}", deleteChapterResponse);
            return new ResponseEntity<>(deleteChapterResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER,"deleteChapterInfo", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @Operation(summary = "Admin Subject Service - Reorder Chapter", description = "Return a new order of chapter by subject id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping("/admin/subject/{subjectId}/chapters/reorder")
    public ResponseEntity<Map<String, Object>> updateChapterOrderIndices(@PathVariable long subjectId, @RequestBody List<OrderChapterRequest> orderChapterRequestList){
        try{
            Map<String, Object> orderChapterIndexResponse = subjectService.orderChapterIndex(subjectId, orderChapterRequestList);
            logger.debug("Final API Response :: {}", orderChapterIndexResponse);
            return new ResponseEntity<>(orderChapterIndexResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER,"updateChapterOrderIndices", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @GetMapping("/admin/subject/dashboard")
    @Operation(summary = "Admin - Subject Service - Get subject summary", description = "Return count of activate subject, total subject, total chapter, total questions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Map<String, Object>> getSubjectSummaryDashboard(){
        try{
            Map<String, Object> subjectSummaryDashboardResponse = subjectService.getSubjectSummaryDashboard();
            logger.debug("Final API Response :: {}", subjectSummaryDashboardResponse);
            return new ResponseEntity<>(subjectSummaryDashboardResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER,"getSubjectSummaryDashboard", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }

    @GetMapping("/teacher/subject/{teacherId}")
    public ResponseEntity<Map<String, Object>> getAllSubjectsByTeacherId(@PathVariable String teacherId){
        try {
            List<SubjectResponse> subjectResponseList = subjectService.getAllSubjectsByTeacherId(teacherId);
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("0", "success", subjectResponseList);
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(200));
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_CONTROLLER, "Update Class Enrollment", e.getMessage());
            Map<String, Object> finalResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return new ResponseEntity<>(finalResponse, HttpStatusCode.valueOf(500));
        }
    }




}

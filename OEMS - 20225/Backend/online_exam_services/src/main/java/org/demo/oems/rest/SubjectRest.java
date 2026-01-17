package org.demo.oems.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.oems.domain.SubjectDomain;
import org.demo.oems.payload.request.*;
import org.demo.oems.payload.response.CreateChapterResponse;
import org.demo.oems.payload.response.SubjectResponse;
import org.demo.oems.service.SubjectService;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
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

    /*
    Purpose: 1. Load all subjects with their chapters on page load
    Query params: Optional search filter
     */
    @GetMapping("")
    public List<SubjectResponse> getAllSubjects(){
        List<SubjectResponse> subjectResponseList = new ArrayList<>();

        return subjectResponseList;
    }

    /*
    Purpose: 2. Add a new subject
     */
    @PostMapping("")
    public String getAllSubjects(@RequestBody CreateSubjectRequest createSubjectRequest){

        return "OK";
    }

    /*
    Purpose: 3. Edit subject details
    */
    @PutMapping("/{subjectId}")
    public String updateSubjectInfo(@PathVariable long subjectId, @RequestBody CreateSubjectRequest createSubjectRequest){

        return "OK";
    }

    /*
   Purpose: 4. Activate or deactivate a subject
   */
    @PutMapping("/{subjectId}/status")
    public String updateSubjectStatus(@PathVariable long subjectId, @RequestBody UpdateSubjectStatusRequest updateSubjectStatus){
        return "OK";
    }

    /*
    Purpose: 5. Activate or deactivate a subject
    */
    @DeleteMapping("/{subjectId}")
    public String deleteSubjectInfo(@PathVariable long subjectId){
        return "OK";
    }

    /*
    Purpose: 6. Add a new chapter to a subject
    */
    @PostMapping("/{subjectId}/chapters")
    public CreateChapterResponse createChapterInfo(@PathVariable long subjectId, @RequestBody CreateChapterRequest createChapterRequest){
        CreateChapterResponse chapterResponse = new CreateChapterResponse();
        return chapterResponse;
    }

    /*
    Purpose: 7. Update Chapter
    */
    @PostMapping("/chapters/{chapterId}")
    public String updateChapterInfo(@PathVariable long chapterId, @RequestBody CreateChapterRequest createChapterRequest){
        return "OK";
    }

    /*
    Purpose: 8. Toggle Chapter Status
    */
    @PostMapping("/chapters/{chapterId}/status")
    public String updateChapterInfo(@PathVariable long chapterId, @RequestBody ToggleChapterStatusRequest toggleChapterStatusRequest){
        return "OK";
    }

    /*
    Purpose: Delete a chapter from a subject
    */
    @DeleteMapping("/chapters/{chapterId}")
    public String deleteChapterInfo(@PathVariable long chapterId){
        return "OK";
    }

    /*
    Purpose: Update chapter order indices
    */
    @PostMapping("/{subjectId}/chapters/reorder")
    public CreateChapterResponse updateChapterOrderIndices(@PathVariable long subjectId, @RequestBody List<OrderChapterRequest> orderChapterRequestList){
        CreateChapterResponse chapterResponse = new CreateChapterResponse();
        return chapterResponse;
    }


}

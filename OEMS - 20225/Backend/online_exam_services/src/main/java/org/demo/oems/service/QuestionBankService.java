package org.demo.oems.service;

import jakarta.persistence.*;
import org.apache.coyote.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.demo.oems.domain.OptionBankDomain;
import org.demo.oems.domain.QuestionBankDomain;
import org.demo.oems.domain.ChapterDomain;
import org.demo.oems.domain.SubjectDomain;
import org.demo.oems.payload.request.OptionBankInsertRequest;
import org.demo.oems.payload.request.QuestionBankInsertRequest;
import org.demo.oems.payload.response.OptionListResponse;
import org.demo.oems.payload.response.QuestionBankListsResponse;
import org.demo.oems.payload.response.QuestionImportResponse;
import org.demo.oems.repository.OptionBankRepo;
import org.demo.oems.repository.QuestionBankRepo;
import org.demo.oems.repository.SubjectChapterRepo;
import org.demo.oems.repository.SubjectRepo;
import org.demo.oems.utils.CommonConstantUtils;
import org.demo.oems.utils.ResponseUtils;
import org.hibernate.type.descriptor.java.ObjectJavaType;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuestionBankService {
    private static final Logger logger = LogManager.getLogger(QuestionBankService.class);
    private final QuestionBankRepo questionBankRepo;
    private final OptionBankRepo optionBankRepo;

    private final SubjectRepo subjectRepo;
    private final SubjectChapterRepo chapterRepo;

    public QuestionBankService(QuestionBankRepo questionBankRepo,
                               OptionBankRepo optionBankRepo, SubjectRepo subjectRepo, SubjectChapterRepo chapterRepo) {
        this.questionBankRepo = questionBankRepo;
        this.optionBankRepo = optionBankRepo;
        this.subjectRepo = subjectRepo;
        this.chapterRepo = chapterRepo;
    }

    public Map<String, Object> getAllQuestionBanksBySubject(long subjectId){
        logger.debug("Get All Question Banks By Subject Services Start");

        Map<String, Object> finalServiceResponse = new HashMap<>();
        Map<String, Object> finalResponse = new JSONObject();
        List<QuestionBankListsResponse> questionListsResponse = new ArrayList<>();

        try {

            logger.debug("Step 1: Find subject Info subject ID ::  {}", subjectId);
            Optional<SubjectDomain> subjectDomainOptional = subjectRepo.getSubjectDomainsById(subjectId);

            SubjectDomain subjectInfo = new SubjectDomain();

            if(subjectDomainOptional.isPresent()) {
                subjectInfo = subjectDomainOptional.get();
            }

            logger.debug("Step 2: Find All Questions related to subject ID ::  {}", subjectId);
            List<QuestionBankDomain> questionBankDomainList = questionBankRepo.getQuestionBankDomainsBySubjectId(subjectId);

            logger.debug("Step 3: Loop Through Questions Lists");
            for (QuestionBankDomain questionBankDomain : questionBankDomainList) {
                QuestionBankListsResponse questionResponse = new QuestionBankListsResponse();

                questionResponse.setQuestionType(String.valueOf(questionBankDomain.getQuestionType()));
                questionResponse.setQuestionId(questionBankDomain.getId());
                questionResponse.setQuestionContent(questionBankDomain.getQuestionContent());
                questionResponse.setDifficulty(String.valueOf(questionBankDomain.getDifficulty()));
                questionResponse.setCreatedBy(questionBankDomain.getCreatedBy());

                Optional<ChapterDomain> chapterDomainOptional = chapterRepo.findSubjectChapterDomainById(questionBankDomain.getChapter().getId());
                if(chapterDomainOptional.isPresent()){
                    ChapterDomain chapterDomain = chapterDomainOptional.get();
                    questionResponse.setChapterId(chapterDomain.getId());
                    questionResponse.setChapter(chapterDomain.getChapter());
                }

                List<OptionBankDomain> optionBankLists = optionBankRepo.getOptionBankDomainsByQuestionId(questionBankDomain.getId());

                List<OptionListResponse> optionResponseLists = getOptionListResponses(optionBankLists);
                questionResponse.setOptionLists(optionResponseLists);

                questionListsResponse.add(questionResponse);

            }
            finalResponse.put("questionData", questionListsResponse);
            finalResponse.put("subjectId", subjectId);
            finalResponse.put("subjectName", subjectInfo.getSubjectName());

            finalServiceResponse = ResponseUtils.formatAPIResponse("0", "Success", finalResponse);

        }catch (Exception e) {
            logger.error("Exception Get All Question Banks By Subject Services :: {}", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
        }

        logger.debug("Final Service Response :: {}", finalServiceResponse);
        return finalServiceResponse;
    }

    public static List<OptionListResponse> getOptionListResponses(List<OptionBankDomain> optionBankLists) {
        List<OptionListResponse> optionResponseLists = new ArrayList<>();


        for (OptionBankDomain optionBankDomain : optionBankLists) {

            OptionListResponse optionResponse = new OptionListResponse();

            optionResponse.setOptionText(optionBankDomain.getOptionText());
            optionResponse.setOptionId(optionBankDomain.getId());
            optionResponse.setIsCorrect(optionBankDomain.getIsCorrect());

            optionResponseLists.add(optionResponse);
        }
        return optionResponseLists;
    }


    @Transactional
    public Map<String, Object> addQuestionBanks(long subjectId, QuestionBankInsertRequest requestPayload){
        Map<String, Object> finalServiceResponse = new JSONObject();
        try{
            Optional<SubjectDomain> subjectDomainOptional = subjectRepo.findById(subjectId);

            if(subjectDomainOptional.isEmpty()){
                logger.error("Subject is not found for ID :: {}", subjectId);
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Subject is not found", "");
                return finalServiceResponse;
            }

            //Step 2: Save Data to Question Domain
            QuestionBankDomain newQuestionBank = new QuestionBankDomain();
            newQuestionBank.setSubject(subjectDomainOptional.get());
            newQuestionBank.setQuestionType(QuestionBankDomain.QuestionType.valueOf(requestPayload.getQuestionType()));
            newQuestionBank.setQuestionContent(requestPayload.getQuestionContent());
            newQuestionBank.setDifficulty(QuestionBankDomain.Difficulty.valueOf(requestPayload.getDifficulty()));
            newQuestionBank.setCreatedBy(requestPayload.getCreatedBy());
            newQuestionBank.setCreatedAt(LocalDateTime.now());

            Optional<ChapterDomain> chapterDomainOptional = chapterRepo.findById(requestPayload.getChapterId());

            if(chapterDomainOptional.isEmpty()){
                logger.error("Chapter is not found for ID :: {}", requestPayload.getChapterId());
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Chapter is not found", "");
                return finalServiceResponse;
            }

            newQuestionBank.setChapter(chapterDomainOptional.get());
            questionBankRepo.save(newQuestionBank);

            //Insert Option
            for(int i = 0; i < requestPayload.getOptionLists().size(); i++){
                OptionBankInsertRequest requestOptionBank = requestPayload.getOptionLists().get(i);

                OptionBankDomain newOptionBank = new OptionBankDomain();
                newOptionBank.setQuestionId(newQuestionBank.getId());
                newOptionBank.setOptionText(requestOptionBank.getOptionText());
                newOptionBank.setIsCorrect(requestOptionBank.getIsCorrect());

                optionBankRepo.save(newOptionBank);
            }

            finalServiceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Create New Question", newQuestionBank);
            logger.debug("Successfully create the Question :: {}", finalServiceResponse);
            return  finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception while create question banks :: {}" , e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return  finalServiceResponse;
        }
    }

    @Transactional
    public Map<String, Object> deleteQuestionById(Long questionId){
        Map<String, Object> finalServiceResponse;
        try{
            //Step 1: Delete All Option Related to Question
            logger.debug("Start Delete Questions By ID");
            List<OptionBankDomain> optionBankDomainList = optionBankRepo.getOptionBankDomainsByQuestionId(questionId);

            logger.debug("Step 1: Going to delete options related to the questions :: {}", optionBankDomainList.size());

            if(!optionBankDomainList.isEmpty()){
                optionBankRepo.deleteByQuestionId(questionId);
            }

            logger.debug("Step 2: Going to delete question ID :: {}", questionId);
            Optional<QuestionBankDomain> questionBankDomainOptional = questionBankRepo.findById(questionId);

            if(questionBankDomainOptional.isEmpty()){
                logger.debug("Could not find any records to deleted");
                finalServiceResponse = ResponseUtils.formatAPIResponse("0", "No Records to Delete", "");
                return finalServiceResponse;
            }

            questionBankRepo.deleteById(questionId);
            finalServiceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Deleted", "");
            logger.debug("Successfully Delete Question :: {}", finalServiceResponse);
            return  finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception while delete question banks :: {}" , e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return  finalServiceResponse;
        }
    }


    public JSONObject addQuestionBanksArray(List<QuestionBankInsertRequest> requestPayload){
        JSONObject addQuestionBankResponse = new JSONObject();
        String responseStatus;
        String responseMessage;
        try{
            logger.debug("Trying to insert Question Banks in Arrays");

            int arraySize = requestPayload.size();

            if(arraySize == 0){
                logger.debug("Array Size is 0");
                addQuestionBankResponse = ResponseUtils.formatServiceResponse("0", "No new records to insert");
                return addQuestionBankResponse;
            }

            int recordSaveCount = 0;

            for(int i =0; i < arraySize; i++){
                insertQuestionAndOptionBank(requestPayload, i);
                recordSaveCount ++;
            }

            responseStatus = "0";
            responseMessage = "Successfully Insert " + recordSaveCount + " records" ;
            addQuestionBankResponse = ResponseUtils.formatServiceResponse(responseStatus, responseMessage);

        }catch (Exception e){
            logger.error("Exception while add question banks :: {}" , e.getMessage());
            responseStatus = "1";
            responseMessage = e.getMessage();

            addQuestionBankResponse = ResponseUtils.formatServiceResponse(responseStatus, responseMessage);
        }

        return  addQuestionBankResponse;

    }

    public void insertQuestionAndOptionBank(List<QuestionBankInsertRequest> questionLists, int questionBankIndex) {
        try {
            QuestionBankInsertRequest questionBank = questionLists.get(questionBankIndex);

            QuestionBankDomain newQuestionBank = new QuestionBankDomain();
            newQuestionBank.setQuestionType(QuestionBankDomain.QuestionType.valueOf(questionBank.getQuestionType()));
            newQuestionBank.setQuestionContent(questionBank.getQuestionContent());
            newQuestionBank.setDifficulty(QuestionBankDomain.Difficulty.valueOf(questionBank.getDifficulty()));
            newQuestionBank.setCreatedBy(questionBank.getCreatedBy());

            Optional<SubjectDomain> subjectOpt = subjectRepo.findById(questionBank.getSubjectId());

            subjectOpt.ifPresent(newQuestionBank::setSubject);

            Optional<ChapterDomain> chapterOpt = chapterRepo.findById(questionBank.getChapterId());
            chapterOpt.ifPresent(newQuestionBank::setChapter);

            int optionListSize = questionBank.getOptionLists().size();

            questionBankRepo.save(newQuestionBank);
            logger.debug("Successfully Save Question Info :: {}", newQuestionBank.getId());

            for(int i = 0; i < optionListSize; i++){
                OptionBankInsertRequest optionBank = questionBank.getOptionLists().get(i);
                OptionBankDomain newOptionBank = new OptionBankDomain();
                newOptionBank.setOptionText(optionBank.getOptionText());
                newOptionBank.setIsCorrect(optionBank.getIsCorrect());
                newOptionBank.setQuestionId(newQuestionBank.getId());

                optionBankRepo.save(newOptionBank);
                logger.debug("Successfully Save Question Info :: {}", newOptionBank);
            }

            logger.debug("successfully save question and options banks");
        }catch (Exception e){
            logger.error("Exception while saving questions and option banks :: {}", e.getMessage());
        }
    }


    @Transactional
    public Map<String, Object> deleteQuestionAndOptionBank(Long questionId){
        Map<String, Object> finalServiceResponse = new HashMap<>();
        try{
            logger.debug("Start - deleteQuestionAndOptionBank with questionId :: {}", questionId);
            logger.debug("Step 1: Validate Question ID if exists");
            Optional<QuestionBankDomain> questionBankOptional = questionBankRepo.findById(questionId);

            if(questionBankOptional.isPresent()){
                logger.debug("Step 1: Question domain exists");
                QuestionBankDomain questionBank = questionBankOptional.get();

                logger.debug("Step 2: Delete all options related to the questions :: {}", questionId);
                long optionCount = optionBankRepo.countByQuestionId(questionBank.getId());
                logger.debug("Step 2: Going to delete {} options related to question ID :: {}", optionCount, questionId);
                optionBankRepo.deleteByQuestionId(questionBank.getId());

            }

            logger.debug("Step 3: Delete Question ID :: {}", questionId);
            questionBankRepo.deleteById(questionId);

            finalServiceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Deleted the Question", "");
            logger.debug("Final service response :: {}",  finalServiceResponse);
            return finalServiceResponse;
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "Delete Question and Option Bank", e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");

            return finalServiceResponse;
        }
    }


    @Transactional
    public QuestionImportResponse importQuestions(MultipartFile file, Long subjectId, String createdUserId) {
        List<String> errors = new ArrayList<>();
        int imported = 0;
        int rowNum = 0;  // Header is row 0

        // Validate subject exists early
        SubjectDomain subject = subjectRepo.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found with ID: " + subjectId));

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                rowNum++;
                if (rowNum == 1) continue;  // Skip header row

                try {
                    String questionText = getCellValue(row.getCell(0));
                    String typeStr = getCellValue(row.getCell(1)).toUpperCase();
                    String difficultyStr = getCellValue(row.getCell(2)).toUpperCase();
                    String chapterName = getCellValue(row.getCell(3));  // Column 4 = chapter (skip topic column 3)
                    String optionA = getCellValue(row.getCell(5));
                    String optionB = getCellValue(row.getCell(6));
                    String optionC = getCellValue(row.getCell(7));
                    String optionD = getCellValue(row.getCell(8));
                    String correctAnswer = getCellValue(row.getCell(9));

                    // Basic validation
                    if (questionText == null || questionText.isBlank()) {
                        errors.add("Row " + rowNum + ": Question text is required");
                        continue;
                    }
                    if (typeStr.isBlank()) {
                        errors.add("Row " + rowNum + ": Question type is required");
                        continue;
                    }
                    if (chapterName == null || chapterName.isBlank()) {
                        errors.add("Row " + rowNum + ": Chapter is required");
                        continue;
                    }

                    // Find or create chapter under the subject
                    ChapterDomain chapter = chapterRepo.findByChapterEqualsIgnoreCaseAndSubjectId(chapterName, subjectId)
                            .orElseGet(() -> {
                                ChapterDomain newChapter = new ChapterDomain();
                                newChapter.setSubject(subject);
                                newChapter.setChapter(chapterName);
                                newChapter.setChapterIndex(0);  // Or auto-generate
                                newChapter.setChapterStatus("active");
                                return chapterRepo.save(newChapter);
                            });

                    // Create question
                    QuestionBankDomain question = new QuestionBankDomain();
                    question.setQuestionContent(questionText);
                    question.setQuestionType(QuestionBankDomain.QuestionType.valueOf(typeStr));
                    question.setDifficulty(QuestionBankDomain.Difficulty.valueOf(difficultyStr));
                    question.setSubject(subject);
                    question.setChapter(chapter);
                    question.setCreatedBy(createdUserId);  // Or pass from auth
                    question.setCreatedAt(LocalDateTime.now());

                    // Save question first to get ID (needed for options)
                    question = questionBankRepo.save(question);

                    QuestionBankDomain.QuestionType type = question.getQuestionType();

                    // Handle options and correct answer based on type
                    // In importQuestions method (replace option handling)
                    if (type == QuestionBankDomain.QuestionType.MULTIPLE_CHOICE) {
                        OptionBankDomain optA = createOption(question, "A", optionA);
                        OptionBankDomain optB = createOption(question, "B", optionB);
                        OptionBankDomain optC = createOption(question, "C", optionC);
                        OptionBankDomain optD = createOption(question, "D", optionD);

                        // Set correct one
                        switch (correctAnswer.toUpperCase()) {
                            case "A" -> optA.setIsCorrect(true);
                            case "B" -> optB.setIsCorrect(true);
                            case "C" -> optC.setIsCorrect(true);
                            case "D" -> optD.setIsCorrect(true);
                            default -> throw new IllegalArgumentException("Invalid correct answer");
                        }

                    } else if (type == QuestionBankDomain.QuestionType.TRUE_FALSE) {
                        OptionBankDomain optTrue = createOption(question, "TRUE", "TRUE");
                        OptionBankDomain optFalse = createOption(question, "FALSE", "FALSE");

                        if ("TRUE".equalsIgnoreCase(correctAnswer)) {
                            optTrue.setIsCorrect(true);
                        } else if ("FALSE".equalsIgnoreCase(correctAnswer)) {
                            optFalse.setIsCorrect(true);
                        } else {
                            throw new IllegalArgumentException("Correct answer must be TRUE or FALSE");
                        }

                    } // Updated handling for FILL_BLANK – centralized in OptionBankDomain
                    else if (type == QuestionBankDomain.QuestionType.FILL_BLANK) {
                        if (correctAnswer == null || correctAnswer.trim().isBlank()) {
                            errors.add("Row " + rowNum + ": Correct answer is required for fill-in-blank");
                            questionBankRepo.delete(question);  // Rollback question
                            continue;
                        }

                        // Create single "answer" option – centralized like MCQ/TF
                        OptionBankDomain answerOption = new OptionBankDomain();
                        answerOption.setQuestionId(question.getId());
                        answerOption.setOptionLabel("ANSWER");  // Fixed label (or null if you prefer)
                        answerOption.setOptionText(correctAnswer.trim());
                        answerOption.setIsCorrect(true);

                        // No longer store in question.correctAnswer
                        // question.setCorrectAnswer(null);  // Optional: clear if field exists
                    }

                    // Final save (with correct answer)
                    questionBankRepo.save(question);
                    imported++;

                } catch (Exception e) {
                    errors.add("Row " + rowNum + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel file: " + e.getMessage(), e);
        }

        return new QuestionImportResponse(imported, errors.size(), errors);
    }

    // Updated createOption helper
    private OptionBankDomain createOption(QuestionBankDomain question, String label, String text) {
        if (text == null || text.isBlank()) return null;

        OptionBankDomain option = new OptionBankDomain();
        option.setQuestionId(question.getId());
        option.setOptionLabel(label);
        option.setOptionText(text);
        option.setIsCorrect(false);
        return optionBankRepo.save(option);  // Or collect for batch}
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                } else {
                    yield String.valueOf((int) cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    @Transactional
    public Map<String, Object> editQuestionByQuestionId(long questionId, QuestionBankInsertRequest requestPayload) {
        Map<String, Object> finalServiceResponse;
        try{
            logger.debug("Start - editQuestionByQuestionId question ID :: {} with requestPayload :: {}", questionId, requestPayload);

            //Step 1: Validate Subject ID
            long subjectId = requestPayload.getSubjectId();
            Optional<SubjectDomain> subjectDomainOptional = subjectRepo.findById(subjectId);

            if(subjectDomainOptional.isEmpty()){
                logger.error("Subject is not found for ID :: {}", subjectId);
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Subject is not found", "");
                return finalServiceResponse;
            }


            Optional<ChapterDomain> chapterDomainOptional = chapterRepo.findById(requestPayload.getChapterId());

            if(chapterDomainOptional.isEmpty()){
                logger.error("Chapter is not found for ID :: {}", requestPayload.getChapterId());
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Chapter is not found", "");
                return finalServiceResponse;
            }

            //Step 1: Validate Question ID
            Optional<QuestionBankDomain> questionBankOptional = questionBankRepo.findById(questionId);
            if(questionBankOptional.isEmpty()){
                logger.error("Question is not found for ID :: {}", questionId);
                finalServiceResponse = ResponseUtils.formatAPIResponse("1", "Question is not found", "");
                return finalServiceResponse;
            }

            QuestionBankDomain existQuestionBank = questionBankOptional.get();

            existQuestionBank.setSubject(subjectDomainOptional.get());
            existQuestionBank.setQuestionType(QuestionBankDomain.QuestionType.valueOf(requestPayload.getQuestionType()));
            existQuestionBank.setQuestionContent(requestPayload.getQuestionContent());
            existQuestionBank.setDifficulty(QuestionBankDomain.Difficulty.valueOf(requestPayload.getDifficulty()));
            existQuestionBank.setCreatedBy(requestPayload.getCreatedBy());
            existQuestionBank.setCreatedAt(LocalDateTime.now());
            existQuestionBank.setChapter(chapterDomainOptional.get());

            questionBankRepo.save(existQuestionBank);

            //Insert Option
            for(int i = 0; i < requestPayload.getOptionLists().size(); i++){
                OptionBankInsertRequest requestOptionBank = requestPayload.getOptionLists().get(i);
                Optional<OptionBankDomain> optionBankDomainOptional = optionBankRepo.findById(requestOptionBank.getOptionId());

                if(optionBankDomainOptional.isEmpty()){
                    logger.error("Question is not found for ID :: {}", questionId);
                    finalServiceResponse = ResponseUtils.formatAPIResponse("0", "Success Update Question", "");
                    return finalServiceResponse;
                }

                OptionBankDomain existOption = optionBankDomainOptional.get();
                existOption.setQuestionId(existQuestionBank.getId());
                existOption.setOptionText(requestOptionBank.getOptionText());
                existOption.setIsCorrect(requestOptionBank.getIsCorrect());

                optionBankRepo.save(existOption);
            }

            finalServiceResponse = ResponseUtils.formatAPIResponse("0", "Successfully Update Question", "");
            logger.debug("Successfully edit the Question :: {}", finalServiceResponse);
            return  finalServiceResponse;
        }catch (Exception e){
            logger.error("Exception while edit question banks :: {}" , e.getMessage());
            finalServiceResponse = ResponseUtils.formatAPIResponse("1", e.getMessage(), "");
            return  finalServiceResponse;
        }
    }
}

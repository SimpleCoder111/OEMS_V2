package org.demo.oems.service;

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
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public JSONObject getAllQuestionBanksBySubject(long subjectId){
        logger.debug("Get All Question Banks By Subject Services Start");

        JSONObject finalResponse = new JSONObject();
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
            finalResponse = ResponseUtils.formatServiceResponse("0", "Success");
            finalResponse.put("questionData", questionListsResponse);
            finalResponse.put("subjectId", subjectId);
            finalResponse.put("subjectName", subjectInfo.getSubjectName());
        }catch (Exception e){
            logger.error("Exception Get All Question Banks By Subject Services :: {}", e.getMessage());
            finalResponse = ResponseUtils.formatServiceResponse("1", e.getMessage());
        }

        return finalResponse;
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


    public JSONObject addQuestionBanks(QuestionBankInsertRequest requestPayload){
        JSONObject addQuestionBankResponse = new JSONObject();
        String responseStatus;
        String responseMessage;
        try{
            QuestionBankDomain newQuestionBank = new QuestionBankDomain();
            newQuestionBank.setQuestionType(QuestionBankDomain.QuestionType.valueOf(requestPayload.getQuestionType()));
            newQuestionBank.setQuestionContent(requestPayload.getQuestionContent());
            newQuestionBank.setDifficulty(QuestionBankDomain.Difficulty.valueOf(requestPayload.getDifficulty()));
            newQuestionBank.setCreatedBy(requestPayload.getCreatedBy());

            questionBankRepo.save(newQuestionBank);

            responseStatus = "success";
            responseMessage = "Successfully Insert";

            addQuestionBankResponse.put("responseStatus", responseStatus);
            addQuestionBankResponse.put("responseMessage", responseMessage);

        }catch (Exception e){
            logger.error("Exception while add question banks :: {}" , e.getMessage() );
            responseStatus = "fail";
            responseMessage = e.getMessage();

            addQuestionBankResponse.put("responseStatus", responseStatus);
            addQuestionBankResponse.put("responseMessage", responseMessage);

        }

        return  addQuestionBankResponse;
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

    public void deleteQuestionAndOptionBank(Long questionId){
        try{
            Optional<QuestionBankDomain> questionBankOptional = questionBankRepo.findById(questionId);

            if(questionBankOptional.isPresent()){
                QuestionBankDomain questionBank = questionBankOptional.get();

                long optionCount = optionBankRepo.countByQuestionId(questionBank.getId());
                logger.debug("Going to delete {} options related to question ID :: {}", optionCount, questionId);
                optionBankRepo.deleteByQuestionId(questionBank.getId());

            }
        }catch (Exception e){
            logger.error(CommonConstantUtils.LOG_PREFIX_EXCEPTION_IN_SERVICE, "Delete Question and Option Bank", e.getMessage());
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
}

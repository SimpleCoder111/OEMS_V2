package org.demo.oems.utils;


import org.demo.oems.domain.QuestionBankDomain;

public class CommonConstantUtils {
    private CommonConstantUtils(){}

    public static final String LOG_PREFIX_EXCEPTION_IN_CONTROLLER = "Exception in controller class {} :: {}";

    public static final String LOG_PREFIX_EXCEPTION_IN_SERVICE = "Exception in service class {} :: {}";

    public static final String VALUE_TEACHER = "TEACHER";

    public static final String VALUE_STUDENT = "STUDENT";

    public static final String VALUE_EASY = String.valueOf(QuestionBankDomain.Difficulty.EASY);

    public static final String VALUE_MEDIUM = String.valueOf(QuestionBankDomain.Difficulty.MEDIUM);

    public static final String VALUE_HARD = String.valueOf(QuestionBankDomain.Difficulty.HARD);

    public static final String VALUE_MCQ = String.valueOf(QuestionBankDomain.QuestionType.MULTIPLE_CHOICE);

    public static final String VALUE_FILL_IN_THE_BLANK = String.valueOf(QuestionBankDomain.QuestionType.FILL_BLANK);

    public static final String VALUE_TURE_FALSE = String.valueOf(QuestionBankDomain.QuestionType.TRUE_FALSE);





}

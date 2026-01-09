package org.demo.oems.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class QuestionBankArrayInsertRequest {

    private List<QuestionBankInsertRequest> questionBankInsertRequest;

}

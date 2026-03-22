package org.demo.oems.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class SmartGradingResult{
        int suggestedMark;
        String rationaleKhmer;
        String rationaleChinese;
        List<String> keyImprovements;
}

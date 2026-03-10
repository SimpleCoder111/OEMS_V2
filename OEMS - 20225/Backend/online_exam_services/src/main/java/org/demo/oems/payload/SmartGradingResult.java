package org.demo.oems.payload;

import java.util.List;

public record SmartGradingResult(
        int suggestedMark,
        String rationaleKhmer,
        String rationaleChinese,
        List<String> keyImprovements
) {
}

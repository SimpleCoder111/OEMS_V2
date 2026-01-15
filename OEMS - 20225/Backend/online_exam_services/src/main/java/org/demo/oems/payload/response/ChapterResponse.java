package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterResponse {

    private long id;

    private String name;

    private String description;

    private int orderIndex;

    private boolean isActive;

    private int questionCount;

}

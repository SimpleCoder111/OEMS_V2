package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectResponse {

    private String id;

    private String name;

    private String code;

    private String description;

    private boolean isActive;

    private List<ChapterResponse> chapterResponseList;

    private String createdAt;

    private String updatedAt;

}

package org.demo.oems.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderChapterRequest {

    private long id;

    private int orderIndex;

}

package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentActivitiesResponse {

    private String id;

    private String user;

    private String action;

    private String subject;

    private String timestamp;

}

package org.demo.oems.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListsResponse {

    private String id;

    private String name;

    private String email;

    private String role;

    private String status;

    private String createAt;

    private String lastLogin;

}

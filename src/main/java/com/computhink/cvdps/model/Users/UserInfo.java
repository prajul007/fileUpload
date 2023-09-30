package com.computhink.cvdps.model.Users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    @Id
    private String email;
    private String name;
    private String password;
    private String roles;
    private String clientIpAddress;
    private Integer expiryDays;
    private String token;

}


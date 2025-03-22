package org.romanzhula.api_gateway_settings.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String id;
    private String username;
    private String password;
    private Set<String> roles;

}

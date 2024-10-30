package com.rsuniverse.jobify_gateway.models.pojos;

import com.rsuniverse.jobify_gateway.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {
    private String fullName;
    private String email;
    private Set<UserRole> roles;
}

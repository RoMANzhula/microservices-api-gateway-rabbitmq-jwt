package org.romanzhula.user_service.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.romanzhula.user_service.models.enums.UserRole;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id", unique = true, updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nickname", unique = true, nullable = false)
    private String username;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phone;

    @JsonIgnore
    @Column(name = "password_crypt", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) //EnumType.ORDINAL - for numerated user roles ADMIN=0, USER=1 etc.
    @Column(name = "role", nullable = false)
    private UserRole role;

}

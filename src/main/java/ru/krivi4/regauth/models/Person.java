package ru.krivi4.regauth.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = Person.TABLE_NAME)
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"password", "email"})
public class Person {

    protected static final String TABLE_NAME = "users";
    private static final String SEQ_GENERATOR_NAME = "user_seq_gen";
    private static final String SEQ_NAME = "security.users_id_seq";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_ENABLED = "enabled";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_LAST_LOGIN = "last_login";
    private static final String JOIN_TABLE_PERSON_ROLE = "person_role";
    private static final String JOIN_SCHEMA_SECURITY = "security";
    private static final String JOIN_COLUMN_PERSON_ID = "person_id";
    private static final String JOIN_COLUMN_ROLE_ID = "role_id";
    private static final int LENGTH_USERNAME = 100;
    private static final int LENGTH_PASSWORD = 100;
    private static final int LENGTH_EMAIL = 100;
    private static final int LENGTH_PHONE_NUMBER = 20;
    private static final int SEQUENCE_ALLOCATION_SIZE = 1;
    private static final boolean NULLABLE = false;
    private static final boolean UNIQUE = true;
    private static final boolean UPDATABLE = true;
    private static final String MSG_USERNAME_REQUIRED = "{username.required.validation.exception}";
    private static final String MSG_PASSWORD_REQUIRED = "{password.required.validation.exception}";
    private static final String MSG_EMAIL_REQUIRED = "{email.required.validation.exception}";
    private static final String MSG_EMAIL_INVALID = "{email.invalid.validation.exception}";
    private static final String MSG_PHONE_REQUIRED = "{phone.required.validation.exception}";

    @Id
    @Column(name = COLUMN_ID, updatable = UPDATABLE, nullable = NULLABLE)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_GENERATOR_NAME)
    @SequenceGenerator(
            name = SEQ_GENERATOR_NAME,
            sequenceName = SEQ_NAME,
            allocationSize = SEQUENCE_ALLOCATION_SIZE
    )
    private int id;

    @NotBlank(message = MSG_USERNAME_REQUIRED)
    @Column(name = COLUMN_USERNAME, nullable = NULLABLE, unique = UNIQUE, length = LENGTH_USERNAME)
    private String username;

    @NotBlank(message = MSG_PASSWORD_REQUIRED)
    @Column(name = COLUMN_PASSWORD, nullable = NULLABLE, length = LENGTH_PASSWORD)
    private String password;

    @NotBlank(message = MSG_EMAIL_REQUIRED)
    @Email(message = MSG_EMAIL_INVALID)
    @Column(name = COLUMN_EMAIL, nullable = NULLABLE, length = LENGTH_EMAIL)
    private String email;

    @NotBlank(message = MSG_PHONE_REQUIRED)
    @Column(name = COLUMN_PHONE_NUMBER, nullable = NULLABLE, unique = UNIQUE, length = LENGTH_PHONE_NUMBER)
    private String phoneNumber;

    /**
     * Статус активности.
     */
    @Column(name = COLUMN_ENABLED, nullable = NULLABLE)
    private boolean enabled = false;

    @Column(name = COLUMN_CREATED_AT, nullable = NULLABLE)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = COLUMN_LAST_LOGIN)
    private LocalDateTime lastLogin;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = JOIN_TABLE_PERSON_ROLE,
            schema = JOIN_SCHEMA_SECURITY,
            joinColumns = @JoinColumn(name = JOIN_COLUMN_PERSON_ID),
            inverseJoinColumns = @JoinColumn(name = JOIN_COLUMN_ROLE_ID)
    )
    private Set<Role> roles = new HashSet<>();
}

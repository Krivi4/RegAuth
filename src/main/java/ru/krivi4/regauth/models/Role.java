package ru.krivi4.regauth.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = Role.TABLE_NAME)
@Getter
@Setter
@NoArgsConstructor
public class Role {

    protected static final String TABLE_NAME = "roles";
    private static final String SEQ_GENERATOR_NAME = "role_seq_gen";
    private static final String SEQ_NAME = "security.role_id_seq";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String MAPPED_BY_PERSON_ROLES = "roles";
    private static final int LENGTH_NAME = 32;
    private static final int SEQUENCE_ALLOCATION_SIZE = 1;
    private static final boolean NULLABLE = false;
    private static final boolean UNIQUE = true;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_GENERATOR_NAME)
    @SequenceGenerator(
            name = SEQ_GENERATOR_NAME,
            sequenceName = SEQ_NAME,
            allocationSize = SEQUENCE_ALLOCATION_SIZE
    )
    @Column(name = COLUMN_ID)
    private int id;

    @NotBlank
    @Column(name = COLUMN_NAME, nullable = NULLABLE, unique = UNIQUE, length = LENGTH_NAME)
    private String name;

    @ManyToMany(mappedBy = MAPPED_BY_PERSON_ROLES)
    private Set<Person> persons;
}

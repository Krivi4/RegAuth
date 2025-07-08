package ru.krivi4.regauth.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "role_seq_gen")
    @SequenceGenerator(
            name            = "role_seq_gen",
            sequenceName    = "security.role_id_seq",
            allocationSize  = 1)
    @Column(name = "id")
    private int id;

    @NotBlank
    @Column(name = "name", nullable = false, unique = true, length = 32)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<Person> persons;
}

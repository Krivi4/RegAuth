package ru.krivi4.regauth.adapters.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.krivi4.regauth.models.Person;
import ru.krivi4.regauth.views.AdminUserView;

import java.util.List;

/**
 * MapStruct-маппер: Person → AdminUserView.
 */
@Mapper(componentModel = AdminUserViewMapper.SPRING_COMPONENT_MODEL, uses = RoleMapper.class)
public interface AdminUserViewMapper {

    String SPRING_COMPONENT_MODEL = "spring";
    String ROLE_TO_STRING_QUALIFIER = "roleToString";

    /**
     * Конвертирует одного пользователя.
     */
    @Mapping(target = "roles", qualifiedByName = ROLE_TO_STRING_QUALIFIER)
    AdminUserView toView(Person person);

    /**
     * Конвертирует список пользователей.
     */
    List<AdminUserView> toViewList(List<Person> people);
}

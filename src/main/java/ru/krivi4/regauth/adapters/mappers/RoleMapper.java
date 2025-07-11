package ru.krivi4.regauth.adapters.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.krivi4.regauth.models.Role;

/**
 * Отдельный маппер «Role → String».
 * Нужен, чтобы в интерфейсе AdminUserViewMapper не было никакой логики.
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

    /** Берём только имя роли. */
    @Named("roleToString")
    default String map(Role role){
        return role.getName();
    };
}

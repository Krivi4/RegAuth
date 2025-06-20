package ru.krivi4.regauth.adapters.mappers;

import org.mapstruct.Mapper;
import ru.krivi4.regauth.dtos.PersonDto;
import ru.krivi4.regauth.models.Person;

/** MapStruct-маппер между DTO и сущностью Person.*/
@Mapper(componentModel = "spring")
public interface PersonMapper {

  /**Преобразует DTO в сущность.*/
  Person toEntity(PersonDto dto);
}

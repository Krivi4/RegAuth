package ru.krivi4.regauth;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.krivi4.regauth.adapters.mappers.PersonMapper;
import ru.krivi4.regauth.dtos.PersonDto;
import ru.krivi4.regauth.models.Person;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MapStruct-генерация корректно копирует
 * данные из PersonDto в сущность Person.
 */
class PersonMapperTest {

  private final PersonMapper mapper = Mappers.getMapper(PersonMapper.class);

  /**
   * Поля username, password, email и phoneNumber
   * должны перейти без искажений.
   */
  @Test
  void toEntity_shouldCopyAllSimpleFields() {
    PersonDto dto = new PersonDto();
    dto.setUsername("user1");
    dto.setPassword("rawPass");
    dto.setEmail("u@example.com");
    dto.setPhoneNumber("+70000000000");


    Person entity = mapper.toEntity(dto);

    assertThat(entity.getUsername()).isEqualTo(dto.getUsername());
    assertThat(entity.getPassword()).isEqualTo(dto.getPassword());
    assertThat(entity.getEmail()).isEqualTo(dto.getEmail());
    assertThat(entity.getPhoneNumber()).isEqualTo(dto.getPhoneNumber());
  }
}
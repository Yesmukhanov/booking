package kz.sdu.booking.mapper;

import kz.sdu.booking.model.dto.UserDTO;
import kz.sdu.booking.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

	UserDTO toDto(User user);

}

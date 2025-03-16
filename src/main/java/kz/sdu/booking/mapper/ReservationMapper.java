package kz.sdu.booking.mapper;

import kz.sdu.booking.model.dto.ReservationDto;
import kz.sdu.booking.model.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
	ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);
	ReservationDto toDto(Reservation reservation);
}

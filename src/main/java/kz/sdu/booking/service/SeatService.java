package kz.sdu.booking.service;

import kz.sdu.booking.utils.Errors;
import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.mapper.SeatMapper;
import kz.sdu.booking.model.dto.ListResponse;
import kz.sdu.booking.model.dto.SeatDto;
import kz.sdu.booking.model.entity.Seat;
import kz.sdu.booking.model.entity.User;
import kz.sdu.booking.model.enums.Role;
import kz.sdu.booking.repository.SeatRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class SeatService {
    private final UserService userService;
    private final SeatRepository seatRepository;

    /**
     * Получает список всех мест
     * @return {@link ListResponse} с данными о местах и их количеством.
     * Если список пуст, возвращает {@link ListResponse#empty()}.
     */
    public ListResponse<SeatDto> getAllSeats() {
        final List<Seat> allSeatList = seatRepository.findAll();

        if (CollectionUtils.isEmpty(allSeatList)) {
            return ListResponse.empty();
        }
        final List<SeatDto> seatDtoList = SeatMapper.INSTANCE.toDtoList(allSeatList);

        return new ListResponse<>(seatDtoList, seatDtoList.size());
    }

    /**
     * Получает информацию о конкретном месте по идентификатору
     * <p/>
     * @param id Идентификатор места
     * @return {@link SeatDto} С данными о месте
     * @throws UserInputException Если место не найдено
     */
    public SeatDto getSeatById(final Long id) throws UserInputException {
        final Seat seat = findById(id);

        return convertAndFill(seat);
    }

    public Seat findById(final Long id) throws UserInputException {
        final Seat seat = seatRepository.findById(id)
                                        .orElseThrow(() -> new UserInputException(Errors.MSG_SEAT_NOT_FOUND
                                                                                  + " (ID: " + id + ")"));

        return seat;
    }

    /**
     * Блокирует место (доступно только ADMIN).
     * <p/>
     * @param id идентификатор места
     * @return обновленный объект {@link SeatDto}
     * @throws UserInputException если у пользователя нет прав или место не найдено
     */
    public SeatDto blockSeat(Long id) throws UserInputException {
        validateAdminAccess();

        final Seat seat =
            seatRepository.findById(id)
                          .orElseThrow(() -> new UserInputException(String.format(Errors.MSG_SEAT_NOT_FOUND, id)));

        seat.setBlocked(true);
        seatRepository.save(seat);

        return convertAndFill(seat);
    }

    /**
     * Разблокирует место (доступно только ADMIN).
     * <p/>
     * @param id идентификатор места
     * @return обновленный объект {@link SeatDto}
     * @throws UserInputException если у пользователя нет прав или место не найдено
     */
    public SeatDto unblockSeat(final Long id) throws UserInputException {
        validateAdminAccess();

        final Seat seat =
            seatRepository.findById(id)
                          .orElseThrow(() -> new UserInputException(String.format(Errors.MSG_SEAT_NOT_FOUND, id)));

        seat.setBlocked(false);
        seatRepository.save(seat);

        return convertAndFill(seat);
    }

    /**
     * Проверяет, является ли пользователь ADMIN.
     * @throws UserInputException если пользователь не ADMIN
     */
    private void validateAdminAccess() throws UserInputException {
        final User user = userService.getAuthenticateUser();
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new UserInputException(Errors.MSG_ACCESS_DENIED);
        }
    }

    private SeatDto convertAndFill(final Seat seat) {
        if (Objects.isNull(seat)) {
            return null;
        }

        return SeatMapper.INSTANCE.toDto(seat);
    }
}

package kz.sdu.booking.service;

import kz.sdu.booking.model.dto.UserStatisticsDto;
import kz.sdu.booking.model.entity.Reservation;
import kz.sdu.booking.model.enums.ReservationStatus;
import kz.sdu.booking.model.enums.Role;
import kz.sdu.booking.utils.Errors;
import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.mapper.UserMapper;
import kz.sdu.booking.model.dto.UserDto;
import kz.sdu.booking.model.dto.UserEditRequestDto;
import kz.sdu.booking.model.entity.User;
import kz.sdu.booking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ReservationService reservationService;

    /**
     * Находит пользователя по идентификатору
     * <p/>
     * @param id Идентификатор пользователь
     * @return Информация о пользователе
     */
    public UserDto findUserById(final Long id) throws UserInputException {
        return convertAndFill(find(id));
    }

    public User find(final Long id) throws UserInputException {
        return userRepository.findById(id).orElseThrow(() -> new UserInputException(Errors.MSG_USER_IS_NULL));
    }

    /**
     * Обновляет пользователя по идентификатору
     * <p/>
     * @param id дентификатор пользователя
     * @param requestDto Изменяемые данные
     * @return Информация о пользователе
     * @throws UserInputException Если пользователь не найден
     */
    public UserDto updateUserById(final Long id, final UserEditRequestDto requestDto) throws UserInputException {
        final User user = userRepository.findById(id).orElseThrow(() -> new UserInputException(Errors.MSG_USER_IS_NULL));

        final String requestFirstName = requestDto.getFirstName();
        if (Objects.nonNull(requestFirstName)) {
            user.setFirstName(requestFirstName);
        }

        final String requestLastName = requestDto.getLastName();
        if (Objects.nonNull(requestLastName)) {
            user.setLastName(requestLastName);
        }

        final String requestPassword = requestDto.getPassword();
        final String password = user.getPassword();
        if (Objects.nonNull(requestPassword)) {
            if (!passwordEncoder.matches(requestPassword, password)) {
                user.setPassword(passwordEncoder.encode(requestPassword));
            }
        }

        userRepository.save(user);

        return convertAndFill(user);
    }

    /**
     * Помечаем пользователя как удаленного
     * <p/>
     * @param userId Идентификатор пользователя
     * @return Информация о пользователе
     * @throws UserInputException Если пользователь не найден
     */
    public UserDto deleteUser(final Long userId) throws UserInputException {
        final User user = userRepository.findById(userId).orElseThrow(() -> new UserInputException(Errors.MSG_USER_IS_NULL));
        user.setIsDeleted(Boolean.TRUE);

        userRepository.save(user);

        return convertAndFill(user);
    }

    /**
     * Возвращает список всех пользователей, если текущий пользователь — ADMIN.
     * @return список {@link UserDto}
     * @throws UserInputException если у пользователя нет прав
     */
    public List<UserDto> getAllUsers() throws UserInputException {
        if (!getAuthenticateUser().getRole().equals(Role.ADMIN)) {
            throw new UserInputException(Errors.MSG_ACCESS_DENIED);
        }

        final List<User> userList = userRepository.findAll();
        if (CollectionUtils.isEmpty(userList)) {
            return Collections.emptyList();
        }

        return convertAndFillList(userList);
    }

    /**
     * Конвертирует объект {@link User} в {@link UserDto}.
     * <p/>
     * @param user объект пользователя
     * @return DTO-представление пользователя или {@code null}, если входной объект равен {@code null}
     */
    public UserDto convertAndFill(final User user) {
        if (Objects.isNull(user)) {
            return null;
        }

        return UserMapper.INSTANCE.toDto(user);
    }

    /**
     * Конвертирует список пользователей {@link User} в список {@link UserDto}.
     * <p/>
     * @param userList список пользователей
     * @return список DTO-представлений пользователей; если входной список пустой — возвращается пустой список
     */
    public List<UserDto> convertAndFillList(final List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return Collections.emptyList();
        }
        final List<UserDto> userDtoList = new ArrayList<>();

        for (final User user : userList) {
            userDtoList.add(convertAndFill(user));
        }

        return userDtoList;
    }

    /**
     * Получает текущего аутентифицированного пользователя из контекста безопасности.
     * @return объект {@link User}, представляющий текущего пользователя
     * @throws ClassCastException если объект principal не является экземпляром {@link User}
     */
    public User getAuthenticateUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return (User) authentication.getPrincipal();
    }

    /**
     *
     * @return
     */
    public UserDto getMe() {
        return convertAndFill(getAuthenticateUser());
    }

    /**
     * Возвращает статистику пользователя по его бронированиям.
     * <p/>
     * @param userId идентификатор пользователя
     * @return объект {@link UserStatisticsDto} со статистикой
     */
    public UserStatisticsDto getUserStatistics(final Long userId) throws UserInputException {
        // Получаем все бронирования пользователя
        final List<Reservation> reservations = reservationService.findByUserId(userId);

        if (reservations.isEmpty()) {
            return new UserStatisticsDto(0, 0, 0, 0, 0);
        }

        int totalMinutes = 0;
        int bookingDaysInMonth = 0;
        int recordHours = 0;
        int recordDay = 0;

        final LocalDate currentMonth = LocalDate.now();

        for (final Reservation reservation : reservations) {
            if (reservation.getStatus() == ReservationStatus.ACTIVE || reservation.getStatus() == ReservationStatus.RESERVED) {
                // Считаем длительность
                if (reservation.getStartTime() != null && reservation.getEndTime() != null) {
                    int minutes = (int) java.time.Duration.between(reservation.getStartTime(), reservation.getEndTime()).toMinutes();
                    totalMinutes += minutes;

                    // Считаем рекорды
                    if (minutes > recordHours * 60) {
                        recordHours = minutes / 60; // сохраняем часы
                    }
                    if (minutes > recordDay * 60) {
                        recordDay = minutes / 60;   // по логике это тоже часы, но можно считать иначе
                    }
                }

                // Если бронирование в этом месяце
                if (Objects.nonNull(reservation.getDate()) && reservation.getDate().getMonth() == currentMonth.getMonth()) {
                    bookingDaysInMonth++;
                }
            }
        }

        int hoursInLibrary = totalMinutes / 60;
        int minutesInLibrary = totalMinutes % 60;

        return new UserStatisticsDto(hoursInLibrary, minutesInLibrary, bookingDaysInMonth, recordDay, recordHours);
    }

}

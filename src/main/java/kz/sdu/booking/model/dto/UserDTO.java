package kz.sdu.booking.model.dto;

import kz.sdu.booking.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private Integer id;
	private String name;
	private String email;
	private Role role;
}

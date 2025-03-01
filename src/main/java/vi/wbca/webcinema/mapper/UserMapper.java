package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.UserDTO;
import vi.wbca.webcinema.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bills", ignore = true)
    @Mapping(target = "confirmEmails", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(UserDTO userDTO);

//    @Mapping(target = "bills", ignore = true)
//    @Mapping(target = "confirmEmails", ignore = true)
//    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(source = "username", target = "userName")
    @Mapping(source = "roles", target = "roleNames")
    UserDTO toUserDTO(User user);
}

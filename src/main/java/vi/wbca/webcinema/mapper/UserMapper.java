package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.user.UserDTO;
import vi.wbca.webcinema.model.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bills", ignore = true)
    @Mapping(target = "confirmEmails", ignore = true)
    @Mapping(target = "accessTokens", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "rankCustomer", ignore = true)
    @Mapping(target = "userStatus", ignore = true)
    User toUser(UserDTO userDTO);

    @Mapping(source = "username", target = "userName")
    @Mapping(target = "accessToken", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(source = "roles", target = "roleNames")
    @Mapping(target = "listRoles", ignore = true)
    UserDTO toUserDTO(User user);
}

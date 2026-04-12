package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.model.dto.user.UserDTO;
import vi.wbca.webcinema.model.entity.user.User;
import vi.wbca.webcinema.model.response.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bills", ignore = true)
    @Mapping(target = "confirmEmails", ignore = true)
    @Mapping(target = "accessTokens", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "rankCustomer", ignore = true)
    @Mapping(target = "userStatus", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "changeHistories", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUser(UserDTO userDTO);

    @Mapping(source = "username", target = "userName")
    UserDTO toUserDTO(User user);

    @Mapping(source = "username", target = "userName")
    UserResponse toResponse(User user);
}

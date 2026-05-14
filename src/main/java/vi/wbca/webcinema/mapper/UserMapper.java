package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import vi.wbca.webcinema.model.dto.user.UserDTO;
import vi.wbca.webcinema.model.entity.user.User;
import vi.wbca.webcinema.model.entity.user.UserProfile;
import vi.wbca.webcinema.model.response.UserResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
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
    User toUser(UserDTO userDTO);

    @Mapping(source = "username", target = "userName")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "profile.point", target = "point")
    @Mapping(source = "profile.email", target = "email")
    @Mapping(source = "profile.name", target = "name")
    @Mapping(source = "profile.phoneNumber", target = "phoneNumber")
    @Mapping(source = "profile.birthDate", target = "birthDate")
    @Mapping(source = "profile.gender", target = "gender")
    @Mapping(source = "role", target = "role")
    UserDTO toUserDTO(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "point", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateUserProfileFromDTO(UserDTO userDTO, @MappingTarget UserProfile userProfile);

    @Mapping(source = "username", target = "userName")
    @Mapping(source = "profile.email", target = "email")
    @Mapping(source = "profile.name", target = "name")
    @Mapping(source = "profile.phoneNumber", target = "phoneNumber")
    @Mapping(source = "profile.point", target = "point")
    UserResponse toResponse(User user);
}

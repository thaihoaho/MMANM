package com.warehouse.warehousemanager.mapper;

import com.warehouse.warehousemanager.dto.UserDto;
import com.warehouse.warehousemanager.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserDto toDto(User user);

    @Mapping(target = "role", expression = "java(com.warehouse.warehousemanager.entity.User.Role.valueOf(userDto.getRole()))")
    User toEntity(UserDto userDto);
}
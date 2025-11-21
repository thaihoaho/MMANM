package com.warehouse.warehousemanager.mapper;

import com.warehouse.warehousemanager.dto.ImportDto;
import com.warehouse.warehousemanager.entity.Import;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ImportMapper {
    ImportMapper INSTANCE = Mappers.getMapper(ImportMapper.class);

    ImportDto toDto(Import importRecord);

    Import toEntity(ImportDto importDto);
}
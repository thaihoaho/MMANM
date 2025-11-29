package com.warehouse.warehousemanager.mapper;

import com.warehouse.warehousemanager.dto.ExportDto;
import com.warehouse.warehousemanager.entity.Export;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExportMapper {
    ExportMapper INSTANCE = Mappers.getMapper(ExportMapper.class);

    ExportDto toDto(Export exportRecord);

    Export toEntity(ExportDto exportDto);
}
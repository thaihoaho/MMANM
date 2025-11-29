package com.warehouse.warehousemanager.mapper;

import com.warehouse.warehousemanager.dto.ProductDto;
import com.warehouse.warehousemanager.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDto toDto(Product product);

    Product toEntity(ProductDto productDto);
}
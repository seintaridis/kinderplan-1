package com.webapplication.mapper;

import com.webapplication.dto.CategoryDto;
import com.webapplication.entity.CategoryEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * Created by mary on 14/6/2017.
 */

@Component
public class CategoryMapper {
	public HashSet<CategoryEntity> categoryEntityFromCategoryDto(HashSet<CategoryDto> categoriesDto){
		HashSet<CategoryEntity> categoryEntities = new HashSet<CategoryEntity>(categoriesDto.size());
		for (CategoryDto categoryDto : categoriesDto){
			CategoryEntity categoryEntity = new CategoryEntity();
			categoryEntity.setCategory(categoryDto.getCategory());
			categoryEntities.add(categoryEntity);
		}

		return categoryEntities;
	}
}
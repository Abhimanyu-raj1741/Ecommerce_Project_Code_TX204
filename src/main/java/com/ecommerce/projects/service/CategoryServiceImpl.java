package com.ecommerce.projects.service;

import com.ecommerce.projects.Repositories.CategoryRepository;
import com.ecommerce.projects.exceptions.APIException;
import com.ecommerce.projects.exceptions.ResourceNotFoundException;
import com.ecommerce.projects.model.Category;
import com.ecommerce.projects.payload.CategoryDTO;
import com.ecommerce.projects.payload.CategoryResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    // private List<Category> categories= new ArrayList<>();

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();
        if(categories.isEmpty())
            throw new APIException("No Category Created till now.");

        List<CategoryDTO> categoryDTOS=categories.stream().map(category -> modelMapper.map(category,CategoryDTO.class)).collect(Collectors.toList());

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());




        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO,Category.class);
        Category categoryFromDb = categoryRepository.findByCategoryName(category.getCategoryName());
        System.out.println(category.getCategoryName());
        if(categoryFromDb != null) {
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists !!!");
        }

       Category savedCategory = categoryRepository.save(category);

        return modelMapper.map(savedCategory,CategoryDTO.class);

    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {

        Optional<Category> categories = categoryRepository.findById(categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        categoryRepository.delete(category);
        CategoryDTO categoryDTO = modelMapper.map(category,CategoryDTO.class);
        return categoryDTO;
    }


    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO , Long categoryId) {
        Optional<Category> saveCategoryOptional = categoryRepository.findById(categoryId);

        Category categoryFromDb = saveCategoryOptional.orElseThrow(()-> new ResourceNotFoundException("Category", "id", categoryId));

        Category categoryto = modelMapper.map(categoryDTO,Category.class);

        categoryFromDb.setCategoryId(categoryId);

        categoryFromDb =  categoryRepository.save(categoryto);
         return  modelMapper.map(categoryFromDb, CategoryDTO.class);




    }
}

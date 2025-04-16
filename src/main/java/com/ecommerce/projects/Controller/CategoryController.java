package com.ecommerce.projects.Controller;

import com.ecommerce.projects.config.AppConstant;
import com.ecommerce.projects.model.Category;
import com.ecommerce.projects.payload.CategoryDTO;
import com.ecommerce.projects.payload.CategoryResponse;
import com.ecommerce.projects.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController {
  @Autowired
    private CategoryService categoryService;


  @GetMapping("/api/public/echo")
  public ResponseEntity<String> EechoMessage(@RequestParam (name = "message") String msg){

      return  new ResponseEntity<>(msg,HttpStatus.OK);
  }


    @GetMapping("/api/public/categories")
    public ResponseEntity<CategoryResponse> getAllCateogries(
            @RequestParam (name = "pageNumber" ,defaultValue= AppConstant.PAGE_NUMBER,required = false) Integer pageNumber ,
            @RequestParam (name="pageSize",defaultValue = AppConstant.PAGE_SIZE,required = false) Integer pageSize ,
    @RequestParam (name="sortBy",defaultValue = AppConstant.SORT_CATEGORIES_BY,required = false) String sortBy ,
    @RequestParam(name="sortOrder",defaultValue = AppConstant.SORT_DIR,required = false) String sortOrder )

    {


       CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortOrder);

        return new ResponseEntity<>(categoryResponse,HttpStatus.OK);
    }
    @PostMapping ("/api/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){

        CategoryDTO saveCategoryDto = categoryService.createCategory(categoryDTO);
        String sc = "Category Added Successfully Bro";
        return new ResponseEntity<>(saveCategoryDto,HttpStatus.CREATED);
    }

    @DeleteMapping("/api/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@Valid @PathVariable Long categoryId){

            CategoryDTO deleteCategoryDTO = categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(deleteCategoryDTO, HttpStatus.OK);

//        catch(ResponseStatusException e ){
//            return new ResponseEntity<>(e.getReason(),e.getStatusCode());
//        }

    }
    @PutMapping("/api/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDTO , @PathVariable Long categoryId){

            CategoryDTO savedCategoryDTO = categoryService.updateCategory(categoryDTO,categoryId);
            return new ResponseEntity<>(savedCategoryDTO, HttpStatus.OK);

    }

}

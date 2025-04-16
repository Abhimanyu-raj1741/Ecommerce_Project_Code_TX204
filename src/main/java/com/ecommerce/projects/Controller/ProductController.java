package com.ecommerce.projects.Controller;

import com.ecommerce.projects.config.AppConstant;
import com.ecommerce.projects.model.Product;
import com.ecommerce.projects.payload.ProductDTO;
import com.ecommerce.projects.payload.ProductResponse;
import com.ecommerce.projects.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.channels.MulticastChannel;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;


    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable  Long categoryId) {


        ProductDTO savedProductDTO= productService.addProduct(categoryId,productDTO);

        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);

    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(  @RequestParam (name = "pageNumber" ,defaultValue= AppConstant.PAGE_NUMBER,required = false) Integer pageNumber ,
                                                            @RequestParam (name="pageSize",defaultValue = AppConstant.PAGE_SIZE,required = false) Integer pageSize ,
                                                            @RequestParam (name="sortBy",defaultValue = AppConstant.SORT_PRODUCTS_BY,required = false) String sortBy ,
                                                            @RequestParam(name="sortOrder",defaultValue = AppConstant.SORT_DIR,required = false) String sortOrder )
 {

        ProductResponse productResponse = productService.getAllProducts(pageNumber, pageSize, sortBy,sortOrder);

        return  new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory( @RequestParam (name = "pageNumber" ,defaultValue= AppConstant.PAGE_NUMBER,required = false) Integer pageNumber ,
                                                                  @RequestParam (name="pageSize",defaultValue = AppConstant.PAGE_SIZE,required = false) Integer pageSize ,
                                                                  @RequestParam (name="sortBy",defaultValue = AppConstant.SORT_PRODUCTS_BY,required = false) String sortBy ,
                                                                  @RequestParam(name="sortOrder",defaultValue = AppConstant.SORT_DIR,required = false) String sortOrder ,

    @PathVariable  Long categoryId) {

        ProductResponse productResponse = productService.searchByCategory(categoryId,pageNumber, pageSize,sortBy, sortOrder);

        return  new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeword(@RequestParam (name = "pageNumber" ,defaultValue= AppConstant.PAGE_NUMBER,required = false) Integer pageNumber ,
                                                               @RequestParam (name="pageSize",defaultValue = AppConstant.PAGE_SIZE,required = false) Integer pageSize ,
                                                               @RequestParam (name="sortBy",defaultValue = AppConstant.SORT_PRODUCTS_BY,required = false) String sortBy ,
                                                               @RequestParam(name="sortOrder",defaultValue = AppConstant.SORT_DIR,required = false) String sortOrder ,


                                                               @PathVariable String keyword) {
       ProductResponse productResponse =  productService.searchProductByKeyword('%'+keyword+'%',pageNumber, pageSize,sortBy, sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.FOUND);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable  Long productId) {
        ProductDTO updateProductDTO = productService.updateProduct(productDTO,productId);
        return new ResponseEntity<>(updateProductDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable  Long productId) {
        ProductDTO productDTO = productService.deleteProduct(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable  Long productId, @RequestParam("Image") MultipartFile image) throws IOException
    {

       ProductDTO updatedProduct = productService.updateProductImage(productId,image);

        return new ResponseEntity<>(updatedProduct,HttpStatus.OK);
    }
}

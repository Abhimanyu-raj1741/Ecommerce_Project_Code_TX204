package com.ecommerce.projects.service;

import com.ecommerce.projects.Repositories.CategoryRepository;
import com.ecommerce.projects.Repositories.ProductRepository;
import com.ecommerce.projects.exceptions.APIException;
import com.ecommerce.projects.exceptions.ResourceNotFoundException;
import com.ecommerce.projects.model.Category;
import com.ecommerce.projects.model.Product;
import com.ecommerce.projects.payload.ProductDTO;
import com.ecommerce.projects.payload.ProductResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private String check;

    @Autowired
    private FileService fileService;

    @Value(("${project.image}"))
    private String path;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException("Category", "categoryId", categoryId));
        boolean isProductNotPresent = true;
        List<Product> products = category.getProducts();
        for (Product product : products) {
            if(product.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }
        if(isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);

            product.setImage("default.png");
            product.setCategory(category);
            double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);

            return modelMapper.map(savedProduct, ProductDTO.class);
        }
        else {
            throw new APIException("Product already exists");
        }

    }

    public ProductResponse getAllProducts(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                             ?Sort.by(sortBy).ascending()
                             :Sort.by(sortBy).descending();

        Pageable pagedetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> page = productRepository.findAll(pagedetails);

        List<Product> products = page.getContent();
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).collect(Collectors.toList());

        if (products.isEmpty()) {
            throw new APIException("No products found");
        }
            ProductResponse productResponse = new ProductResponse();
            productResponse.setContent(productDTOS);
            productResponse.setPageNumber(page.getNumber());
            productResponse.setPageSize(page.getSize());
            productResponse.setTotalElements(page.getTotalElements());
            productResponse.setTotalPages(page.getTotalPages());
            productResponse.setLastPage(page.isLast());

            return productResponse;


    }
    public ProductResponse searchByCategory(Long categoryId,Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {


        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pagedetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> page = productRepository.findByCategoryOrderByPriceAsc(category ,pagedetails);


        List<Product> product =  page.getContent();
        if(product.isEmpty()) {
            throw new APIException(category.getCategoryName() + " does not have any products" );
        }
        List<ProductDTO> productDTO =  product.stream().map(product1 -> modelMapper.map(product1, ProductDTO.class)).collect(Collectors.toList());

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTO);
        productResponse.setPageNumber(page.getNumber());
        productResponse.setPageSize(page.getSize());
        productResponse.setTotalElements(page.getTotalElements());
        productResponse.setTotalPages(page.getTotalPages());
        productResponse.setLastPage(page.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pagedetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> page = productRepository.findByProductNameLikeIgnoreCase(keyword,pagedetails);


        List<Product> product =  page.getContent();

        if(product.isEmpty()) {
            throw new APIException("No products found with keyword " + keyword );
        }
        List<ProductDTO> productDTO =  product.stream()
                .map(product1 -> modelMapper.map(product1, ProductDTO.class))
                .collect(Collectors.toList());



        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTO);
        productResponse.setPageNumber(page.getNumber());
        productResponse.setPageSize(page.getSize());
        productResponse.setTotalElements(page.getTotalElements());
        productResponse.setTotalPages(page.getTotalPages());
        productResponse.setLastPage(page.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        // Get the product from DB
        Product productFromDB = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        Product product = modelMapper.map(productDTO, Product.class);
        productFromDB.setProductName(product.getProductName());
        productFromDB.setDescription(product.getDescription());
        productFromDB.setQuantity(product.getQuantity());
        productFromDB.setPrice(product.getPrice());
        productFromDB.setDiscount(product.getDiscount());
        productFromDB.setSpecialPrice(product.getSpecialPrice());

        // save to database
        Product savedProduct =  productRepository.save(productFromDB);
        System.out.println(check);
        return  modelMapper.map(savedProduct, ProductDTO.class);

    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Optional<Product> productFromDB = productRepository.findById(productId);
        if(productFromDB.isPresent()) {
             productRepository.deleteById(productId);
        }
        else
            throw new ResourceNotFoundException("Product", "productId", productId);

        return  modelMapper.map(productFromDB.get(), ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        // Get the product from DB
         Product productFromDb  =   productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // upload image to server
        // Get the file name of uploaded image

         String filename = fileService.uploadImage(path,image);

        // updating the new file name to the product
        productFromDb.setImage(filename);

        // save updated product
        Product updatedProduct = productRepository.save(productFromDb);
        return  modelMapper.map(productFromDb, ProductDTO.class);

    }


}

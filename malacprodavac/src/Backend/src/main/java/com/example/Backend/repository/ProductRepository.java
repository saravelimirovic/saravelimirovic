package com.example.Backend.repository;

import com.example.Backend.dto.ProductDetailsDTO;
import com.example.Backend.entity.Product;
import com.example.Backend.entity.ProductCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends ListCrudRepository<Product, Long> {

    List<Product> findByProductCategory(ProductCategory productCategory);

    Optional<Product> findProductById(Long id);

    List<Product> findProductByCompanyId(Long companyId);

    Long deleteProductById(Long id);

    List<Product> findProductsByNameContainingIgnoreCase(String search);
}

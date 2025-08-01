package com.docugenius.demo.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service class responsible for managing product-related operations.
 * This service demonstrates basic CRUD (Create, Read, Update, Delete)
 * operations for a simple Product entity.
 */
@Service
public class ProductService {

    private final List<Product> products = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong(); // Used for generating unique product IDs

    /**
     * Constructor for ProductService. Initializes with some sample products.
     */
    public ProductService() {
        products.add(new Product(counter.incrementAndGet(), "Laptop", 1200.00, "High-performance laptop"));
        products.add(new Product(counter.incrementAndGet(), "Mouse", 25.00, "Wireless ergonomic mouse"));
        products.add(new Product(counter.incrementAndGet(), "Keyboard", 75.00, "Mechanical gaming keyboard"));
    }

    /**
     * Retrieves all products available in the system.
     *
     * @return A list of all products.
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products); // Return a copy to prevent external modification
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id The ID of the product to retrieve.
     * @return An Optional containing the product if found, or empty if not.
     */
    public Optional<Product> getProductById(Long id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    /**
     * Adds a new product to the system. A new unique ID is assigned.
     *
     * @param product The product object to add (ID will be ignored and generated).
     * @return The newly created product with its assigned ID.
     */
    public Product addProduct(Product product) {
        product.setId(counter.incrementAndGet()); // Assign a new ID
        products.add(product);
        return product;
    }

    /**
     * Updates an existing product.
     *
     * @param id The ID of the product to update.
     * @param updatedProduct The product object containing updated details.
     * @return An Optional containing the updated product if found and updated, or empty if not found.
     */
    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(existingProduct -> {
                    existingProduct.setName(updatedProduct.getName());
                    existingProduct.setPrice(updatedProduct.getPrice());
                    existingProduct.setDescription(updatedProduct.getDescription());
                    return existingProduct;
                });
    }

    /**
     * Deletes a product by its unique identifier.
     *
     * @param id The ID of the product to delete.
     * @return True if the product was successfully deleted, false otherwise.
     */
    public boolean deleteProduct(Long id) {
        return products.removeIf(p -> p.getId().equals(id));
    }
}

/**
 * A simple POJO (Plain Old Java Object) representing a Product.
 * Used for data transfer within the service.
 */
class Product {
    private Long id;
    private String name;
    private Double price;
    private String description;

    public Product(Long id, String name, Double price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
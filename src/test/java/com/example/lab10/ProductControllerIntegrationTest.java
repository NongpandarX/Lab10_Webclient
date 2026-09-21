package com.example.lab10;

import com.example.lab10.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class ProductControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testGetAllProducts() {
        webTestClient.get()
                .uri("/products")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Product.class)
                .hasSize(3);
    }

    @Test
    void testGetProductById() {
        webTestClient.get()
                .uri("/products/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .value(product -> {
                    org.junit.jupiter.api.Assertions.assertEquals("1", product.getId());
                    org.junit.jupiter.api.Assertions.assertTrue(product.getName().contains("iPhone"));
                });
    }

    @Test
    void testGetByCategory() {
        webTestClient.get()
                .uri("/products/category/Electronics")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .hasSize(3);
    }

    @Test
    void testGetPrice() {
        webTestClient.get()
                .uri("/products/1/price")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Double.class)
                .isEqualTo(35910.0);
    }

    @Test
    void testCreateAndDeleteProduct() {
        Product newProduct = new Product("test-99", "Test Tablet", "Electronics", "BrandX", 10, 15000.0, "NONE");

        // 1. Create
        webTestClient.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newProduct)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .value(p -> org.junit.jupiter.api.Assertions.assertEquals("test-99", p.getId()));

        // 2. Verify exists
        webTestClient.get()
                .uri("/products/test-99")
                .exchange()
                .expectStatus().isOk();

        // 3. Delete
        webTestClient.delete()
                .uri("/products/test-99")
                .exchange()
                .expectStatus().isOk();
    }
}

package com.example.lab10.client;

import com.example.lab10.model.Product;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * ProductWebClient — Reactive HTTP Client
 *
 * ✅ WebClient setup และ Bean config ครบแล้ว
 * ✅ method getProductById() ทำเสร็จแล้วเป็นตัวอย่าง (30%)
 * ❌ TODO: เติม method body ที่เหลือ (70%)
 *
 * WebClient method chain:
 *   client.get()              ← HTTP method
 *     .uri("/products/{id}", id)  ← URL
 *     .retrieve()             ← เริ่มรับ response
 *     .bodyToMono(T.class)    ← แปลงเป็น Mono<T>
 *     .bodyToFlux(T.class)    ← แปลงเป็น Flux<T>
 */
@Component
public class ProductWebClient {

    // ✅ WebClient ตั้งค่าแล้ว ชี้ไปที่ server ตัวเอง
    private final WebClient client = WebClient.create("http://localhost:8080");

    // ══════════════════════════════════════════════════════
    // ✅ ตัวอย่างที่ทำเสร็จแล้ว — ศึกษาแล้วทำ method ที่เหลือ
    // ══════════════════════════════════════════════════════

    /**
     * GET /products/{id} → Mono<Product>
     * ดึง Product 1 รายการจาก server
     */
    public Mono<Product> getProductById(String id) {
        return client.get()
                .uri("/products/{id}", id)
                .retrieve()
                .bodyToMono(Product.class);
    }

    // ══════════════════════════════════════════════════════
    // ❌ TODO: เติม method body ด้านล่างนี้
    // ══════════════════════════════════════════════════════

    /**
     * GET /products → Flux<Product>
     * ดึง Product ทั้งหมดจาก server
     */
    public Flux<Product> getAllProducts() {
        return client.get()
                .uri("/products")
                .retrieve()
                .bodyToFlux(Product.class);
    }

    /**
     * POST /products → Mono<Product>
     * ส่ง Product ใหม่ไปยัง server
     */
    public Mono<Product> createProduct(Product product) {
        return client.post()
                .uri("/products")
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class);
    }

    /**
     * DELETE /products/{id} → Mono<Void>
     * ส่ง request ลบ Product
     */
    public Mono<Void> deleteProduct(String id) {
        return client.delete()
                .uri("/products/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }

    /**
     * GET /products/category/{category} → Flux<Product>
     * ดึง Product ตาม category
     */
    public Flux<Product> getByCategory(String category) {
        return client.get()
                .uri("/products/category/{category}", category)
                .retrieve()
                .bodyToFlux(Product.class);
    }

    /**
     * GET /products/{id}/price → Mono<Double>
     * ดึงราคาหลังส่วนลด แล้ว chain .doOnNext() เพื่อ log ราคาที่ได้
     */
    public Mono<Double> getDiscountedPrice(String id) {
        return client.get()
                .uri("/products/{id}/price", id)
                .retrieve()
                .bodyToMono(Double.class)
                .doOnNext(price -> System.out.println("Price: " + price));
    }
}

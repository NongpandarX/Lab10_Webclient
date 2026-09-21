package com.example.lab10;

import com.example.lab10.model.Product;
import com.example.lab10.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

/**
 * Lab10ApplicationTests — ทดสอบ Reactive code
 *
 * ✅ test findById() ทำเสร็จแล้วเป็นตัวอย่าง
 * ❌ TODO: เพิ่ม test สำหรับ method ที่นักศึกษาทำเอง
 *
 * StepVerifier — วิธีทดสอบ Mono/Flux:
 *   StepVerifier.create(mono/flux)
 *     .expectNext(value)     ← คาดหวังค่าที่ได้
 *     .expectNextCount(n)    ← คาดหวังจำนวน element
 *     .verifyComplete()      ← ยืนยัน onComplete
 *     .verifyError()         ← ยืนยัน onError
 */
@SpringBootTest
class Lab10ApplicationTests {

    @Autowired
    private ProductRepository repository;

    // ══════════════════════════════════════════════════════
    // ✅ ตัวอย่าง test — ศึกษาแล้วเพิ่ม test เอง
    // ══════════════════════════════════════════════════════

    @Test
    void contextLoads() {
        // Spring Application Context โหลดสำเร็จ
    }

    @Test
    void testFindById_found() {
        // ✅ ตัวอย่าง: ทดสอบ findById ที่พบข้อมูล
        StepVerifier.create(repository.findById("1"))
                .expectNextMatches(p -> p.getName().contains("iPhone"))
                .verifyComplete();
    }

    @Test
    void testFindById_notFound() {
        // ✅ ตัวอย่าง: ทดสอบ findById ที่ไม่พบข้อมูล
        StepVerifier.create(repository.findById("999"))
                .verifyComplete(); // Mono.empty() → onComplete ทันที
    }

    // ══════════════════════════════════════════════════════
    // ❌ TODO: เพิ่ม test ด้านล่างนี้
    // ══════════════════════════════════════════════════════

    @Test
    void testFindAll() {
        StepVerifier.create(repository.findAll())
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void testSave() {
        Product newProduct = new Product("99", "Test Headphone", "Accessories", "Sony", 10, 4500.0, "NONE");
        StepVerifier.create(repository.save(newProduct))
                .expectNextMatches(p -> p.getId().equals("99") && p.getName().equals("Test Headphone"))
                .verifyComplete();
        // Clean up so other tests expecting 3 initial items won't be affected
        StepVerifier.create(repository.deleteById("99"))
                .verifyComplete();
    }

    @Autowired
    private com.example.lab10.service.ProductService service;

    @Test
    void testFindByCategory() {
        StepVerifier.create(repository.findByCategory("Electronics"))
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void testServiceGetById_notFound() {
        StepVerifier.create(service.getById("unknown-id"))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().contains("Product not found: unknown-id"))
                .verify();
    }

    @Test
    void testServiceSave_autoGenerateId() {
        Product p = new Product(null, "Keyboard", "Accessories", "Logitech", 5, 2500.0, "NONE");
        StepVerifier.create(service.save(p))
                .expectNextMatches(saved -> saved.getId() != null && !saved.getId().isBlank())
                .verifyComplete();
        if (p.getId() != null) {
            repository.deleteById(p.getId()).block();
        }
    }

    @Test
    void testServiceGetDiscountedPrice() {
        // ID "1" has price 39900.0 and MEMBER discount (10% off -> 35910.0)
        StepVerifier.create(service.getDiscountedPrice("1"))
                .expectNext(35910.0)
                .verifyComplete();
    }
}

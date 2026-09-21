package com.example.lab10.repository;

import com.example.lab10.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ProductRepository — In-memory Reactive Repository
 *
 * ✅ โครงสร้างและ annotation ครบแล้ว
 * ❌ TODO: เติม method body ให้ครบทุก method
 *
 * ใช้ ConcurrentHashMap เป็น in-memory storage
 * (ไม่ต่อ Database — เน้นฝึก Mono/Flux)
 *
 * Hint:
 *   - Mono.just(value)          คืนค่าเดียว
 *   - Mono.empty()              คืนเปล่า
 *   - Flux.fromIterable(list)   คืนหลายค่าจาก collection
 */
public class ProductRepository {

    // ── In-memory storage ────────────────────────────────
    private final Map<String, Product> store = new ConcurrentHashMap<>();

    // ── Constructor: ใส่ข้อมูลตัวอย่าง ──────────────────
    public ProductRepository() {
        store.put("1", new Product("1", "iPhone 15 Pro (673380042-8 SEC 2)",
                "Electronics", "Apple", 50, 39900.0, "MEMBER"));
        store.put("2", new Product("2", "MacBook Air M3",
                "Electronics", "Apple", 20, 49900.0, "NONE"));
        store.put("3", new Product("3", "Samsung Galaxy S24",
                "Electronics", "Samsung", 30, 29900.0, "SEASONAL"));
    }

    // ── 1. หา Product 1 รายการ ───────────────────────────
    /**
     * TODO: คืน Mono<Product> จาก store โดยใช้ id
     *       ถ้าไม่พบให้คืน Mono.empty()
     *
     * Hint: store.get(id) คืน Product หรือ null
     *       ถ้า null ให้ใช้ Mono.empty()
     *       ถ้ามีค่าให้ใช้ Mono.just(product)
     */
    public Mono<Product> findById(String id) {
        Product product = store.get(id);
        return product != null ? Mono.just(product) : Mono.empty();
    }

    // ── 2. หา Product ทั้งหมด ────────────────────────────
    /**
     * คืน Flux<Product> ของทุกรายการใน store
     */
    public Flux<Product> findAll() {
        return Flux.fromIterable(store.values());
    }

    // ── 3. บันทึก Product ────────────────────────────────
    /**
     * บันทึก product ลง store แล้วคืน Mono<Product>
     */
    public Mono<Product> save(Product product) {
        store.put(product.getId(), product);
        return Mono.just(product);
    }

    // ── 4. ลบ Product ────────────────────────────────────
    /**
     * ลบ product จาก store แล้วคืน Mono<Void>
     */
    public Mono<Void> deleteById(String id) {
        store.remove(id);
        return Mono.empty();
    }

    // ── 5. กรองตาม category ──────────────────────────────
    /**
     * คืน Flux<Product> ที่ category ตรงกัน
     */
    public Flux<Product> findByCategory(String category) {
        return findAll()
                .filter(p -> p.getCategory() != null && p.getCategory().equalsIgnoreCase(category));
    }
}

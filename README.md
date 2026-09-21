# ⚡ Lab 10: Spring WebFlux & WebClient

**วิชา:** CP353002 หลักการออกแบบและพัฒนาซอฟต์แวร์  
**เรื่อง:** Reactive Programming · Spring WebFlux · Mono · Flux · WebClient  
**อ้างอิง:**
- [Guide to Spring WebFlux — Baeldung](https://www.baeldung.com/spring-webflux)
- [Introduction to Reactive Programming — Project Reactor](https://projectreactor.io/docs/core/release/reference/reactiveProgramming.html)

---

## 📋 วัตถุประสงค์

1. นักศึกษาอธิบายได้ว่า Reactive Programming คืออะไร และแตกต่างจาก Blocking I/O อย่างไร
2. นักศึกษาเข้าใจ **Mono\<T\>** และ **Flux\<T\>** และรู้ว่าควรใช้แบบไหนในสถานการณ์ใด
3. นักศึกษาสร้าง REST API ด้วย **Spring WebFlux** และ `@RestController` ได้
4. นักศึกษาใช้ **WebClient** เรียก HTTP API แบบ non-blocking ได้
5. นักศึกษาใช้ Operators สำคัญ (`map`, `flatMap`, `filter`, `subscribe`) ได้ถูกต้อง

---

## 🧠 รู้หมือไหร่

### 1. Reactive Programming คืออะไร?

> Reactive programming คือการเขียนโปรแกรมแบบ **asynchronous** ที่ตอบสนองต่อ **data stream** โดยไม่บล็อก thread รอผลลัพธ์

| | Blocking (แบบเดิม) | Reactive |
|---|---|---|
| Thread | รอจนกว่าจะได้ผล | ไม่รอ — ไปทำงานอื่น |
| Scalability | 1 thread per request | Few threads, many requests |
| Return type | `String result` | `Mono<String> result` |
| เหมาะกับ | CRUD ทั่วไป | High-concurrency, streaming |

### 2. Publisher → Subscriber Pattern

```
Publisher ──→ [Operator] ──→ [Operator] ──→ Subscriber
(Source)      (transform)    (filter)       (Consumer)
```

**3 Signal ที่ Publisher ส่งได้:**

| Signal | ความหมาย |
|---|---|
| `onNext(T)` | ส่ง data item (เรียกได้ 0 ถึง N ครั้ง) |
| `onError(e)` | เกิด error — stream จบทันที |
| `onComplete()` | stream จบปกติ ไม่มี item อีกแล้ว |

> ⚠️ **กฎสำคัญ:** ไม่มีอะไรเกิดขึ้นจนกว่าจะเรียก `subscribe()` — Publisher เป็นแค่ "blueprint"

---

### 3. Mono\<T\> — 0 หรือ 1 ค่า

```java
// สร้าง Mono
Mono<String> m1 = Mono.just("Hello");          // มีค่า
Mono<String> m2 = Mono.empty();                // ว่าง
Mono<Product> p  = repo.findById("1");         // จาก Repository

// Operators
mono.map(p -> p.getName())                     // แปลงค่า (sync)
    .flatMap(p -> repo.save(p))                // async transform
    .defaultIfEmpty("Not Found")               // fallback ถ้าว่าง
    .onErrorReturn(new Product())              // fallback เมื่อ error
    .subscribe(System.out::println);           // เริ่ม execute
```

**ใช้ Mono เมื่อ:** `findById`, `save`, `update`, `delete` (คืนค่าเดียว)

---

### 4. Flux\<T\> — 0 ถึง N ค่า

```java
// สร้าง Flux
Flux<String>  f1 = Flux.just("A", "B", "C");  // จากค่าตายตัว
Flux<Product> f2 = repo.findAll();             // จาก Repository
Flux<Integer> f3 = Flux.range(1, 10);          // range 1-10

// Operators
flux.map(p -> p.getName())                     // แปลงแต่ละ element
    .filter(name -> name.startsWith("A"))      // กรอง
    .flatMap(id -> repo.findById(id))          // async แปลงแต่ละตัว
    .take(5)                                   // เอาแค่ 5 ตัวแรก
    .collectList()                             // รวมเป็น Mono<List<T>>
    .subscribe(System.out::println);           // เริ่ม execute
```

**ใช้ Flux เมื่อ:** `findAll`, `search`, event stream (คืนหลายค่า)

---

### 5. Rule of Thumb

| คืน | ใช้ |
|---|---|
| 1 ผลลัพธ์ | `Mono<T>` |
| หลายผลลัพธ์ | `Flux<T>` |
| ไม่มีผลลัพธ์ (side-effect) | `Mono<Void>` |

---

### 6. Spring WebFlux — @RestController

```java
@RestController
@RequestMapping("/products")
public class ProductController {

    // GET /products/{id} → Mono<Product>
    @GetMapping("/{id}")
    public Mono<Product> getById(@PathVariable String id) {
        return service.getById(id);
    }

    // GET /products → Flux<Product>
    @GetMapping
    public Flux<Product> getAll() {
        return service.getAll();
    }
}
```

---

### 7. WebClient — เรียก HTTP API แบบ Reactive

```java
WebClient client = WebClient.create("http://localhost:8080");

// GET 1 รายการ → Mono
Mono<Product> p = client.get()
    .uri("/products/{id}", "1")
    .retrieve()
    .bodyToMono(Product.class);

// GET หลายรายการ → Flux
Flux<Product> all = client.get()
    .uri("/products")
    .retrieve()
    .bodyToFlux(Product.class);

// POST ส่งข้อมูล
Mono<Product> saved = client.post()
    .uri("/products")
    .bodyValue(product)
    .retrieve()
    .bodyToMono(Product.class);
```

---

### 8. Operators สรุป

| Operator | ทำอะไร | ตัวอย่าง |
|---|---|---|
| `map` | แปลง T → R (sync) | `.map(p -> p.getName())` |
| `flatMap` | แปลง T → Publisher\<R\> (async) | `.flatMap(id -> repo.findById(id))` |
| `filter` | กรอง element | `.filter(p -> p.getPrice() > 0)` |
| `take` | เอาแค่ N ตัวแรก | `.take(5)` |
| `collectList` | รวมเป็น `Mono<List<T>>` | `.collectList()` |
| `defaultIfEmpty` | fallback ถ้าว่าง | `.defaultIfEmpty("N/A")` |
| `onErrorReturn` | fallback เมื่อ error | `.onErrorReturn(new Product())` |
| `subscribe` | เริ่ม execute stream | `.subscribe(System.out::println)` |

---

## 🛠️ โครงสร้างโปรเจกต์ Template

```
src/main/java/com/example/lab10/
├── Lab10Application.java          ← ✅ Application Entry Point
├── AppConfig.java                 ← ✅ Spring Bean Configuration
├── model/
│   └── Product.java               ← ✅ Domain Model
├── repository/
│   └── ProductRepository.java     ← ✅ In-memory Reactive Repository (ครบ 5 methods)
├── service/
│   └── ProductService.java        ← ✅ Business Logic Layer (ครบ 6 methods)
├── controller/
│   └── ProductController.java     ← ✅ Reactive REST Controller (ครบ 6 endpoints)
└── client/
    └── ProductWebClient.java      ← ✅ Reactive HTTP Client (ครบ 6 methods)
```

**Endpoints ทั้งหมด:**

| Method | URL | Return | คำอธิบาย | สถานะ |
|---|---|---|---|---|
| GET | `/products` | `Flux<Product>` | ดึงรายการสินค้าทั้งหมด | ✅ ใช้งานได้ |
| GET | `/products/{id}` | `Mono<Product>` | ค้นหาสินค้าตาม ID | ✅ ใช้งานได้ |
| POST | `/products` | `Mono<Product>` | เพิ่มสินค้าใหม่ (auto UUID ถ้าไม่มี ID) | ✅ ใช้งานได้ |
| DELETE | `/products/{id}` | `Mono<Void>` | ลบสินค้าตาม ID | ✅ ใช้งานได้ |
| GET | `/products/category/{category}` | `Flux<Product>` | กรองสินค้าตามหมวดหมู่ | ✅ ใช้งานได้ |
| GET | `/products/{id}/price` | `Mono<Double>` | คำนวณราคาหลังหักส่วนลด | ✅ ใช้งานได้ |

---

## ⚙️ ขั้นตอนการเตรียมตัวก่อนรัน

1. **แก้ไขข้อมูลนักศึกษาใน `pom.xml`**:
   แก้ `<artifactId>` ให้เป็นรหัสนักศึกษาและ section ของตนเอง:
   ```xml
   <artifactId>lab10-67XXXXXXXX-X-sec1</artifactId>
   ```

2. **แก้ไขชื่อนักศึกษาในข้อมูลเริ่มต้น (`ProductRepository.java`)**:
   ใน Constructor ของ `ProductRepository.java` ให้เปลี่ยนเป็นชื่อและรหัสนักศึกษาของตนเอง:
   ```java
   store.put("1", new Product("1", "iPhone 15 Pro (รหัสนักศึกษา ชื่อ-นามสกุล SEC 1)",
           "Electronics", "Apple", 50, 39900.0, "MEMBER"));
   ```

---

## 🚀 วิธีรันโปรเจกต์และทดสอบ

### 1. ทดสอบ Unit & Integration Tests ด้วย Maven
ตรวจสอบความถูกต้องของ Reactive Stream (`StepVerifier`) และ Web Layer:
```bash
mvn clean test
```

### 2. สั่งรัน Spring Boot Application (Port 8080)
```bash
mvn spring-boot:run
```
หรือรันไฟล์ `.jar` ที่ build แล้ว:
```bash
mvn package -DskipTests
java -jar target/lab10-*.jar
```

---

## 🧪 คำสั่งทดสอบ Endpoints ทั้งหมด (Curl & PowerShell)

เมื่อเปิดเซิร์ฟเวอร์เรียบร้อย (`http://localhost:8080`) สามารถทดสอบได้ดังนี้:

### 1) ดึงสินค้าทั้งหมด (GET /products)
- **Browser:** [http://localhost:8080/products](http://localhost:8080/products)
- **PowerShell:**
  ```powershell
  Invoke-RestMethod -Uri "http://localhost:8080/products" -Method Get | ConvertTo-Json
  ```
- **cURL:**
  ```bash
  curl -X GET http://localhost:8080/products
  ```

### 2) ค้นหาสินค้าตาม ID (GET /products/{id})
- **Browser:** [http://localhost:8080/products/1](http://localhost:8080/products/1)
- **PowerShell:**
  ```powershell
  Invoke-RestMethod -Uri "http://localhost:8080/products/1" -Method Get | ConvertTo-Json
  ```
- **cURL:**
  ```bash
  curl -X GET http://localhost:8080/products/1
  ```

### 3) เพิ่มสินค้าใหม่ (POST /products)
- **PowerShell:**
  ```powershell
  $body = @{
      name = "AirPods Pro (67XXXXXXXX-X)"
      category = "Electronics"
      brand = "Apple"
      stock = 25
      price = 8990.0
      discountType = "MEMBER"
  } | ConvertTo-Json

  Invoke-RestMethod -Uri "http://localhost:8080/products" -Method Post -Body $body -ContentType "application/json" | ConvertTo-Json
  ```
- **cURL:**
  ```bash
  curl -X POST http://localhost:8080/products -H "Content-Type: application/json" -d "{\"name\":\"AirPods Pro\",\"category\":\"Electronics\",\"brand\":\"Apple\",\"stock\":25,\"price\":8990.0,\"discountType\":\"MEMBER\"}"
  ```

### 4) ค้นหาสินค้าตามหมวดหมู่ (GET /products/category/{category})
- **Browser:** [http://localhost:8080/products/category/Electronics](http://localhost:8080/products/category/Electronics)
- **PowerShell:**
  ```powershell
  Invoke-RestMethod -Uri "http://localhost:8080/products/category/Electronics" -Method Get | ConvertTo-Json
  ```
- **cURL:**
  ```bash
  curl -X GET http://localhost:8080/products/category/Electronics
  ```

### 5) คำนวณราคาหลังส่วนลด (GET /products/{id}/price)
- **Browser:** [http://localhost:8080/products/1/price](http://localhost:8080/products/1/price)
- **PowerShell:**
  ```powershell
  Invoke-RestMethod -Uri "http://localhost:8080/products/1/price" -Method Get
  ```
- **cURL:**
  ```bash
  curl -X GET http://localhost:8080/products/1/price
  ```

### 6) ลบสินค้า (DELETE /products/{id})
- **PowerShell:**
  ```powershell
  Invoke-RestMethod -Uri "http://localhost:8080/products/2" -Method Delete
  ```
- **cURL:**
  ```bash
  curl -X DELETE http://localhost:8080/products/2
  ```

---

## 📄 โครงร่างและไฟล์รายงาน (DOCX & PDF)

ในโปรเจกต์นี้มีไฟล์โครงร่างรายงานเตรียมไว้ให้เรียบร้อยแล้ว:
- 📁 **`Lab10_Report_Template.docx`**: ไฟล์ Word พร้อมจัดหน้า โครงสร้างหัวข้อ ตารางเปรียบเทียบ คำอธิบายโค้ด และกรอบใส่รูปภาพ Screenshot สำหรับส่งงาน
- 📁 **`REPORT_TEMPLATE.md`**: ไฟล์เนื้อหารายงานฉบับเต็มในรูปแบบ Markdown

**สิ่งที่ต้องใส่ในรายงานก่อนแปลงเป็น PDF (`Lab10_xxxxSec#.pdf`):**
1. **บทนำ & ทฤษฎี:**
   - Reactive Programming vs Blocking I/O (เปรียบเทียบ Thread Model, Throughput, และ Resource Utilization)
   - Mono vs Flux (Publisher Types และข้อแตกต่างในการนำไปใช้)
   - WebClient (อธิบาย Method Chaining: `.get()`, `.uri()`, `.retrieve()`, `.bodyToMono/Flux()`)
2. **อธิบายซอร์สโค้ด:**
   - `ProductRepository.java`
   - `ProductService.java`
   - `ProductController.java`
   - `ProductWebClient.java`
3. **รูปภาพ Screenshot ผลการทดสอบ:**
   - ภาพรันคำสั่ง `mvn clean test` (ผ่าน 100%)
   - ภาพทดสอบ GET /products (เห็นชื่อและรหัสนักศึกษาใน JSON)
   - ภาพทดสอบ GET /products/1
   - ภาพทดสอบ POST /products
   - ภาพทดสอบ GET /products/category/Electronics
   - ภาพทดสอบ GET /products/1/price
   - ภาพทดสอบ DELETE /products/{id}

---

## 📝 รายการ Checklist สำหรับการส่งงาน

- [ ] **GitHub Repository** — ชื่อ `lab10-{รหัสนักศึกษา}-sec{section}` พร้อม commit history ที่ชัดเจน
- [ ] **Code ครบทุก TODO** — Repository, Service, Controller, WebClient
- [ ] **ผลลัพธ์ใน response มีชื่อและรหัสนักศึกษา** (ใน Product name ที่ seed ไว้)
- [ ] **Screenshot** ผลทดสอบครบทุก Endpoint ผ่าน Browser / Postman / PowerShell
- [ ] **ไฟล์ PDF** ชื่อ `Lab10_{รหัสนักศึกษา}Sec{section}.pdf` (บันทึกจาก Word/DOCX)

---

## 🔗 แหล่งอ้างอิง

- [Spring WebFlux Guide — Baeldung](https://www.baeldung.com/spring-webflux)
- [Introduction to Reactive Programming — Project Reactor](https://projectreactor.io/docs/core/release/reference/reactiveProgramming.html)
- [WebClient Reference — Spring Docs](https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html)

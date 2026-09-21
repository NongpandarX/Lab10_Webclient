package com.example.lab10.model;

/**
 * Product — Domain Model สำหรับ Lab 10
 *
 * ✅ ไฟล์นี้เตรียมไว้ให้ครบแล้ว ไม่ต้องแก้ไข
 *    ยกเว้นต้องการเพิ่ม field เอง
 */
public class Product {

    private String id;
    private String name;
    private String category;
    private String brand;
    private Integer stock;
    private Double price;
    private String discountType; // "NONE" | "MEMBER" | "SEASONAL"

    // ── Constructors ──────────────────────────────────────
    public Product() {}

    public Product(String id, String name, String category,
                   String brand, Integer stock, Double price,
                   String discountType) {
        this.id          = id;
        this.name        = name;
        this.category    = category;
        this.brand       = brand;
        this.stock       = stock;
        this.price       = price;
        this.discountType = discountType;
    }

    // ── Getters & Setters ─────────────────────────────────
    public String  getId()           { return id; }
    public void    setId(String id)  { this.id = id; }

    public String  getName()              { return name; }
    public void    setName(String name)   { this.name = name; }

    public String  getCategory()                  { return category; }
    public void    setCategory(String category)   { this.category = category; }

    public String  getBrand()               { return brand; }
    public void    setBrand(String brand)   { this.brand = brand; }

    public Integer getStock()               { return stock; }
    public void    setStock(Integer stock)  { this.stock = stock; }

    public Double  getPrice()               { return price; }
    public void    setPrice(Double price)   { this.price = price; }

    public String  getDiscountType()                      { return discountType; }
    public void    setDiscountType(String discountType)   { this.discountType = discountType; }

    // ── Helper: คำนวณราคาหลังส่วนลด ──────────────────────
    public Double getDiscountedPrice() {
        if (price == null) return 0.0;
        return switch (discountType == null ? "NONE" : discountType) {
            case "MEMBER"   -> price * 0.90;  // ลด 10%
            case "SEASONAL" -> price * 0.80;  // ลด 20%
            default         -> price;
        };
    }

    @Override
    public String toString() {
        return "Product{id='" + id + "', name='" + name
                + "', price=" + price + ", discounted=" + getDiscountedPrice() + "}";
    }
}

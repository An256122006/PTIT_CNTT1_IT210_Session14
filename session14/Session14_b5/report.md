📄 PHẦN 1: THIẾT KẾ KIẾN TRÚC

🧱 1. Các Module & Entity

🔹 Entity chính

1. User

* id
* name
* wallet_id

⸻

2. Wallet

* id
* balance

👉 Quan hệ:

* 1 User → 1 Wallet

⸻

3. Vendor

* id
* name

⸻

4. Product

* id
* name
* price
* stock
* vendor_id

👉 Quan hệ:

* 1 Vendor → nhiều Product

⸻

5. Order

* id
* user_id
* total_amount
* status (PENDING, SUCCESS, FAILED)

⸻

6. OrderDetail

* id
* order_id
* product_id
* quantity
* price

👉 Quan hệ:

* 1 Order → nhiều OrderDetail

⸻

🔗 Quan hệ tổng thể

```text

User → Wallet
Vendor → Product
Order → OrderDetail → Product

```

2. Data Flow (Luồng dữ liệu)

🎯 Flow thanh toán đa Vendor

Bước 1: User chọn sản phẩm (Menu)

→ Gửi request vào OrderService

⸻


Bước 2: OrderService xử lý
```java


@Transactional
public void checkout(List<Item> items) {
    // 1. Tính tổng tiền
    // 2. Kiểm tra số dư ví
    // 3. Tạo Order (PENDING)
    
    for (Item item : items) {
        vendorService.processVendor(item);
    }

    // 4. Trừ tiền ví
    // 5. Update Order SUCCESS
}

```

Bước 3: VendorService (Transaction con)

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void processVendor(Item item) {
    // check stock
    // trừ kho
    // tạo OrderDetail
}
```

🚀 PHẦN 2: PHÂN TÍCH RỦI RO (EDGE CASES)

❗ Case 1: Vendor A OK, Vendor B hết hàng

👉 Vấn đề:

* Nếu không rollback → mất tiền sai

👉 Giải pháp:


```java

if (stock < quantity) {
    throw new RuntimeException("Out of stock");
}
```

❗ Case 2: Database mất kết nối giữa chừng

👉 Vấn đề:

* Transaction bị treo
* Data inconsistency

👉 Giải pháp:

* Dùng Transaction rollback tự động
* Retry mechanism (nâng cao)

⸻

❗ Case 3: User nhập sai dữ liệu

👉 Ví dụ:

* nhập chữ thay vì số
* nhập số âm

👉 Giải pháp:

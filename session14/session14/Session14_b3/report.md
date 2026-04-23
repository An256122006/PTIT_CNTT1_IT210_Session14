
Phần 1 phân tích thiết kế



1 Input / Output

Input

- productId(Long)
- quantity (int, thường = 1)
- userId(giả lập)

Output

Trường hợp : Kết quả Còn hàng  -> Tạo order + trừ kho

Hết hàng : “Hết hàng”  

Tranh chấp (concurrency) -> ️ “Hệ thống đang bận, vui lòng thử lại”

2. Vấn đề Over-selling

Tình huống nguy hiểm:

Kho còn: 1

Thread A : đọc stock = 1 : trừ còn 0

Thread B: đọc stock = 1 : trừ còn 0



Kết quả:

* bán cho 2 người 
* thực tế chỉ có 1 sản phẩm 





3.  Giải pháp đề xuất

 Chọn: Pessimistic Lock (Khuyến nghị cho Flash Sale)

 Vì sao?

* Flash sale = cực nhiều request cùng lúc
* Optimistic sẽ retry nhiều → lag
* Pessimistic lock:
  => khóa row ngay khi đọc

Flow dạng step

```text

START
  ↓
BEGIN TRANSACTION
  ↓
LOCK PRODUCT (FOR UPDATE)
  ↓
CHECK STOCK
  ↓
[stock <= 0] → THROW "OUT OF STOCK"
  ↓
DECREASE STOCK
  ↓
CREATE ORDER
  ↓
COMMIT
  ↓
END

```











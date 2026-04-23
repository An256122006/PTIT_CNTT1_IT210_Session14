📄 PHẦN 1: PHÂN TÍCH & ĐỀ XUẤT

1. I/O (Input / Output)

🔹 Input

* userId
* productId
* quantity
* action:
    * addToCart
    * checkout
    * cancel
    * timeout

⸻

🔹 Output
* Trạng thái đơn hàng:
    * PENDING: Đang giữ hàng
    * PAID: Đã thanh toán
    * CANCELLED: Người dùng hủy
    * EXPIRED: Hết thời gian giữ
* Số lượng tồn kho được cập nhật chính xác

⸻

2. Flow nghiệp vụ chuẩn

🔄 Luồng xử lý

1. User thêm sản phẩm vào giỏ
   → Chưa trừ kho
2. User click Checkout
    * Tạo đơn hàng với trạng thái PENDING
    * Thực hiện reserve stock (giữ hàng)
    * Lưu thời gian hết hạn: now + 15 phút
3. Sau đó hệ thống xử lý:
    * ✅ Nếu thanh toán thành công:
        * Trạng thái → PAID
        * Trừ kho vĩnh viễn
    * ❌ Nếu hết 15 phút hoặc user không thanh toán:
        * Trạng thái → EXPIRED
        * Hoàn lại kho

⸻

🚀 GIẢI PHÁP 1: Transaction + Order PENDING

💡 Ý tưởng

* Dùng transaction DB để giữ hàng ngay khi checkout
* Không giữ transaction lâu (tránh deadlock)

⸻

⚙️ Cách triển khai

1. Khi Checkout

* Kiểm tra tồn kho
* Trừ stock tạm thời
* Tạo order PENDING

⸻

2. Khi thanh toán

* Update order → PAID
* Không cần xử lý thêm stock (đã trừ trước)

⸻

3. Khi hết hạn

* Cron job hoặc API check:
    * Tìm order PENDING quá hạn
    * Update → EXPIRED
    * Cộng lại stock

⸻

⚠️ Nhược điểm

* Dễ xảy ra deadlock khi nhiều request
* Khó scale khi traffic cao
* Phụ thuộc nhiều vào DB

⸻

🚀 GIẢI PHÁP 2: Reservation + Scheduled Task (Khuyến nghị)

💡 Ý tưởng

* Không trừ kho ngay
* Tạo bảng trung gian để giữ hàng (reservation)
* Dùng job chạy nền để xử lý timeout

⸻

🗄️ Thiết kế bảng

reservation

* id
* product_id
* quantity
* expired_at
* status:
    * ACTIVE
    * DONE
    * RELEASED

⸻

⚙️ Cách hoạt động

1. Checkout

* Kiểm tra stock khả dụng
* Tạo reservation (chưa trừ kho thật)

⸻

2. Scheduler (chạy mỗi 1 phút)

* Tìm reservation hết hạn
* Cập nhật:
    * status → RELEASED
    * Trả lại stock

⸻

3. Khi thanh toán

* status → DONE
* Trừ kho chính thức

⸻

🔥 Ưu điểm

* Không lock DB lâu
* Tránh deadlock
* Dễ scale
* Phù hợp hệ thống lớn

⸻

⚠️ XỬ LÝ CÁC TRƯỜNG HỢP ĐẶC BIỆT

❗ Product bị xóa

* Không xóa cứng → dùng is_deleted
* Nếu đã xóa:
    * Bỏ qua khi release stock

⸻

❗ Session timeout

* Không phụ thuộc session
* Dựa vào expired_at trong DB

⸻

🏆 KẾT LUẬN

👉 Chọn: Giải pháp 2 – Reservation + Scheduled Task

Lý do:

* Tránh long transaction
* Giảm deadlock
* Phù hợp hệ thống thương mại điện tử có traffic lớn
* Dễ mở rộng (microservice, Redis, queue)


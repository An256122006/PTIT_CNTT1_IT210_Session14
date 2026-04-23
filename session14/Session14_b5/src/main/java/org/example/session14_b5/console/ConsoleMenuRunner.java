package org.example.session14_b5.console;

import org.example.session14_b5.exception.CheckoutException;
import org.example.session14_b5.exception.ValidationException;
import org.example.session14_b5.model.CheckoutItem;
import org.example.session14_b5.model.CheckoutReceipt;
import org.example.session14_b5.model.ProductCatalogItem;
import org.example.session14_b5.model.RevenueStats;
import org.example.session14_b5.model.Wallet;
import org.example.session14_b5.service.CatalogService;
import org.example.session14_b5.service.OrderService;
import org.example.session14_b5.service.RevenueService;
import org.example.session14_b5.service.WalletService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "app.console", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ConsoleMenuRunner implements CommandLineRunner {

    private static final long DEMO_USER_ID = 1L;

    private final WalletService walletService;
    private final CatalogService catalogService;
    private final OrderService orderService;
    private final RevenueService revenueService;
    private final ConsoleInputReader inputReader = new ConsoleInputReader();

    public ConsoleMenuRunner(WalletService walletService,
                             CatalogService catalogService,
                             OrderService orderService,
                             RevenueService revenueService) {
        this.walletService = walletService;
        this.catalogService = catalogService;
        this.orderService = orderService;
        this.revenueService = revenueService;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Rikkei Mall | Thanh toán đa bên ===");
        System.out.println("Demo sử dụng user ID: " + DEMO_USER_ID);
        boolean running = true;
        while (running) {
            printMenu();
            String choice = inputReader.readLine("Chọn chức năng: ");
            try {
                running = switch (choice) {
                    case "1" -> {
                        handleDeposit();
                        yield true;
                    }
                    case "2" -> {
                        handleShowCatalog();
                        yield true;
                    }
                    case "3" -> {
                        handleCheckout();
                        yield true;
                    }
                    case "4" -> {
                        handleRevenueStats();
                        yield true;
                    }
                    case "5" -> false;
                    default -> {
                        System.out.println("Lựa chọn không hợp lệ. Vui lòng nhập từ 1 đến 5.");
                        yield true;
                    }
                };
            } catch (IllegalStateException ex) {
                System.out.println(ex.getMessage());
                running = false;
            } catch (CheckoutException ex) {
                System.out.println("Thanh toán thất bại: " + ex.getMessage());
            } catch (RuntimeException ex) {
                System.out.println("Lỗi hệ thống: " + ex.getMessage());
            }
        }
        System.out.println("Tạm biệt!");
    }

    private void printMenu() {
        Wallet wallet = walletService.getWalletByUserId(DEMO_USER_ID);
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println("Ví hiện tại: " + wallet.balance());
        System.out.println("1. Nạp tiền ví");
        System.out.println("2. Xem danh sách sản phẩm & kho");
        System.out.println("3. Thanh toán đơn hàng đa bên");
        System.out.println("4. Thống kê doanh thu");
        System.out.println("5. Thoát");
        System.out.println("----------------------------------------");
    }

    private void handleDeposit() {
        BigDecimal amount = inputReader.readPositiveBigDecimal("Nhập số tiền cần nạp: ");
        Wallet wallet = walletService.deposit(DEMO_USER_ID, amount);
        System.out.println("Nạp tiền thành công. Số dư mới: " + wallet.balance());
    }

    private void handleShowCatalog() {
        List<ProductCatalogItem> catalog = catalogService.getCatalog();
        System.out.println("\n=== Danh sách sản phẩm & kho ===");
        for (ProductCatalogItem item : catalog) {
            System.out.printf("[%d] %s | Vendor: %s | Giá: %s | Tồn kho: %d%n",
                    item.id(), item.name(), item.vendorName(), item.price(), item.stock());
        }
    }

    private void handleCheckout() {
        handleShowCatalog();
        List<String> lines = inputReader.readItemLines();
        List<CheckoutItem> items = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length != 2) {
                throw new ValidationException("Sai định dạng. Hãy nhập theo mẫu productId:quantity");
            }
            try {
                long productId = Long.parseLong(parts[0].trim());
                int quantity = Integer.parseInt(parts[1].trim());
                if (productId <= 0 || quantity <= 0) {
                    throw new NumberFormatException();
                }
                items.add(new CheckoutItem(productId, quantity));
            } catch (NumberFormatException ex) {
                throw new ValidationException("Mỗi dòng phải là hai số hợp lệ, ví dụ 1:2");
            }
        }

        CheckoutReceipt receipt = orderService.checkout(DEMO_USER_ID, items);
        System.out.println("Thanh toán thành công.");
        System.out.println("Mã đơn hàng: " + receipt.orderId());
        System.out.println("Tổng tiền: " + receipt.totalAmount());
        System.out.println("Số dư ví còn lại: " + receipt.walletBalance());
    }

    private void handleRevenueStats() {
        RevenueStats stats = revenueService.getRevenueStats();
        System.out.println("\n=== Thống kê doanh thu ===");
        System.out.println("Đơn thành công: " + stats.successfulOrders());
        System.out.println("Đơn thất bại: " + stats.failedOrders());
        System.out.println("Tổng doanh thu: " + stats.totalRevenue());
    }
}


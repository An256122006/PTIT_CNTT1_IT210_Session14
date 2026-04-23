package org.example.session14_b5.console;

import org.example.session14_b5.exception.ValidationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ConsoleInputReader {

    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    public String readLine(String prompt) {
        System.out.print(prompt);
        try {
            String line = reader.readLine();
            if (line == null) {
                throw new IllegalStateException("Đã đóng luồng nhập, thoát ứng dụng");
            }
            return line.trim();
        } catch (IOException ex) {
            throw new IllegalStateException("Không thể đọc dữ liệu từ console", ex);
        }
    }

    public long readPositiveLong(String prompt) {
        while (true) {
            String text = readLine(prompt);
            try {
                long value = Long.parseLong(text);
                if (value <= 0) {
                    System.out.println("Giá trị phải lớn hơn 0.");
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Vui lòng nhập một số nguyên hợp lệ.");
            }
        }
    }

    public int readPositiveInt(String prompt) {
        while (true) {
            String text = readLine(prompt);
            try {
                int value = Integer.parseInt(text);
                if (value <= 0) {
                    System.out.println("Giá trị phải lớn hơn 0.");
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Vui lòng nhập một số nguyên hợp lệ.");
            }
        }
    }

    public BigDecimal readPositiveBigDecimal(String prompt) {
        while (true) {
            String text = readLine(prompt);
            try {
                BigDecimal value = new BigDecimal(text);
                if (value.signum() <= 0) {
                    System.out.println("Giá trị phải lớn hơn 0.");
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Vui lòng nhập một số hợp lệ.");
            }
        }
    }

    public List<String> readItemLines() {
        List<String> lines = new ArrayList<>();
        while (true) {
            String line = readLine("Nhập productId:quantity (Enter để kết thúc): ");
            if (line.isBlank()) {
                break;
            }
            lines.add(line);
        }
        if (lines.isEmpty()) {
            throw new ValidationException("Danh sách thanh toán không được để trống");
        }
        return lines;
    }
}


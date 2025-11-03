-- KiotViet Database Initialization Script
-- This script creates initial data for the product management system

USE kiotviet_db;

-- Insert default admin user
INSERT INTO users (username, password, email, full_name, role, active, created_at, updated_at) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVjzieMwkOmANgNOgKQNNBDvAGK', 'admin@kiotviet.local', 'System Administrator', 'ADMIN', true, NOW(), NOW());

-- Insert default categories
INSERT INTO categories (name, description, sort_order, active, created_at, updated_at) VALUES
('Đồ Ăn & Thức Uống', 'Các sản phẩm đồ ăn và thức uống', 1, true, NOW(), NOW()),
('Thời Trang & Phụ Kiện', 'Quần áo, giày dép và phụ kiện thời trang', 2, true, NOW(), NOW()),
('Điện Tử & Điện Lạnh', 'Các thiết bị điện tử và điện lạnh', 3, true, NOW(), NOW()),
('Sức Khỏe & Sắc Đẹp', 'Sản phẩm chăm sóc sức khỏe và làm đẹp', 4, true, NOW(), NOW()),
('Gia Dụng & Nội Thất', 'Đồ gia dụng và nội thất nhà cửa', 5, true, NOW(), NOW()),
('Sách & Văn Phòng Phẩm', 'Sách, báo và văn phòng phẩm', 6, true, NOW(), NOW()),
('Mẹ & Bé', 'Sản phẩm cho mẹ và bé', 7, true, NOW(), NOW()),
('Ô Tô & Xe Máy', 'Phụ tùng và đồ dùng cho ô tô, xe máy', 8, true, NOW(), NOW());

-- Insert sample customers
INSERT INTO customers (customer_code, name, phone_number, email, address, city, district, active, created_at, updated_at) VALUES
('KH000001', 'Nguyễn Văn A', '0901234567', 'nguyenvana@email.com', '123 Nguyễn Huệ, Quận 1', 'TP. Hồ Chí Minh', 'Quận 1', true, NOW(), NOW()),
('KH000002', 'Trần Thị B', '0912345678', 'tranthib@email.com', '456 Lê Lợi, Quận 3', 'TP. Hồ Chí Minh', 'Quận 3', true, NOW(), NOW()),
('KH000003', 'Lê Văn C', '0923456789', 'levanc@email.com', '789 Đồng Khởi, Quận 5', 'TP. Hồ Chí Minh', 'Quận 5', true, NOW(), NOW());

-- Create a stored procedure for generating order numbers
DELIMITER //
CREATE PROCEDURE GenerateOrderNumber()
BEGIN
    DECLARE order_number VARCHAR(20);
    DECLARE date_prefix VARCHAR(8);

    SET date_prefix = DATE_FORMAT(NOW(), '%Y%m%d');

    SELECT CONCAT('HD', date_prefix, LPAD(IFNULL(MAX(CAST(SUBSTRING(order_number, 9) AS UNSIGNED)), 0) + 1, 4, '0'))
    INTO order_number
    FROM orders
    WHERE order_number LIKE CONCAT('HD', date_prefix, '%');

    SELECT order_number;
END //
DELIMITER ;

-- Create a stored procedure for generating customer codes
DELIMITER //
CREATE PROCEDURE GenerateCustomerCode()
BEGIN
    DECLARE customer_code VARCHAR(10);

    SELECT CONCAT('KH', LPAD(IFNULL(MAX(CAST(SUBSTRING(customer_code, 3) AS UNSIGNED)), 0) + 1, 6, '0'))
    INTO customer_code
    FROM customers
    WHERE customer_code LIKE 'KH%';

    SELECT customer_code;
END //
DELIMITER ;
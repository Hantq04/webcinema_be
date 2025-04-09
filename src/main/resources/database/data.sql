-- Insert all values into the database tables

INSERT INTO banners (image_url, title) VALUES
    ('src/home/image/movie_image.jpg', 'Thunderbolt* - Sắp ra mắt');

INSERT INTO foods (description, image, is_active, name_of_food, price) VALUES
    ('Mô tả đồ ăn', 'src/image/food_image.jpg', true, 'Bỏng ngô', 30000),
    ('Mô tả đồ ăn', 'src/image/food_image.jpg', true, 'Coca cola', 20000),
    ('Mô tả đồ ăn', 'src/image/food_image.jpg', true, 'Pepsi', 20000),
    ('Mô tả đồ ăn', 'src/image/food_image.jpg', true, 'Fanta', 20000),
    ('Mô tả đồ ăn', 'src/image/food_image.jpg', true, 'Sprite', 20000),
    ('Mô tả đồ ăn', 'src/image/food_image.jpg', true, 'Combo 2 người', 120000),
    ('Mô tả đồ ăn', 'src/image/food_image.jpg', true, 'Combo 3 người', 185000);

INSERT INTO general_settings (break_time, business_hours, close_time, percent_weekend, time_begin_to_change, open_time) VALUES
    ('12:00:00', 18, '02:00:00', 20, '2025-04-15 00:00:00', '08:00:00');

INSERT INTO movie_types (is_active, movie_type_name) VALUES
    (true, 'Hành động'),
    (true, 'Phiêu lưu'),
    (true, 'Hoạt hình'),
    (true, 'Hài'),
    (true, 'Tội phạm'),
    (true, 'Tài liệu'),
    (true, 'Chính kịch'),
    (true, 'Kỳ ảo'),
    (true, 'Kinh dị'),
    (true, 'Bí ẩn'),
    (true, 'Lãng mạn'),
    (true, 'Khoa học viễn tưởng'),
    (true, 'Giật gân'),
    (true, 'Chiến tranh');

INSERT INTO promotions (description, end_time,is_active, name, percent, quantity, start_time, rank_customer_id, promotion_type) VALUES
    ('Giảm 10% cho đồ ăn đã đặt', '2025-04-30 00:00:00',true, 'Đồ ăn', 10, 10, '2025-04-15 00:00:00', 3, 'Food'),
    ('Giảm 20% cho đồ ăn đã đặt', '2025-04-30 00:00:00',true, 'Đồ ăn', 20, 10, '2025-04-15 00:00:00', 3, 'Food'),
    ('Giảm 30% cho đồ ăn đã đặt', '2025-04-30 00:00:00',true, 'Đồ ăn', 30, 10, '2025-04-15 00:00:00', 2, 'Food');

INSERT INTO rates (code, description) VALUES
    ('G', 'Phù hợp với mọi lứa tuổi. Không có nội dung gây hại.'),
    ('PG', 'Cần có sự hướng dẫn của cha mẹ. Một số nội dung có thể không phù hợp với trẻ nhỏ.'),
    ('PG-13', 'Trẻ em dưới 13 tuổi cần có sự giám sát của cha mẹ. Có thể có nội dung mạnh như bạo lực nhẹ hoặc từ ngữ nhạy cảm.'),
    ('R', 'Hạn chế: Dưới 17 tuổi phải có người lớn đi kèm. Có thể chứa bạo lực mạnh, ngôn ngữ thô tục hoặc nội dung người lớn.'),
    ('NC-17', 'Cấm người dưới 18 tuổi. Nội dung dành riêng cho người trưởng thành, có thể chứa cảnh nóng hoặc bạo lực nặng.');

INSERT INTO seat_status (code, name_status) VALUES
    ('AVAILABLE', 'Ghế trống'),
    ('OCCUPIED', 'Ghế đã được đặt');

INSERT INTO seat_types (name_type) VALUES
    ('Standard'),
    ('VIP'),
    ('Deluxe');

INSERT INTO user_status (code, name) VALUES
    ('ACTIVE', 'Tài khoản đã được kích hoạt'),
    ('INACTIVE', 'Tài khoản chưa được kích hoạt');


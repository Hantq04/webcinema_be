-- Insert all values into the database tables

INSERT INTO banners (image_url, title) VALUES
    ('src/home/image/movie_image.jpg', 'Thunderbolt* - Sắp ra mắt'),
    ('src/home/image/movie_image.jpg', 'Mickey 17 - Đang chiếu')
    ('src/home/image/movie_image.jpg', '28 Year Later - Sắp ra mắt')
    ('src/home/image/movie_image.jpg', 'Địa đạo: Mặt trời trong bóng tối - Đang chiếu');

INSERT INTO cinemas (address, code, description, is_active, name_of_cinema) VALUES
    ('Hà Nội', 'BETA_HN431', 'Số 123 Thanh Xuân', 'Hà Nội', true, 'Beta Thanh Xuân'),
    ('Hồ Chí Minh', 'BETA_HC762', 'Số 123 Hồ Chí Minh', true, 'Beta Hồ Chí Minh'),
    ('Đà Nẵng', 'BETA_DN250', 'Số 123 Đà Nẵng', true, 'Beta Đà Nẵng');

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

INSERT INTO rates (code, description) VALUES
    ('G', 'Phù hợp với mọi lứa tuổi. Không có nội dung gây hại.'),
    ('PG', 'Cần có sự hướng dẫn của cha mẹ. Một số nội dung có thể không phù hợp với trẻ nhỏ.'),
    ('PG-13', 'Trẻ em dưới 13 tuổi cần có sự giám sát của cha mẹ. Có thể có nội dung mạnh như bạo lực nhẹ hoặc từ ngữ nhạy cảm.'),
    ('R', 'Hạn chế: Dưới 17 tuổi phải có người lớn đi kèm. Có thể chứa bạo lực mạnh, ngôn ngữ thô tục hoặc nội dung người lớn.'),
    ('NC-17', 'Cấm người dưới 18 tuổi. Nội dung dành riêng cho người trưởng thành, có thể chứa cảnh nóng hoặc bạo lực nặng.');

INSERT INTO movies (description, director, end_date, hero_image, image, ia_active, language, movie_duration, name, premiere_date, trailer, movie_type_id, rate_id) VALUES
    ('Thunderbolts* là một bộ phim siêu anh hùng sắp ra m...', 'Jake Schreier', '2025-06-01 00:00:00',
     'src/home/image/movie_hero_image.jpg', 'src/home/image/movie_image.jpg', true, 'English', 120,
     'Thunderbolts*', '2025-05-02 00:00:00', 'https://www.youtube.com/watch?v=hUUszE29jS0', 1, 4),
    ('Vào năm 2054, Mickey Barnes và người bạn của anh ấy....', 'Bong Joon-ho', '2025-05-18 00:00:00',
     'src/home/image/movie_hero_image.jpg', 'src/home/image/movie_image.jpg', true, 'English', 137,
     'Mickey 17', '2025-03-19 00:00:00', 'https://www.youtube.com/watch?v=hUUszE29jS0', 12, 3),
    ('28 Năm Sau là một bộ phim kinh dị hậu tận thế...', 'Danny Boyle', '2025-08-20 00:00:00',
     'src/home/image/movie_hero_image.jpg', 'src/home/image/movie_image.jpg', true, 'English', 120,
     '28 Year Later', '2025-06-20 00:00:00', 'https://www.youtube.com/watch?v=mcvLKldPM08', 9, 4),
    ('Địa đạo: Mặt trời trong bóng tối là một bộ phim điện ảnh Việt Nam...', 'Bùi Thạc Chuyên', '2025-05-31 00:00:00',
     'src/home/image/movie_hero_image.jpg', 'src/home/image/movie_image.jpg', true, 'English', 128,
     'Địa đạo: Mặt trời trong bóng tối', '2025-04-04 00:00:00', 'https://www.youtube.com/watch?v=-OGDDtsIBHA', 14, 4);

INSERT INTO rooms (capacity, code, description, is_active, name, type, cinema_id) VALUES
    ('250', 'A1', 'Mô tả', true, 'Beta Thanh Xuân', 'IMAX', 1),
    ('100', 'A2', 'Mô tả', true, 'Beta Thanh Xuân', 'STANDARD', 1),
    ('100', 'A1', 'Mô tả', true, 'Beta Hồ Chí Minh', 'STANDARD', 2),
    ('200', 'A3', 'Mô tả', true, 'Beta Đà Nẵng', 'IMAX', 3);

INSERT INTO rank_customers (description, is_active, name, point) VALUES
    ('Dành cho khách hàng có tổng chi tiêu tích lũy đạt 3.000.000 VND', true, 'VIP', 3000000),
    ('Đối với khách hàng vừa mới đăng ký tài khoản', true, 'Standard', 0);

INSERT INTO promotions (description, end_time,is_active, name, percent, quantity, start_time, rank_customer_id, promotion_type) VALUES
    ('Giảm 10% cho đồ ăn đã đặt', '2025-04-30 00:00:00',true, 'Đồ ăn', 10, 10, '2025-04-15 00:00:00', 2, 'Food'),
    ('Giảm 20% cho đồ ăn đã đặt', '2025-04-30 00:00:00',true, 'Đồ ăn', 20, 10, '2025-04-15 00:00:00', 2, 'Food'),
    ('Giảm 30% cho đồ ăn đã đặt', '2025-04-30 00:00:00',true, 'Đồ ăn', 30, 10, '2025-04-15 00:00:00', 1, 'Food');

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


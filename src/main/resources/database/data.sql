-- Insert all values into the database tables

INSERT INTO banners (image_url, title) VALUES
    ('src/home/image/movie_image.jpg', 'Thunderbolt* - Coming soon')

INSERT INTO foods (description, image, is_active, name_of_food, price) VALUES
    ('food description', 'src/image/food_image.jpg', true, 'Bỏng ngô', 30000)
    ('food description', 'src/image/food_image.jpg', true, 'Coca cola', 20000)
    ('food description', 'src/image/food_image.jpg', true, 'Pepsi', 20000)
    ('food description', 'src/image/food_image.jpg', true, 'Fanta', 20000)
    ('food description', 'src/image/food_image.jpg', true, 'Sprite', 20000)
    ('food description', 'src/image/food_image.jpg', true, 'Combo 2 người', 120000)
    ('food description', 'src/image/food_image.jpg', true, 'Combo 3 người', 185000)

INSERT INTO general_settings (break_time, business_hours, close_time, percent_weekend, time_begin_to_change, open_time) VALUES
    ('12:00:00', 18, '08:00:00', 20, '2025-04-15 00:00:00')

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


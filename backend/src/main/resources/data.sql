-- Produkty
INSERT INTO products (id, name, verified, category)
VALUES (1, 'Truskawki', true, 'FRUIT'),
       (2, 'Maliny', true, 'FRUIT'),
       (3, 'Borówki', true, 'FRUIT'),
       (4, 'Czereśnie', true, 'FRUIT'),
       (5, 'Jabłka', true, 'FRUIT'),
       (6, 'Gruszki', true, 'FRUIT'),
       (7, 'Śliwki', true, 'FRUIT'),
       (8, 'Agrest', true, 'FRUIT'),
       (9, 'Porzeczki', true, 'FRUIT'),
       (10, 'Morele', true, 'FRUIT');

-- Sklepy
INSERT INTO shops (id, name, description, latitude, longitude, city, address, image_url, verified)
VALUES (1, 'Stragan u Zosi', 'Tradycyjny stragan z owocami', 52.23, 21.01, 'Warszawa', 'ul. Wiejska 1', NULL, true),
       (2, 'Owoce Rynku', 'Świeże owoce z Podlasia', 52.24, 21.02, 'Warszawa', 'ul. Rynek 5', NULL, true),
       (3, 'EkoSad', 'Eko-uprawy z Mazur', 52.22, 21.00, 'Warszawa', 'ul. Zielona 12', NULL, true),
       (4, 'Smaki Lata', 'Słodkie owoce z ogródka', 52.21, 21.03, 'Warszawa', 'ul. Owocowa 3', NULL, true),
       (5, 'Zielony Kram', 'Lokalne smaki', 52.20, 21.04, 'Warszawa', 'ul. Kramarska 7', NULL, true),
       (6, 'Ogród Marii', 'Z rodzinnego gospodarstwa', 52.25, 21.05, 'Warszawa', 'ul. Marii 10', NULL, true),
       (7, 'Targowy Raj', 'Codziennie świeże', 52.26, 21.06, 'Warszawa', 'ul. Targowa 15', NULL, true),
       (8, 'Smaczny Koszyk', 'Bez chemii i nawozów', 52.27, 21.07, 'Warszawa', 'ul. Koszykowa 8', NULL, true),
       (9, 'Sadownik', 'Prosto z sadu', 52.28, 21.08, 'Warszawa', 'ul. Sadowa 2', NULL, true),
       (10, 'Owocowy Zakątek', 'Naturalnie i zdrowo', 52.29, 21.09, 'Warszawa', 'ul. Zakątek 11', NULL, true);

-- OwnProduct (5 na każdy sklep, losowe produkty)
-- Shop 1
INSERT INTO own_products (id, shop_id, product_id, price, quantity, image_url)
VALUES (1, 1, 1, 10.50, 100, NULL),
       (2, 1, 2, 12.00, 80, NULL),
       (3, 1, 3, 15.00, 60, NULL),
       (4, 1, 4, 18.00, 40, NULL),
       (5, 1, 5, 7.00, 200, NULL);

-- Shop 2
INSERT INTO own_products (id, shop_id, product_id, price, quantity, image_url)
VALUES (6, 2, 6, 9.99, 150, NULL),
       (7, 2, 7, 11.20, 90, NULL),
       (8, 2, 8, 10.00, 75, NULL),
       (9, 2, 9, 13.50, 120, NULL),
       (10, 2, 10, 14.00, 110, NULL);

-- Shop 3
INSERT INTO own_products (id, shop_id, product_id, price, quantity, image_url)
VALUES (11, 3, 1, 9.50, 130, NULL),
       (12, 3, 2, 11.00, 100, NULL),
       (13, 3, 3, 12.00, 95, NULL),
       (14, 3, 4, 13.00, 80, NULL),
       (15, 3, 5, 8.00, 140, NULL);

-- Shop 4
INSERT INTO own_products (id, shop_id, product_id, price, quantity, image_url)
VALUES (16, 4, 6, 9.00, 160, NULL),
       (17, 4, 7, 10.50, 85, NULL),
       (18, 4, 8, 11.00, 100, NULL),
       (19, 4, 9, 12.50, 110, NULL),
       (20, 4, 10, 13.00, 115, NULL);

-- Shop 5
INSERT INTO own_products (id, shop_id, product_id, price, quantity, image_url)
VALUES (21, 5, 1, 10.00, 125, NULL),
       (22, 5, 2, 10.50, 105, NULL),
       (23, 5, 3, 12.00, 95, NULL),
       (24, 5, 4, 13.50, 85, NULL),
       (25, 5, 5, 14.00, 75, NULL);

-- Shop 6
INSERT INTO own_products (id, shop_id, product_id, price, quantity, image_url)
VALUES (26, 6, 6, 10.00, 100, NULL),
       (27, 6, 7, 11.50, 80, NULL),
       (28, 6, 8, 12.00, 70, NULL),
       (29, 6, 9, 13.00, 60, NULL),
       (30, 6, 10, 14.00, 50, NULL);

-- Shop 7
INSERT INTO own_products (id, shop_id, product_id, price, quantity, image_url)
VALUES (31, 7, 1, 9.00, 90, NULL),
       (32, 7, 2, 9.50, 100, NULL),
       (33, 7, 3, 10.50, 110, NULL),
       (34, 7, 4, 11.00, 120, NULL),
       (35, 7, 5, 12.00, 130, NULL);

-- Shop 8
INSERT INTO own_products (id, shop_id, product_id, price, quantity, image_url)
VALUES (36, 8, 6, 13.00, 85, NULL),
       (37, 8, 7, 13.50, 95, NULL),
       (38, 8, 8, 14.00, 105, NULL),
       (39, 8, 9, 14.50, 115, NULL),
       (40, 8, 10, 15.00, 125, NULL);

-- Shop 9
INSERT INTO own_products (id, shop_id, product_id, price, quantity, image_url)
VALUES (41, 9, 1, 8.50, 90, NULL),
       (42, 9, 2, 9.00, 100, NULL),
       (43, 9, 3, 10.00, 110, NULL),
       (44, 9, 4, 10.50, 120, NULL),
       (45, 9, 5, 11.00, 130, NULL);

-- Shop 10
INSERT INTO own_products (id, shop_id, product_id, price, quantity, image_url)
VALUES (46, 10, 6, 10.00, 140, NULL),
       (47, 10, 7, 11.00, 150, NULL),
       (48, 10, 8, 12.00, 160, NULL),
       (49, 10, 9, 13.00, 170, NULL),
       (50, 10, 10, 14.00, 180, NULL);
-- Business Hours dla każdego sklepu (7 dni w tygodniu × 10 sklepów = 70 wpisów)
-- Godziny: 08:00–16:00
INSERT INTO business_hours (id, shop_id, day_of_week, opening_time, closing_time)
VALUES
-- Shop 1
(1, 1, 'MONDAY', '08:00:00', '16:00:00'),
(2, 1, 'TUESDAY', '08:00:00', '16:00:00'),
(3, 1, 'WEDNESDAY', '08:00:00', '16:00:00'),
(4, 1, 'THURSDAY', '08:00:00', '16:00:00'),
(5, 1, 'FRIDAY', '08:00:00', '16:00:00'),
(6, 1, 'SATURDAY', '08:00:00', '14:00:00'),
(7, 1, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 2
(8, 2, 'MONDAY', '08:00:00', '16:00:00'),
(9, 2, 'TUESDAY', '08:00:00', '16:00:00'),
(10, 2, 'WEDNESDAY', '08:00:00', '16:00:00'),
(11, 2, 'THURSDAY', '08:00:00', '16:00:00'),
(12, 2, 'FRIDAY', '08:00:00', '16:00:00'),
(13, 2, 'SATURDAY', '08:00:00', '14:00:00'),
(14, 2, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 3
(15, 3, 'MONDAY', '08:00:00', '16:00:00'),
(16, 3, 'TUESDAY', '08:00:00', '16:00:00'),
(17, 3, 'WEDNESDAY', '08:00:00', '16:00:00'),
(18, 3, 'THURSDAY', '08:00:00', '16:00:00'),
(19, 3, 'FRIDAY', '08:00:00', '16:00:00'),
(20, 3, 'SATURDAY', '08:00:00', '14:00:00'),
(21, 3, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 4
(22, 4, 'MONDAY', '08:00:00', '16:00:00'),
(23, 4, 'TUESDAY', '08:00:00', '16:00:00'),
(24, 4, 'WEDNESDAY', '08:00:00', '16:00:00'),
(25, 4, 'THURSDAY', '08:00:00', '16:00:00'),
(26, 4, 'FRIDAY', '08:00:00', '16:00:00'),
(27, 4, 'SATURDAY', '08:00:00', '14:00:00'),
(28, 4, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 5
(29, 5, 'MONDAY', '08:00:00', '16:00:00'),
(30, 5, 'TUESDAY', '08:00:00', '16:00:00'),
(31, 5, 'WEDNESDAY', '08:00:00', '16:00:00'),
(32, 5, 'THURSDAY', '08:00:00', '16:00:00'),
(33, 5, 'FRIDAY', '08:00:00', '16:00:00'),
(34, 5, 'SATURDAY', '08:00:00', '14:00:00'),
(35, 5, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 6
(36, 6, 'MONDAY', '08:00:00', '16:00:00'),
(37, 6, 'TUESDAY', '08:00:00', '16:00:00'),
(38, 6, 'WEDNESDAY', '08:00:00', '16:00:00'),
(39, 6, 'THURSDAY', '08:00:00', '16:00:00'),
(40, 6, 'FRIDAY', '08:00:00', '16:00:00'),
(41, 6, 'SATURDAY', '08:00:00', '14:00:00'),
(42, 6, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 7
(43, 7, 'MONDAY', '08:00:00', '16:00:00'),
(44, 7, 'TUESDAY', '08:00:00', '16:00:00'),
(45, 7, 'WEDNESDAY', '08:00:00', '16:00:00'),
(46, 7, 'THURSDAY', '08:00:00', '16:00:00'),
(47, 7, 'FRIDAY', '08:00:00', '16:00:00'),
(48, 7, 'SATURDAY', '08:00:00', '14:00:00'),
(49, 7, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 8
(50, 8, 'MONDAY', '08:00:00', '16:00:00'),
(51, 8, 'TUESDAY', '08:00:00', '16:00:00'),
(52, 8, 'WEDNESDAY', '08:00:00', '16:00:00'),
(53, 8, 'THURSDAY', '08:00:00', '16:00:00'),
(54, 8, 'FRIDAY', '08:00:00', '16:00:00'),
(55, 8, 'SATURDAY', '08:00:00', '14:00:00'),
(56, 8, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 9
(57, 9, 'MONDAY', '08:00:00', '16:00:00'),
(58, 9, 'TUESDAY', '08:00:00', '16:00:00'),
(59, 9, 'WEDNESDAY', '08:00:00', '16:00:00'),
(60, 9, 'THURSDAY', '08:00:00', '16:00:00'),
(61, 9, 'FRIDAY', '08:00:00', '16:00:00'),
(62, 9, 'SATURDAY', '08:00:00', '14:00:00'),
(63, 9, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 10
(64, 10, 'MONDAY', '08:00:00', '16:00:00'),
(65, 10, 'TUESDAY', '08:00:00', '16:00:00'),
(66, 10, 'WEDNESDAY', '08:00:00', '16:00:00'),
(67, 10, 'THURSDAY', '08:00:00', '16:00:00'),
(68, 10, 'FRIDAY', '08:00:00', '16:00:00'),
(69, 10, 'SATURDAY', '08:00:00', '14:00:00'),
(70, 10, 'SUNDAY', '08:00:00', '12:00:00');

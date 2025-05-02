-- Produkty
INSERT INTO products (name, verified, category)
VALUES ('Truskawki', true, 'FRUIT'),
       ('Maliny', true, 'FRUIT'),
       ('Borówki', true, 'FRUIT'),
       ('Czereśnie', true, 'FRUIT'),
       ('Jabłka', true, 'FRUIT'),
       ('Gruszki', true, 'FRUIT'),
       ('Śliwki', true, 'FRUIT'),
       ('Agrest', true, 'FRUIT'),
       ('Porzeczki', true, 'FRUIT'),
       ('Morele', true, 'FRUIT');

-- Sklepy
INSERT INTO shops (name, description, latitude, longitude, city, address, image_url, verified)
VALUES ('Stragan u Zosi', 'Tradycyjny stragan z owocami', 52.23, 21.01, 'Warszawa', 'ul. Wiejska 1', NULL, true),
       ('Owoce Rynku', 'Świeże owoce z Podlasia', 52.24, 21.02, 'Warszawa', 'ul. Rynek 5', NULL, true),
       ('EkoSad', 'Eko-uprawy z Mazur', 52.22, 21.00, 'Warszawa', 'ul. Zielona 12', NULL, true),
       ('Smaki Lata', 'Słodkie owoce z ogródka', 52.21, 21.03, 'Warszawa', 'ul. Owocowa 3', NULL, true),
       ('Zielony Kram', 'Lokalne smaki', 52.20, 21.04, 'Warszawa', 'ul. Kramarska 7', NULL, true),
       ('Ogród Marii', 'Z rodzinnego gospodarstwa', 52.25, 21.05, 'Warszawa', 'ul. Marii 10', NULL, true),
       ('Targowy Raj', 'Codziennie świeże', 52.26, 21.06, 'Warszawa', 'ul. Targowa 15', NULL, true),
       ('Smaczny Koszyk', 'Bez chemii i nawozów', 52.27, 21.07, 'Warszawa', 'ul. Koszykowa 8', NULL, true),
       ('Sadownik', 'Prosto z sadu', 52.28, 21.08, 'Warszawa', 'ul. Sadowa 2', NULL, true),
       ('Owocowy Zakątek', 'Naturalnie i zdrowo', 52.29, 21.09, 'Warszawa', 'ul. Zakątek 11', NULL, true);

-- Produkty w sklepach (own_products)
INSERT INTO own_products (shop_id, product_id, price, quantity, image_url)
VALUES
-- Shop 1
(1, 1, 10.50, 100, NULL),
(1, 2, 12.00, 80, NULL),
(1, 3, 15.00, 60, NULL),
(1, 4, 18.00, 40, NULL),
(1, 5, 7.00, 200, NULL),
-- Shop 2
(2, 6, 9.99, 150, NULL),
(2, 7, 11.20, 90, NULL),
(2, 8, 10.00, 75, NULL),
(2, 9, 13.50, 120, NULL),
(2, 10, 14.00, 110, NULL),
-- Shop 3
(3, 1, 9.50, 130, NULL),
(3, 2, 11.00, 100, NULL),
(3, 3, 12.00, 95, NULL),
(3, 4, 13.00, 80, NULL),
(3, 5, 8.00, 140, NULL),
-- Shop 4
(4, 6, 9.00, 160, NULL),
(4, 7, 10.50, 85, NULL),
(4, 8, 11.00, 100, NULL),
(4, 9, 12.50, 110, NULL),
(4, 10, 13.00, 115, NULL),
-- Shop 5
(5, 1, 10.00, 125, NULL),
(5, 2, 10.50, 105, NULL),
(5, 3, 12.00, 95, NULL),
(5, 4, 13.50, 85, NULL),
(5, 5, 14.00, 75, NULL),
-- Shop 6
(6, 6, 10.00, 100, NULL),
(6, 7, 11.50, 80, NULL),
(6, 8, 12.00, 70, NULL),
(6, 9, 13.00, 60, NULL),
(6, 10, 14.00, 50, NULL),
-- Shop 7
(7, 1, 9.00, 90, NULL),
(7, 2, 9.50, 100, NULL),
(7, 3, 10.50, 110, NULL),
(7, 4, 11.00, 120, NULL),
(7, 5, 12.00, 130, NULL),
-- Shop 8
(8, 6, 13.00, 85, NULL),
(8, 7, 13.50, 95, NULL),
(8, 8, 14.00, 105, NULL),
(8, 9, 14.50, 115, NULL),
(8, 10, 15.00, 125, NULL),
-- Shop 9
(9, 1, 8.50, 90, NULL),
(9, 2, 9.00, 100, NULL),
(9, 3, 10.00, 110, NULL),
(9, 4, 10.50, 120, NULL),
(9, 5, 11.00, 130, NULL),
-- Shop 10
(10, 6, 10.00, 140, NULL),
(10, 7, 11.00, 150, NULL),
(10, 8, 12.00, 160, NULL),
(10, 9, 13.00, 170, NULL),
(10, 10, 14.00, 180, NULL);

-- Business Hours (7 dni × 10 sklepów = 70 wpisów)
INSERT INTO business_hours (shop_id, day_of_week, opening_time, closing_time)
VALUES
-- Shop 1
(1, 'MONDAY', '08:00:00', '16:00:00'),
(1, 'TUESDAY', '08:00:00', '16:00:00'),
(1, 'WEDNESDAY', '08:00:00', '16:00:00'),
(1, 'THURSDAY', '08:00:00', '16:00:00'),
(1, 'FRIDAY', '08:00:00', '16:00:00'),
(1, 'SATURDAY', '08:00:00', '14:00:00'),
(1, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 2
(2, 'MONDAY', '08:00:00', '16:00:00'),
(2, 'TUESDAY', '08:00:00', '16:00:00'),
(2, 'WEDNESDAY', '08:00:00', '16:00:00'),
(2, 'THURSDAY', '08:00:00', '16:00:00'),
(2, 'FRIDAY', '08:00:00', '16:00:00'),
(2, 'SATURDAY', '08:00:00', '14:00:00'),
(2, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 3
(3, 'MONDAY', '08:00:00', '16:00:00'),
(3, 'TUESDAY', '08:00:00', '16:00:00'),
(3, 'WEDNESDAY', '08:00:00', '16:00:00'),
(3, 'THURSDAY', '08:00:00', '16:00:00'),
(3, 'FRIDAY', '08:00:00', '16:00:00'),
(3, 'SATURDAY', '08:00:00', '14:00:00'),
(3, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 4
(4, 'MONDAY', '08:00:00', '16:00:00'),
(4, 'TUESDAY', '08:00:00', '16:00:00'),
(4, 'WEDNESDAY', '08:00:00', '16:00:00'),
(4, 'THURSDAY', '08:00:00', '16:00:00'),
(4, 'FRIDAY', '08:00:00', '16:00:00'),
(4, 'SATURDAY', '08:00:00', '14:00:00'),
(4, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 5
(5, 'MONDAY', '08:00:00', '16:00:00'),
(5, 'TUESDAY', '08:00:00', '16:00:00'),
(5, 'WEDNESDAY', '08:00:00', '16:00:00'),
(5, 'THURSDAY', '08:00:00', '16:00:00'),
(5, 'FRIDAY', '08:00:00', '16:00:00'),
(5, 'SATURDAY', '08:00:00', '14:00:00'),
(5, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 6
(6, 'MONDAY', '08:00:00', '16:00:00'),
(6, 'TUESDAY', '08:00:00', '16:00:00'),
(6, 'WEDNESDAY', '08:00:00', '16:00:00'),
(6, 'THURSDAY', '08:00:00', '16:00:00'),
(6, 'FRIDAY', '08:00:00', '16:00:00'),
(6, 'SATURDAY', '08:00:00', '14:00:00'),
(6, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 7
(7, 'MONDAY', '08:00:00', '16:00:00'),
(7, 'TUESDAY', '08:00:00', '16:00:00'),
(7, 'WEDNESDAY', '08:00:00', '16:00:00'),
(7, 'THURSDAY', '08:00:00', '16:00:00'),
(7, 'FRIDAY', '08:00:00', '16:00:00'),
(7, 'SATURDAY', '08:00:00', '14:00:00'),
(7, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 8
(8, 'MONDAY', '08:00:00', '16:00:00'),
(8, 'TUESDAY', '08:00:00', '16:00:00'),
(8, 'WEDNESDAY', '08:00:00', '16:00:00'),
(8, 'THURSDAY', '08:00:00', '16:00:00'),
(8, 'FRIDAY', '08:00:00', '16:00:00'),
(8, 'SATURDAY', '08:00:00', '14:00:00'),
(8, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 9
(9, 'MONDAY', '08:00:00', '16:00:00'),
(9, 'TUESDAY', '08:00:00', '16:00:00'),
(9, 'WEDNESDAY', '08:00:00', '16:00:00'),
(9, 'THURSDAY', '08:00:00', '16:00:00'),
(9, 'FRIDAY', '08:00:00', '16:00:00'),
(9, 'SATURDAY', '08:00:00', '14:00:00'),
(9, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 10
(10, 'MONDAY', '08:00:00', '16:00:00'),
(10, 'TUESDAY', '08:00:00', '16:00:00'),
(10, 'WEDNESDAY', '08:00:00', '16:00:00'),
(10, 'THURSDAY', '08:00:00', '16:00:00'),
(10, 'FRIDAY', '08:00:00', '16:00:00'),
(10, 'SATURDAY', '08:00:00', '14:00:00'),
(10, 'SUNDAY', '08:00:00', '12:00:00');

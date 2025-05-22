-- Produkty
INSERT INTO products (name, verified, category)
VALUES ('Truskawki', true, 'FRUIT'),
       ('Maliny', true, 'FRUIT'),
       ('Borówki', true, 'FRUIT'),
       ('Czereśnie', true, 'FRUIT'),
       ('Jabłka', true, 'FRUIT'),
       ('Gruszki', true, 'FRUIT'),
       ('Śliwki', true, 'FRUIT'),
       ('Pomidory', true, 'VEGETABLE'),
       ('Ogórki', true, 'VEGETABLE'),
       ('Marchew', true, 'VEGETABLE');

-- Użytkownicy
INSERT INTO users (first_name, last_name, email, phone_number, img, tier)
VALUES
    ('Jan', 'Kowalski', 'jan1@example.com', '+48123123123', NULL,1),
    ('Anna', 'Nowak', 'anna2@example.com', '+48123123124', NULL,1),
    ('Piotr', 'Wiśniewski', 'piotr3@example.com', '+48123123125', NULL,1),
    ('Maria', 'Wójcik', 'maria4@example.com', '+48123123126', NULL,1),
    ('Tomasz', 'Kaczmarek', 'tomasz5@example.com', '+48123123127', NULL,1),
    ('Agnieszka', 'Mazur', 'agnieszka6@example.com', '+48123123128', NULL,1),
    ('Marek', 'Krawczyk', 'marek7@example.com', '+48123123129', NULL,1),
    ('Katarzyna', 'Piotrowska', 'kasia8@example.com', '+48123123130', NULL,1),
    ('Paweł', 'Grabowski', 'pawel9@example.com', '+48123123131', NULL,1),
    ('Ewa', 'Zając', 'ewa10@example.com', '+48123123132', NULL,1);

-- Sklepy
INSERT INTO stores (user_id, name, slug, description, latitude, longitude, city, address, image_url, verified)
VALUES
    (1, 'Stragan u Zosi', 'stragan-u-zosi-1', 'Tradycyjny stragan z owocami', 52.23, 21.01, 'Warszawa', 'ul. Wiejska 1', NULL, true),
    (2, 'Owoce Rynku', 'owoce-rynku-1', 'Świeże owoce z Podlasia', 52.24, 21.02, 'Warszawa', 'ul. Rynek 5', NULL, true),
    (3, 'EkoSad', 'ekosad-1', 'Eko-uprawy z Mazur', 52.22, 21.00, 'Warszawa', 'ul. Zielona 12', NULL, true),
    (4, 'Smaki Lata', 'smaki-lata-1', 'Słodkie owoce z ogródka', 52.21, 21.03, 'Warszawa', 'ul. Owocowa 3', NULL, true),
    (5, 'Zielony Kram', 'zielony-kram-1', 'Lokalne smaki', 52.20, 21.04, 'Warszawa', 'ul. Kramarska 7', NULL, true),
    (6, 'Ogród Marii', 'ogrod-marii-1', 'Z rodzinnego gospodarstwa', 52.25, 21.05, 'Warszawa', 'ul. Marii 10', NULL, true),
    (7, 'Targowy Raj', 'targowy-raj-1', 'Codziennie świeże', 52.26, 21.06, 'Warszawa', 'ul. Targowa 15', NULL, true),
    (8, 'Smaczny Koszyk', 'smaczny-koszyk-1', 'Bez chemii i nawozów', 52.27, 21.07, 'Warszawa', 'ul. Koszykowa 8', NULL, true),
    (9, 'Sadownik', 'sadownik-1', 'Prosto z sadu', 52.28, 21.08, 'Warszawa', 'ul. Sadowa 2', NULL, true),
    (10, 'Owocowy Zakątek', 'owocowy-zakatek-1', 'Naturalnie i zdrowo', 52.29, 21.09, 'Warszawa', 'ul. Zakątek 11', NULL, true);

-- Produkty w sklepach (own_products)
INSERT INTO own_products (store_id, product_id, price, quantity, image_url)
VALUES
-- Shop 1
(1, 1, 10.50, 100, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747582794/truskawki-tunel_bh1mbw.jpg'),
(1, 2, 12.00, 80, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747583231/maliny-4_zo9ti9.jpg'),
(1, 3, 15.00, 60, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747587844/Borowki_4_nquqvo.jpg'),
(1, 4, 18.00, 40, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588200/czeresnie-3_jlmdtz.jpg'),
(1, 5, 7.00, 200, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588973/jablka-5_mo8yts.jpg'),
-- Shop 2
(2, 6, 9.99, 150, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747589835/gruszki-5_aww9bz.jpg'),
(2, 7, 11.20, 90, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747590770/sliwki-5_uzphmm.jpg'),
(2, 8, 10.00, 75, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747591950/pomidory-5_owops9.jpg'),
(2, 9, 13.50, 120, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592215/ogorki-5_vsymlr.jpg'),
(2, 10, 14.00, 110, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592524/marchew-5_hvdqnf.jpg'),
-- Shop 3
(3, 1, 9.50, 130, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747582793/owoce-kaszubskie-truskawki-delicje-kaszub-1024x641_ttd8bu.jpg'),
(3, 2, 11.00, 100, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747583232/maliny-5_whdpbc.jpg'),
(3, 3, 12.00, 95, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747587845/Borowki_5_f0gf0q.jpg'),
(3, 4, 13.00, 80, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588204/czeresnie-5_p3eatf.jpg'),
(3, 5, 8.00, 140, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588971/jablka-4_zdt8oy.jpg'),
-- Shop 4
(4, 6, 9.00, 160, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747589833/gruszki-4_gapnvu.jpg'),
(4, 7, 10.50, 85, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747590766/sliwki-4_njlx1t.jpg'),
(4, 8, 11.00, 100, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747591947/pomidory-4_s6xkwb.jpg'),
(4, 9, 12.50, 110, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592212/ogorki-4_uu4t2y.jpg'),
(4, 10, 13.00, 115, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592519/marchew-4_f1wqat.jpg'),
-- Shop 5
(5, 1, 10.00, 125, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747582794/z27129298Q_98-procent-Polakow-deklaruje--ze-je-truskawki-przy_uzh0zw.jpg'),
(5, 2, 10.50, 105, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747583230/Maliny-3_b3bkud.jpg'),
(5, 3, 12.00, 95, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747587843/Borowki_3_ntghg4.jpg'),
(5, 4, 13.50, 85, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588204/czeresnie-5_p3eatf.jpg'),
(5, 5, 14.00, 75, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588968/jablka-3_t16ror.jpg'),
-- Shop 6
(6, 6, 10.00, 100, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747589830/gruszki-3_gntrbj.jpg'),
(6, 7, 11.50, 80, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747590763/sliwki-3_bseict.jpg'),
(6, 8, 12.00, 70, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747591909/pomidory-3_mzj94r.jpg'),
(6, 9, 13.00, 60, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592207/ogorki-3_e4ks54.jpg'),
(6, 10, 14.00, 50, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592515/marchew-3_yey3ad.jpg'),
-- Shop 7
(7, 1, 9.00, 90, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747582793/000KXR1C07W2JK7D-C461-F4_z1jalv.jpg'),
(7, 2, 9.50, 100, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747583229/Maliny-1_hy3epk.jpg'),
(7, 3, 10.50, 110, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747587842/Borowki_2_reu2s9.jpg'),
(7, 4, 11.00, 120, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588198/czeresnie-2_wm1vlz.jpg'),
(7, 5, 12.00, 130, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588966/jablka-2_heaomu.jpg'),
-- Shop 8
(8, 6, 13.00, 85, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747589827/gruszki-2_zqskr9.jpg'),
(8, 7, 13.50, 95, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747590759/sliwki-2_qsldxf.jpg'),
(8, 8, 14.00, 105, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747591906/pomidory-2_hxtwbw.jpg'),
(8, 9, 14.50, 115, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592203/ogorki-2_sfojr1.jpg'),
(8, 10, 15.00, 125, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592511/marchew-2_cem9f6.jpg'),
-- Shop 9
(9, 1, 8.50, 90, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747582793/042749_r0_980_kokfe0.jpg'),
(9, 2, 9.00, 100, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747583230/Maliny-2_e7pwiv.jpg'),
(9, 3, 10.00, 110, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747587841/Borowki_1_ruiwtv.png'),
(9, 4, 10.50, 120, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588198/czeresnie-1_d6zp7l.jpg'),
(9, 5, 11.00, 130, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588965/jablka-1_wtktnz.jpg'),
-- Shop 10
(10, 6, 10.00, 140, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747589826/gruszki-1_yeplh4.jpg'),
(10, 7, 11.00, 150, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747590757/sliwki-1_jeu8jg.jpg'),
(10, 8, 12.00, 160, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747591903/pomidory-1_mtxggt.jpg'),
(10, 9, 13.00, 170, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592200/ogorki-1_kkb4ns.jpg'),
(10, 10, 14.00, 180, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592507/marchew-1_f4tb3v.jpg');

-- Business Hours (7 dni × 10 sklepów = 70 wpisów)
INSERT INTO business_hours (store_id, day_of_week, opening_time, closing_time)
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

-- Opinie
INSERT INTO opinions (store_id, user_id, description, stars)
VALUES
    (1, 3, 'Świetne owoce, bardzo świeże!', 5),
    (2, 1, 'Obsługa bardzo miła, polecam!', 4),
    (3, 2, 'Super ceny i lokalizacja.', 5),
    (4, 5, 'Jakość mogłaby być lepsza.', 3),
    (5, 7, 'Zawsze wracam po więcej!', 5),
    (6, 4, 'Fajne miejsce, eko owoce.', 4),
    (7, 10, 'Duży wybór i dobra cena.', 5),
    (8, 9, 'Lubię ten sklep, lokalnie i smacznie.', 5),
    (9, 8, 'Średnie ceny, ale dobra jakość.', 4),
    (10, 7, 'Polecam każdemu!', 5);

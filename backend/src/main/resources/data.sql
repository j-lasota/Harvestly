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
       ('Marchew', true, 'VEGETABLE'),
       ('Ziemniaki', true, 'VEGETABLE');

-- Użytkownicy
INSERT INTO users (id, first_name, last_name, email, phone_number, img, tier)
VALUES
    ('9f1c22f3-1a10-4d4e-8b73-72a60b973401', 'Jan', 'Kowalski', 'jan1@example.com', '+48123123123', NULL, 1),
    ('a134f4e6-b305-45e3-94cd-12ae4e50df3c', 'Anna', 'Nowak', 'anna2@example.com', '+48123123124', NULL, 1),
    ('81e5d01c-7cc2-4213-b3d3-598df3520ff2', 'Piotr', 'Wiśniewski', 'piotr3@example.com', '+48123123125', NULL, 1),
    ('b379f994-2dc7-4f12-802b-1e646f74bdbb', 'Maria', 'Wójcik', 'maria4@example.com', '+48123123126', NULL, 1),
    ('8d7f1f12-38b3-4b14-97a4-b1a7631d13b1', 'Tomasz', 'Kaczmarek', 'tomasz5@example.com', '+48123123127', NULL, 1),
    ('72ec4c7f-bb9c-4b2d-8d0e-6dc123b7f3a4', 'Agnieszka', 'Mazur', 'agnieszka6@example.com', '+48123123128', NULL, 1),
    ('54ed9b1f-5905-4a98-bd4d-faf263de62d1', 'Marek', 'Krawczyk', 'marek7@example.com', '+48123123129', NULL, 1),
    ('e734f6ef-b738-4ed7-9293-7d96e1f7f2e2', 'Katarzyna', 'Piotrowska', 'kasia8@example.com', '+48123123130', NULL, 1),
    ('d4f0aa48-28a5-4bb3-b194-bac4919a5603', 'Paweł', 'Grabowski', 'pawel9@example.com', '+48123123131', NULL, 1),
    ('07a0fc37-d3a7-408e-a765-098e2c612258', 'Ewa', 'Zając', 'ewa10@example.com', '+48123123132', NULL, 1);

-- Sklepy
INSERT INTO stores (user_id, name, slug, description, latitude, longitude, city, address, image_url, verified)
VALUES
    ('9f1c22f3-1a10-4d4e-8b73-72a60b973401', 'Stragan u Zosi', 'stragan-u-zosi', 'Tradycyjny stragan z owocami', 52.23, 21.01, 'Warszawa', 'ul. Wiejska 1', 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1748339178/2_uaqhvo.avif', true),
    ('a134f4e6-b305-45e3-94cd-12ae4e50df3c', 'Owoce Rynku', 'owoce-rynku', 'Świeże owoce z Podlasia', 52.24, 21.02, 'Warszawa', 'ul. Rynek 5', 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1748339178/1_kvyhn4.webp', true),
    ('81e5d01c-7cc2-4213-b3d3-598df3520ff2', 'EkoSad', 'ekosad', 'Eko-uprawy z Mazur', 52.22, 21.00, 'Warszawa', 'ul. Zielona 12', NULL, true),
    ('b379f994-2dc7-4f12-802b-1e646f74bdbb', 'Smaki Lata', 'smaki-lata', 'Słodkie owoce z ogródka', 52.21, 21.03, 'Warszawa', 'ul. Owocowa 3', NULL, true),
    ('8d7f1f12-38b3-4b14-97a4-b1a7631d13b1', 'Zielony Kram', 'zielony-kram', 'Lokalne smaki', 52.20, 21.04, 'Warszawa', 'ul. Kramarska 7', NULL, true),
    ('72ec4c7f-bb9c-4b2d-8d0e-6dc123b7f3a4', 'Ogród Marii', 'ogrod-marii', 'Z rodzinnego gospodarstwa', 52.25, 21.05, 'Warszawa', 'ul. Marii 10', NULL, true),
    ('54ed9b1f-5905-4a98-bd4d-faf263de62d1', 'Targowy Raj', 'targowy-raj', 'Codziennie świeże', 52.26, 21.06, 'Warszawa', 'ul. Targowa 15', NULL, true),
    ('e734f6ef-b738-4ed7-9293-7d96e1f7f2e2', 'Smaczny Koszyk', 'smaczny-koszyk', 'Bez chemii i nawozów', 52.27, 21.07, 'Warszawa', 'ul. Koszykowa 8', NULL, true),
    ('d4f0aa48-28a5-4bb3-b194-bac4919a5603', 'Sadownik', 'sadownik', 'Prosto z sadu', 52.28, 21.08, 'Warszawa', 'ul. Sadowa 2', NULL, true),
    ('07a0fc37-d3a7-408e-a765-098e2c612258', 'Owocowy Zakątek', 'owocowy-zakatek', 'Naturalnie i zdrowo', 52.29, 21.09, 'Warszawa', 'ul. Zakątek 11', NULL, true),
    ('9f1c22f3-1a10-4d4e-8b73-72a60b973401', 'Łódzkie Smaki Natury', 'lodzkie-smaki-natury', 'Naturalne produkty prosto z Łodzi', 51.759248, 19.455983, 'Łódź', 'ul. Piotrkowska 101', NULL, true),
    ('a134f4e6-b305-45e3-94cd-12ae4e50df3c', 'Stragan Pod Lipą', 'stragan-pod-lipa', 'Stragan pod rozłożystą lipą w centrum Łodzi', 51.759900, 19.426900, 'Łódź', 'ul. Lipowa 5', NULL, true),
    ('81e5d01c-7cc2-4213-b3d3-598df3520ff2', 'Zielony Zakątek Łódź', 'zielony-zakatek-lodz', 'Zielone warzywa i owoce z Łodzi', 51.800070, 19.467210, 'Łódź', 'ul. Zakątek 3', NULL, true),
    ('b379f994-2dc7-4f12-802b-1e646f74bdbb', 'Ogródek Babci Heli', 'ogrodek-babci-heli', 'Domowe przetwory i świeże warzywa', 51.747800, 19.411200, 'Łódź', 'ul. Helin 7', NULL, true),
    ('8d7f1f12-38b3-4b14-97a4-b1a7631d13b1', 'Tęczowy Koszyk', 'teczowy-koszyk', 'Kolorowe owoce i warzywa', 51.782010, 19.536010, 'Łódź', 'ul. Kolorowa 9', NULL, true),
    ('72ec4c7f-bb9c-4b2d-8d0e-6dc123b7f3a4', 'Łódzka Spiżarnia', 'lodzka-spizarnia', 'Tradycyjne produkty z regionu', 51.734520, 19.485010, 'Łódź', 'ul. Spiżowa 2', NULL, true),
    ('54ed9b1f-5905-4a98-bd4d-faf263de62d1', 'Stragan Pod Chmurką', 'stragan-pod-chmurka', 'Stragan na świeżym powietrzu', 51.813010, 19.410000, 'Łódź', 'ul. Chmurna 11', NULL, true),
    ('e734f6ef-b738-4ed7-9293-7d96e1f7f2e2', 'Eko Targ Łódź', 'eko-targ-lodz', 'Ekologiczne produkty od lokalnych rolników', 51.747040, 19.531000, 'Łódź', 'ul. Targowa 30', NULL, true),
    ('d4f0aa48-28a5-4bb3-b194-bac4919a5603', 'Owocowy Rajd', 'owocowy-rajd', 'Raj dla miłośników owoców', 51.771200, 19.393000, 'Łódź', 'ul. Rajska 4', NULL, true),
    ('07a0fc37-d3a7-408e-a765-098e2c612258', 'Warzywniak u Sąsiada', 'warzywniak-u-sasiada', 'Warzywa od zaprzyjaźnionych gospodarzy', 51.784500, 19.481200, 'Łódź', 'ul. Sąsiedzka 8', NULL, true),
    ('9f1c22f3-1a10-4d4e-8b73-72a60b973401', 'Ziemniakowy Raj', 'ziemniakowy-raj', 'Najlepsze ziemniaki z okolicznych pól', 52.232334, 21.000908, 'Warszawa', 'ul. Śliska 5', NULL, true);

-- Produkty w sklepach (own_products)
INSERT INTO own_products (store_id, product_id, base_price, price, quantity, image_url, discount)
VALUES
-- Shop 1
(1, 1, 10.50, 10.50, 100, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747582794/truskawki-tunel_bh1mbw.jpg', 0),
(1, 2, 12.00, 12.00, 80, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747583231/maliny-4_zo9ti9.jpg', 0),
(1, 3, 15.00, 15.00, 60, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747587844/Borowki_4_nquqvo.jpg', 0),
(1, 4, 18.00, 18.00, 40, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588200/czeresnie-3_jlmdtz.jpg', 0),
(1, 5, 7.00, 7.00, 200, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588973/jablka-5_mo8yts.jpg', 0),
-- Shop 2
(2, 6, 9.99, 9.99, 150, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747589835/gruszki-5_aww9bz.jpg', 0),
(2, 7, 11.20, 11.20, 90, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747590770/sliwki-5_uzphmm.jpg', 0),
(2, 8, 10.00, 10.00, 75, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747591950/pomidory-5_owops9.jpg', 0),
(2, 9, 13.50, 13.50, 120, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592215/ogorki-5_vsymlr.jpg', 0),
(2, 10, 14.00, 14.00, 110, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592524/marchew-5_hvdqnf.jpg', 0),
-- Shop 3
(3, 1, 9.50, 9.50, 130, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747582793/owoce-kaszubskie-truskawki-delicje-kaszub-1024x641_ttd8bu.jpg', 0),
(3, 2, 11.00, 11.00, 100, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747583232/maliny-5_whdpbc.jpg', 0),
(3, 3, 12.00, 12.00, 95, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747587845/Borowki_5_f0gf0q.jpg', 0),
(3, 4, 13.00, 13.00, 80, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588204/czeresnie-5_p3eatf.jpg', 0),
(3, 5, 8.00, 8.00, 140, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588971/jablka-4_zdt8oy.jpg', 0),
-- Shop 4
(4, 6, 9.00, 9.00, 160, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747589833/gruszki-4_gapnvu.jpg', 0),
(4, 7, 10.50, 10.50, 85, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747590766/sliwki-4_njlx1t.jpg', 0),
(4, 8, 11.00, 11.00, 100, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747591947/pomidory-4_s6xkwb.jpg', 0),
(4, 9, 12.50, 12.50, 110, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592212/ogorki-4_uu4t2y.jpg', 0),
(4, 10, 13.00, 13.00, 115, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592519/marchew-4_f1wqat.jpg', 0),
-- Shop 5
(5, 1, 10.00, 10.00, 125, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747582794/z27129298Q_98-procent-Polakow-deklaruje--ze-je-truskawki-przy_uzh0zw.jpg', 0),
(5, 2, 10.50, 10.50, 105, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747583230/Maliny-3_b3bkud.jpg', 0),
(5, 3, 12.00, 12.00, 95, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747587843/Borowki_3_ntghg4.jpg', 0),
(5, 4, 13.50, 13.50, 85, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588204/czeresnie-5_p3eatf.jpg', 0),
(5, 5, 14.00, 14.00, 75, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588968/jablka-3_t16ror.jpg', 0),
-- Shop 6
(6, 6, 10.00, 10.00, 100, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747589830/gruszki-3_gntrbj.jpg', 0),
(6, 7, 11.50, 11.50, 80, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747590763/sliwki-3_bseict.jpg', 0),
(6, 8, 12.00, 12.00, 70, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747591909/pomidory-3_mzj94r.jpg', 0),
(6, 9, 13.00, 13.00, 60, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592207/ogorki-3_e4ks54.jpg', 0),
(6, 10, 14.00, 14.00, 50, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592515/marchew-3_yey3ad.jpg', 0),
-- Shop 7
(7, 1, 9.00, 9.00, 90, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747582793/000KXR1C07W2JK7D-C461-F4_z1jalv.jpg', 0),
(7, 2, 9.50, 9.50, 100, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747583229/Maliny-1_hy3epk.jpg', 0),
(7, 3, 10.50, 10.50, 110, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747587842/Borowki_2_reu2s9.jpg', 0),
(7, 4, 11.00, 11.00, 120, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588198/czeresnie-2_wm1vlz.jpg', 0),
(7, 5, 12.00, 12.00, 130, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588966/jablka-2_heaomu.jpg', 0),
-- Shop 8
(8, 6, 13.00, 13.00, 85, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747589827/gruszki-2_zqskr9.jpg', 0),
(8, 7, 13.50, 13.50, 95, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747590759/sliwki-2_qsldxf.jpg', 0),
(8, 8, 14.00, 14.00, 105, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747591906/pomidory-2_hxtwbw.jpg', 0),
(8, 9, 14.50, 14.50, 115, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592203/ogorki-2_sfojr1.jpg', 0),
(8, 10, 15.00, 15.00, 125, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592511/marchew-2_cem9f6.jpg', 0),
-- Shop 9
(9, 1, 8.50, 8.50, 90, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747582793/042749_r0_980_kokfe0.jpg', 0),
(9, 2, 9.00, 9.00, 100, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747583230/Maliny-2_e7pwiv.jpg', 0),
(9, 3, 10.00, 10.00, 110, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747587841/Borowki_1_ruiwtv.png', 0),
(9, 4, 10.50, 10.50, 120, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588198/czeresnie-1_d6zp7l.jpg', 0),
(9, 5, 11.00, 11.00, 130, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588965/jablka-1_wtktnz.jpg', 0),
-- Shop 10
(10, 6, 10.00, 10.00, 140, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747589826/gruszki-1_yeplh4.jpg', 0),
(10, 7, 11.00, 11.00, 150, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747590757/sliwki-1_jeu8jg.jpg', 0),
(10, 8, 12.00, 12.00, 160, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747591903/pomidory-1_mtxggt.jpg', 0),
(10, 9, 13.00, 13.00, 170, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592200/ogorki-1_kkb4ns.jpg', 0),
(10, 10, 14.00, 14.00, 180, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592507/marchew-1_f4tb3v.jpg', 0),
-- Shop 11
(11, 1, 10.80, 10.80, 90, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747582794/truskawki-tunel_bh1mbw.jpg', 0),
(11, 2, 12.20, 12.20, 70, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747583231/maliny-4_zo9ti9.jpg', 0),
(11, 3, 15.30, 15.30, 65, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747587844/Borowki_4_nquqvo.jpg', 0),
(11, 4, 18.50, 18.50, 35, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588200/czeresnie-3_jlmdtz.jpg', 0),
(11, 5, 7.20, 7.20, 180, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588973/jablka-5_mo8yts.jpg', 0),

-- Shop 12
(12, 6, 10.10, 10.10, 140, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747589835/gruszki-5_aww9bz.jpg', 0),
(12, 7, 11.40, 11.40, 85, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747590770/sliwki-5_uzphmm.jpg', 0),
(12, 8, 10.20, 10.20, 70, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747591950/pomidory-5_owops9.jpg', 0),
(12, 9, 13.70, 13.70, 110, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592215/ogorki-5_vsymlr.jpg', 0),
(12, 10, 14.10, 14.10, 100, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592524/marchew-5_hvdqnf.jpg', 0),

-- Shop 13
(13, 1, 9.70, 9.70, 120, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747582793/owoce-kaszubskie-truskawki-delicje-kaszub-1024x641_ttd8bu.jpg', 0),
(13, 2, 11.10, 11.10, 90, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747583232/maliny-5_whdpbc.jpg', 0),
(13, 3, 12.10, 12.10, 85, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747587845/Borowki_5_f0gf0q.jpg', 0),
(13, 4, 13.10, 13.10, 70, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588204/czeresnie-5_p3eatf.jpg', 0),
(13, 5, 8.10, 8.10, 130, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588971/jablka-4_zdt8oy.jpg', 0),

-- Shop 14
(14, 6, 9.20, 9.20, 150, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747589833/gruszki-4_gapnvu.jpg', 0),
(14, 7, 10.60, 10.60, 80, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747590766/sliwki-4_njlx1t.jpg', 0),
(14, 8, 11.10, 11.10, 90, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747591947/pomidory-4_s6xkwb.jpg', 0),
(14, 9, 12.60, 12.60, 100, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592212/ogorki-4_uu4t2y.jpg', 0),
(14, 10, 13.10, 13.10, 105, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592519/marchew-4_f1wqat.jpg', 0),

-- Shop 15
(15, 1, 10.10, 10.10, 110, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747582794/z27129298Q_98-procent-Polakow-deklaruje--ze-je-truskawki-przy_uzh0zw.jpg', 0),
(15, 2, 10.60, 10.60, 95, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747583230/Maliny-3_b3bkud.jpg', 0),
(15, 3, 12.10, 12.10, 85, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747587843/Borowki_3_ntghg4.jpg', 0),
(15, 4, 13.60, 13.60, 75, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588204/czeresnie-5_p3eatf.jpg', 0),
(15, 5, 14.10, 14.10, 65, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588968/jablka-3_t16ror.jpg', 0),

-- Shop 16
(16, 6, 10.10, 10.10, 90, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747589830/gruszki-3_gntrbj.jpg', 0),
(16, 7, 11.60, 11.60, 70, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747590763/sliwki-3_bseict.jpg', 0),
(16, 8, 12.10, 12.10, 60, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747591909/pomidory-3_mzj94r.jpg', 0),
(16, 9, 13.10, 13.10, 50, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592207/ogorki-3_e4ks54.jpg', 0),
(16, 10, 14.10, 14.10, 40, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592515/marchew-3_yey3ad.jpg', 0),

-- Shop 17
(17, 1, 9.10, 9.10, 80, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747582793/000KXR1C07W2JK7D-C461-F4_z1jalv.jpg', 0),
(17, 2, 9.60, 9.60, 90, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747583229/Maliny-1_hy3epk.jpg', 0),
(17, 3, 10.60, 10.60, 100, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747587842/Borowki_2_reu2s9.jpg', 0),
(17, 4, 11.10, 11.10, 110, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588198/czeresnie-2_wm1vlz.jpg', 0),
(17, 5, 12.10, 12.10, 120, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588966/jablka-2_heaomu.jpg', 0),
-- Shop 18
(18, 6, 13.10, 13.10, 75, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747589827/gruszki-2_zqskr9.jpg', 0),
(18, 7, 13.60, 13.60, 85, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747590759/sliwki-2_qsldxf.jpg', 0),
(18, 8, 14.10, 14.10, 95, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747591906/pomidory-2_hxtwbw.jpg', 0),
(18, 9, 14.60, 14.60, 105, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592203/ogorki-2_sfojr1.jpg', 0),
(18, 10, 15.10, 15.10, 115, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592511/marchew-2_cem9f6.jpg', 0),
-- Shop 19
(19, 1, 8.60, 8.60, 80, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747582793/042749_r0_980_kokfe0.jpg', 0),
(19, 2, 9.10, 9.10, 90, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747583230/Maliny-2_e7pwiv.jpg', 0),
(19, 3, 10.10, 10.10, 100, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747587841/Borowki_1_ruiwtv.png', 0),
(19, 4, 10.60, 10.60, 110, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588198/czeresnie-1_d6zp7l.jpg', 0),
(19, 5, 11.10, 11.10, 120, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747588965/jablka-1_wtktnz.jpg', 0),
-- Shop 20
(20, 6, 10.10, 10.10, 130, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747589826/gruszki-1_yeplh4.jpg', 0),
(20, 7, 11.10, 11.10, 140, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747590757/sliwki-1_jeu8jg.jpg', 0),
(20, 8, 12.10, 12.10, 150, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747591903/pomidory-1_mtxggt.jpg', 0),
(20, 9, 13.10, 13.10, 160, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592200/ogorki-1_kkb4ns.jpg', 0),
(20, 10, 14.10, 14.10, 170, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1747592507/marchew-1_f4tb3v.jpg', 0),
-- Shop 21
    (21, 11, 10.90, 10.90, 80, 'https://res.cloudinary.com/dfzgy9znb/image/upload/v1749989855/ziemniaki_qa3q2z.jpg', 0);
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
(10, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 11
(11, 'MONDAY', '08:00:00', '16:00:00'),
(11, 'TUESDAY', '08:00:00', '16:00:00'),
(11, 'WEDNESDAY', '08:00:00', '16:00:00'),
(11, 'THURSDAY', '08:00:00', '16:00:00'),
(11, 'FRIDAY', '08:00:00', '16:00:00'),
(11, 'SATURDAY', '08:00:00', '14:00:00'),
(11, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 12
(12, 'MONDAY', '08:00:00', '16:00:00'),
(12, 'TUESDAY', '08:00:00', '16:00:00'),
(12, 'WEDNESDAY', '08:00:00', '16:00:00'),
(12, 'THURSDAY', '08:00:00', '16:00:00'),
(12, 'FRIDAY', '08:00:00', '16:00:00'),
(12, 'SATURDAY', '08:00:00', '14:00:00'),
(12, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 13
(13, 'MONDAY', '08:00:00', '16:00:00'),
(13, 'TUESDAY', '08:00:00', '16:00:00'),
(13, 'WEDNESDAY', '08:00:00', '16:00:00'),
(13, 'THURSDAY', '08:00:00', '16:00:00'),
(13, 'FRIDAY', '08:00:00', '16:00:00'),
(13, 'SATURDAY', '08:00:00', '14:00:00'),
(13, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 14
(14, 'MONDAY', '08:00:00', '16:00:00'),
(14, 'TUESDAY', '08:00:00', '16:00:00'),
(14, 'WEDNESDAY', '08:00:00', '16:00:00'),
(14, 'THURSDAY', '08:00:00', '16:00:00'),
(14, 'FRIDAY', '08:00:00', '16:00:00'),
(14, 'SATURDAY', '08:00:00', '14:00:00'),
(14, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 15
(15, 'MONDAY', '08:00:00', '16:00:00'),
(15, 'TUESDAY', '08:00:00', '16:00:00'),
(15, 'WEDNESDAY', '08:00:00', '16:00:00'),
(15, 'THURSDAY', '08:00:00', '16:00:00'),
(15, 'FRIDAY', '08:00:00', '16:00:00'),
(15, 'SATURDAY', '08:00:00', '14:00:00'),
(15, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 16
(16, 'MONDAY', '08:00:00', '16:00:00'),
(16, 'TUESDAY', '08:00:00', '16:00:00'),
(16, 'WEDNESDAY', '08:00:00', '16:00:00'),
(16, 'THURSDAY', '08:00:00', '16:00:00'),
(16, 'FRIDAY', '08:00:00', '16:00:00'),
(16, 'SATURDAY', '08:00:00', '14:00:00'),
(16, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 17
(17, 'MONDAY', '08:00:00', '16:00:00'),
(17, 'TUESDAY', '08:00:00', '16:00:00'),
(17, 'WEDNESDAY', '08:00:00', '16:00:00'),
(17, 'THURSDAY', '08:00:00', '16:00:00'),
(17, 'FRIDAY', '08:00:00', '16:00:00'),
(17, 'SATURDAY', '08:00:00', '14:00:00'),
(17, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 18
(18, 'MONDAY', '08:00:00', '16:00:00'),
(18, 'TUESDAY', '08:00:00', '16:00:00'),
(18, 'WEDNESDAY', '08:00:00', '16:00:00'),
(18, 'THURSDAY', '08:00:00', '16:00:00'),
(18, 'FRIDAY', '08:00:00', '16:00:00'),
(18, 'SATURDAY', '08:00:00', '14:00:00'),
(18, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 19
(19, 'MONDAY', '08:00:00', '16:00:00'),
(19, 'TUESDAY', '08:00:00', '16:00:00'),
(19, 'WEDNESDAY', '08:00:00', '16:00:00'),
(19, 'THURSDAY', '08:00:00', '16:00:00'),
(19, 'FRIDAY', '08:00:00', '16:00:00'),
(19, 'SATURDAY', '08:00:00', '14:00:00'),
(19, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 20
(20, 'MONDAY', '08:00:00', '16:00:00'),
(20, 'TUESDAY', '08:00:00', '16:00:00'),
(20, 'WEDNESDAY', '08:00:00', '16:00:00'),
(20, 'THURSDAY', '08:00:00', '16:00:00'),
(20, 'FRIDAY', '08:00:00', '16:00:00'),
(20, 'SATURDAY', '08:00:00', '14:00:00'),
(20, 'SUNDAY', '08:00:00', '12:00:00'),

-- Shop 21
(21, 'MONDAY', '08:00:00', '16:00:00'),
(21, 'TUESDAY', '08:00:00', '16:00:00'),
(21, 'WEDNESDAY', '08:00:00', '16:00:00'),
(21, 'THURSDAY', '08:00:00', '16:00:00'),
(21, 'FRIDAY', '08:00:00', '16:00:00'),
(21, 'SATURDAY', '08:00:00', '14:00:00'),
(21, 'SUNDAY', '08:00:00', '12:00:00');

-- Opinie
INSERT INTO opinions (store_id, user_id, description, stars, reported)
VALUES
    (1, '81e5d01c-7cc2-4213-b3d3-598df3520ff2', 'Świetne owoce, bardzo świeże!', 5, false),
    (2, '9f1c22f3-1a10-4d4e-8b73-72a60b973401', 'Obsługa bardzo miła, polecam!', 4, false),
    (3, 'a134f4e6-b305-45e3-94cd-12ae4e50df3c', 'Super ceny i lokalizacja.', 5,false),
    (4, '8d7f1f12-38b3-4b14-97a4-b1a7631d13b1', 'Jakość mogłaby być lepsza.', 3, false),
    (5, '54ed9b1f-5905-4a98-bd4d-faf263de62d1', 'Zawsze wracam po więcej!', 5, false),
    (6, 'b379f994-2dc7-4f12-802b-1e646f74bdbb', 'Fajne miejsce, eko owoce.', 4, false),
    (7, '07a0fc37-d3a7-408e-a765-098e2c612258', 'Duży wybór i dobra cena.', 5, false),
    (8, 'd4f0aa48-28a5-4bb3-b194-bac4919a5603', 'Lubię ten sklep, lokalnie i smacznie.', 5, false),
    (9, 'e734f6ef-b738-4ed7-9293-7d96e1f7f2e2', 'Średnie ceny, ale dobra jakość.', 4, false),
    (10, '54ed9b1f-5905-4a98-bd4d-faf263de62d1', 'Polecam każdemu!', 5, false),
    (11, 'b379f994-2dc7-4f12-802b-1e646f74bdbb', 'Często tu robię zakupy.', 4, false),
    (12, '07a0fc37-d3a7-408e-a765-098e2c612258', 'Dobre owoce, miła obsługa.', 5, false),
    (13, 'd4f0aa48-28a5-4bb3-b194-bac4919a5603', 'Lubię lokalne produkty.', 5, false),
    (14, 'e734f6ef-b738-4ed7-9293-7d96e1f7f2e2', 'Zawsze świeże owoce!', 5, false),
    (15, '81e5d01c-7cc2-4213-b3d3-598df3520ff2', 'Ceny mogłyby być niższe.', 3, false),
    (16, '9f1c22f3-1a10-4d4e-8b73-72a60b973401', 'Fajne miejsce na zakupy.', 4, false),
    (17, 'a134f4e6-b305-45e3-94cd-12ae4e50df3c', 'Polecam każdemu!', 5, false),
    (18, '8d7f1f12-38b3-4b14-97a4-b1a7631d13b1', 'Świetne owoce i warzywa.', 5, false),
    (19, '54ed9b1f-5905-4a98-bd4d-faf263de62d1', 'Zawsze wracam po więcej!', 5, false),
    (20, 'b379f994-2dc7-4f12-802b-1e646f74bdbb', 'Miła obsługa i dobre ceny.', 4, false);

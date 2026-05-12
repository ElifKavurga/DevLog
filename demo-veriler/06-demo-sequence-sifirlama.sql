-- DevLog demo verisi | PostgreSQL
-- Ortak: JPA tabloları oluşmuş olmalı. Kullanici @Table(name = "kullanici").
-- Şifre 1234 | PBKDF2WithHmacSHA256:2048:obLD1OX2BxgpOktcbX6PkBEiM0RVZneImaq7zN3u/wA=:XUfrT1CsujdMNGLk84rKj3Jtge0mzWYj55V5MZ6TaQs=
-- Hata sonrası 25P02: ROLLBACK; | blog sütun adı için \d blog
-- Adım 6: SERIAL/IDENTITY (tüm INSERT'ler bittikten sonra)

-- SERIAL / IDENTITY dizilerini senkronize et (manuel id sonrası)
SELECT setval(pg_get_serial_sequence('kullanici', 'id'), (SELECT COALESCE(MAX(id), 1) FROM kullanici));
SELECT setval(pg_get_serial_sequence('kategori', 'id'), (SELECT COALESCE(MAX(id), 1) FROM kategori));
SELECT setval(pg_get_serial_sequence('blog', 'id'), (SELECT COALESCE(MAX(id), 1) FROM blog));
SELECT setval(pg_get_serial_sequence('yorum', 'id'), (SELECT COALESCE(MAX(id), 1) FROM yorum));
SELECT setval(pg_get_serial_sequence('bildirim', 'id'), (SELECT COALESCE(MAX(id), 1) FROM bildirim));

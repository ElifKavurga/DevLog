-- DevLog demo verisi | PostgreSQL
-- Ortak: JPA tabloları oluşmuş olmalı. Kullanici @Table(name = "kullanici").
-- Şifre 1234 | PBKDF2WithHmacSHA256:2048:obLD1OX2BxgpOktcbX6PkBEiM0RVZneImaq7zN3u/wA=:XUfrT1CsujdMNGLk84rKj3Jtge0mzWYj55V5MZ6TaQs=
-- Hata sonrası 25P02: ROLLBACK; | blog sütun adı için \d blog
-- Adım 2: kategori (kullanici sonrası)

-- Temizlik (isteğe bağlı):
-- TRUNCATE degerlendirme, yorum, bildirim, blog, kategori, kullanici RESTART IDENTITY CASCADE;

INSERT INTO kategori (id, kategoriadi, slug) VALUES
  (1, 'Yazılım Mühendisliği', 'yazilim-muhendisligi'),
  (2, 'Yapay Zeka', 'yapay-zeka'),
  (3, 'Web Geliştirme', 'web-gelistirme'),
  (4, 'Mobil Uygulama', 'mobil-uygulama'),
  (5, 'Bulut ve DevOps', 'bulut-ve-devops'),
  (6, 'Siber Güvenlik', 'siber-guvenlik');

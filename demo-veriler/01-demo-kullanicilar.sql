-- DevLog demo verisi | PostgreSQL
-- Ortak: JPA tabloları oluşmuş olmalı. Kullanici @Table(name = "kullanici").
-- Şifre 1234 | PBKDF2WithHmacSHA256:2048:obLD1OX2BxgpOktcbX6PkBEiM0RVZneImaq7zN3u/wA=:XUfrT1CsujdMNGLk84rKj3Jtge0mzWYj55V5MZ6TaQs=
-- Hata sonrası 25P02: ROLLBACK; | blog sütun adı için \d blog
-- Adım 1: kullanici

-- Temizlik (isteğe bağlı):
-- TRUNCATE degerlendirme, yorum, bildirim, blog, kategori, kullanici RESTART IDENTITY CASCADE;

INSERT INTO kullanici (id, ad, soyad, "kullaniciadi", eposta, sifre, rol, yazarlik_talep_etti) VALUES
  (1, 'Ahmet', 'Yılmaz', 'ahmetyilmaz', 'ahmet.yilmaz@devlog.demo', 'PBKDF2WithHmacSHA256:2048:obLD1OX2BxgpOktcbX6PkBEiM0RVZneImaq7zN3u/wA=:XUfrT1CsujdMNGLk84rKj3Jtge0mzWYj55V5MZ6TaQs=', 'ADMIN', false),
  (2, 'Elif', 'Kavurga', 'elifkavurga', 'elif.kavurga@devlog.demo', 'PBKDF2WithHmacSHA256:2048:obLD1OX2BxgpOktcbX6PkBEiM0RVZneImaq7zN3u/wA=:XUfrT1CsujdMNGLk84rKj3Jtge0mzWYj55V5MZ6TaQs=', 'YAZAR', false),
  (3, 'Mehmet', 'Demir', 'mehmetdemir', 'mehmet.demir@devlog.demo', 'PBKDF2WithHmacSHA256:2048:obLD1OX2BxgpOktcbX6PkBEiM0RVZneImaq7zN3u/wA=:XUfrT1CsujdMNGLk84rKj3Jtge0mzWYj55V5MZ6TaQs=', 'YAZAR', false),
  (4, 'Zeynep', 'Arslan', 'zeyneparslan', 'zeynep.arslan@devlog.demo', 'PBKDF2WithHmacSHA256:2048:obLD1OX2BxgpOktcbX6PkBEiM0RVZneImaq7zN3u/wA=:XUfrT1CsujdMNGLk84rKj3Jtge0mzWYj55V5MZ6TaQs=', 'YAZAR', false),
  (5, 'Burak', 'Çelik', 'burakcelik', 'burak.celik@devlog.demo', 'PBKDF2WithHmacSHA256:2048:obLD1OX2BxgpOktcbX6PkBEiM0RVZneImaq7zN3u/wA=:XUfrT1CsujdMNGLk84rKj3Jtge0mzWYj55V5MZ6TaQs=', 'YAZAR', false),
  (6, 'Ayşe', 'Öztürk', 'ayseozturk', 'ayse.ozturk@devlog.demo', 'PBKDF2WithHmacSHA256:2048:obLD1OX2BxgpOktcbX6PkBEiM0RVZneImaq7zN3u/wA=:XUfrT1CsujdMNGLk84rKj3Jtge0mzWYj55V5MZ6TaQs=', 'YAZAR', false),
  (7, 'Can', 'Şahin', 'cansahin', 'can.sahin@devlog.demo', 'PBKDF2WithHmacSHA256:2048:obLD1OX2BxgpOktcbX6PkBEiM0RVZneImaq7zN3u/wA=:XUfrT1CsujdMNGLk84rKj3Jtge0mzWYj55V5MZ6TaQs=', 'OKUR', false),
  (8, 'Deniz', 'Yurt', 'denizyurt', 'deniz.yurt@devlog.demo', 'PBKDF2WithHmacSHA256:2048:obLD1OX2BxgpOktcbX6PkBEiM0RVZneImaq7zN3u/wA=:XUfrT1CsujdMNGLk84rKj3Jtge0mzWYj55V5MZ6TaQs=', 'OKUR', false),
  (9, 'Emre', 'Koç', 'emrekoc', 'emre.koc@devlog.demo', 'PBKDF2WithHmacSHA256:2048:obLD1OX2BxgpOktcbX6PkBEiM0RVZneImaq7zN3u/wA=:XUfrT1CsujdMNGLk84rKj3Jtge0mzWYj55V5MZ6TaQs=', 'OKUR', false),
  (10, 'Selin', 'Aydın', 'selinaydin', 'selin.aydin@devlog.demo', 'PBKDF2WithHmacSHA256:2048:obLD1OX2BxgpOktcbX6PkBEiM0RVZneImaq7zN3u/wA=:XUfrT1CsujdMNGLk84rKj3Jtge0mzWYj55V5MZ6TaQs=', 'OKUR', false);

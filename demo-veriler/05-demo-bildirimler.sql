-- DevLog demo verisi | PostgreSQL
-- Ortak: JPA tabloları oluşmuş olmalı. Kullanici @Table(name = "kullanici").
-- Şifre 1234 | PBKDF2WithHmacSHA256:2048:obLD1OX2BxgpOktcbX6PkBEiM0RVZneImaq7zN3u/wA=:XUfrT1CsujdMNGLk84rKj3Jtge0mzWYj55V5MZ6TaQs=
-- Hata sonrası 25P02: ROLLBACK; | blog sütun adı için \d blog
-- Adım 5: bildirim (kullanici + blog sonrası)

-- Temizlik (isteğe bağlı):
-- TRUNCATE degerlendirme, yorum, bildirim, blog, kategori, kullanici RESTART IDENTITY CASCADE;

-- Bildirimler: onaylanan yazılara (uygulamadaki BLOG: öneki ile), reddedilenlere reddedildi mesajı
INSERT INTO bildirim (id, alici_id, mesaj, okundu_mu, tarih) VALUES
  (1, 2, 'BLOG:1:Blog yazınız onaylandı: Spring Boot ile Sağlık Kontrolü ve Metrik Uçları Tasarımı', false, TIMESTAMP '2025-04-15 10:01:00'),
  (2, 3, 'BLOG:2:Blog yazınız onaylandı: PostgreSQL''de Kısmi İndekslerle Sorgu Maliyetini Düşürmek', false, TIMESTAMP '2025-05-15 10:02:00'),
  (3, 4, 'BLOG:3:Blog yazınız onaylandı: Jakarta EE ve CDI ile Katmanlı Mimari Kurmak', false, TIMESTAMP '2025-06-15 10:03:00'),
  (4, 5, 'BLOG:4:Blog yazınız onaylandı: REST API Versiyonlama: Uyumluluk ve Geriye Dönük Destek', false, TIMESTAMP '2025-07-15 10:04:00'),
  (5, 6, 'BLOG:5:Blog yazınız onaylandı: Docker Çok Aşamalı Derleme ile Daha Küçük JVM İmajları', false, TIMESTAMP '2025-08-15 10:05:00'),
  (6, 2, 'BLOG:6:Blog yazınız onaylandı: Git İş Akışlarında Rebase mi Merge mü?', false, TIMESTAMP '2025-09-15 10:06:00'),
  (7, 3, 'BLOG:7:Blog yazınız onaylandı: JUnit 5 ve Mockito ile Servis Sözleşmesi Testleri', false, TIMESTAMP '2025-10-15 10:07:00'),
  (8, 4, 'BLOG:8:Blog yazınız onaylandı: Kafka ile Olay Odaklı Entegrasyona Giriş', false, TIMESTAMP '2025-11-15 10:08:00'),
  (9, 5, 'BLOG:9:Blog yazınız onaylandı: OpenAPI ile Sözleşme Öncelikli API Tasarımı', false, TIMESTAMP '2025-12-15 10:09:00'),
  (10, 6, 'BLOG:10:Blog yazınız onaylandı: Redis Önbellekle Oturum ve Hız Sınırlama Örüntüleri', false, TIMESTAMP '2025-03-15 10:10:00'),
  (11, 2, 'BLOG:11:Blog yazınız onaylandı: React''te Durum Yönetimi: Context ve Özel Kanca Kullanımı', false, TIMESTAMP '2025-04-15 10:11:00'),
  (12, 3, 'BLOG:12:Blog yazınız onaylandı: TypeScript ile Tip Güvenliği ve Daha İyi IDE Deneyimi', false, TIMESTAMP '2025-05-15 10:12:00'),
  (13, 4, 'BLOG:13:Blog yazınız onaylandı: CSS Grid ve Flexbox ile Duyarlı Yerleşim Stratejileri', false, TIMESTAMP '2025-06-15 10:13:00'),
  (14, 5, 'BLOG:14:Blog yazınız onaylandı: Erişilebilir Formlar: Klavye, Ekran Okuyucu ve ARIA', false, TIMESTAMP '2025-07-15 10:14:00'),
  (15, 6, 'BLOG:15:Blog yazınız onaylandı: Node.js Olay Döngüsü ve Bloklamayan G/Ç', false, TIMESTAMP '2025-08-15 10:15:00'),
  (16, 2, 'BLOG:16:Blog yazınız onaylandı: Kotlin ile Android Jetpack Compose Bileşenleri', false, TIMESTAMP '2025-09-15 10:16:00'),
  (17, 3, 'BLOG:17:Blog yazınız onaylandı: SwiftUI''da Durum ve Yan Etki Ayrımı', false, TIMESTAMP '2025-10-15 10:17:00'),
  (18, 4, 'BLOG:18:Blog yazınız onaylandı: Flutter''da Durum Yönetimi: Riverpod Özeti', false, TIMESTAMP '2025-11-15 10:18:00'),
  (19, 5, 'BLOG:19:Blog yazınız onaylandı: iOS ve Android için Ortak API Tasarımı', false, TIMESTAMP '2025-12-15 10:19:00'),
  (20, 6, 'BLOG:20:Blog yazınız onaylandı: Mobil Uygulamalarda Çevrimdışı Önbellek ve Senkronizasyon', false, TIMESTAMP '2025-03-15 10:20:00'),
  (21, 2, 'BLOG:21:Blog yazınız onaylandı: Kubernetes Deployment ve HPA ile Ölçekleme', false, TIMESTAMP '2025-04-15 10:21:00'),
  (22, 3, 'BLOG:22:Blog yazınız onaylandı: Helm Şablonlarında Konfigürasyon Yönetimi', false, TIMESTAMP '2025-05-15 10:22:00'),
  (23, 4, 'BLOG:23:Blog yazınız onaylandı: Terraform ile Altyapıyı Kod Olarak Tanımlamak', false, TIMESTAMP '2025-06-15 10:23:00'),
  (24, 5, 'BLOG:24:Blog yazınız onaylandı: CI/CD Boru Hattında Güvenlik Taraması Aşamaları', false, TIMESTAMP '2025-07-15 10:24:00'),
  (25, 6, 'BLOG:25:Blog yazınız onaylandı: Gözlemlenebilirlik: Log, Metrik ve İzlerin Birlikte Kullanımı', false, TIMESTAMP '2025-08-15 10:25:00'),
  (26, 2, 'BLOG:26:Blog yazınız onaylandı: Prompt Mühendisliği ve LLM Çıktılarının Değerlendirilmesi', false, TIMESTAMP '2025-09-15 10:26:00'),
  (27, 3, 'BLOG:27:Blog yazınız onaylandı: RAG Mimarisi: Belgeleri Vektör Depoda Düzenlemek', false, TIMESTAMP '2025-10-15 10:27:00'),
  (28, 4, 'BLOG:28:Blog yazınız onaylandı: Transformer Modellerinde Bağlam Penceresi ve Maliyet', false, TIMESTAMP '2025-11-15 10:28:00'),
  (29, 5, 'BLOG:29:Blog yazınız onaylandı: Etik Yapay Zeka: Önyargı ve Veri Kalitesi', false, TIMESTAMP '2025-12-15 10:29:00'),
  (30, 6, 'BLOG:30:Blog yazınız onaylandı: Derin Öğrenme Modellerini Üretimde İzlemek', false, TIMESTAMP '2025-03-15 10:30:00'),
  (31, 2, 'BLOG:31:Blog yazınız onaylandı: OWASP Top 10 ve Güvenli Kodlama Kontrol Listesi', false, TIMESTAMP '2025-04-15 10:31:00'),
  (32, 3, 'BLOG:32:Blog yazınız onaylandı: JWT ve Oturum Çerezleri: Tehdit Modeli ve En İyi Uygulamalar', false, TIMESTAMP '2025-05-15 10:32:00'),
  (33, 4, 'BLOG:33:Blog yazınız onaylandı: Parola Saklama: PBKDF2 ve Parametre Seçimi', false, TIMESTAMP '2025-06-15 10:33:00'),
  (34, 5, 'BLOG:34:Blog yazınız onaylandı: SQL Enjeksiyonuna Karşı Parametreli Sorgular', false, TIMESTAMP '2025-07-15 10:34:00'),
  (35, 6, 'BLOG:35:Blog yazınız onaylandı: CORS ve Tarayıcı Güvenlik Başlıkları Özeti', false, TIMESTAMP '2025-08-15 10:35:00'),
  (36, 2, 'BLOG:36:Blog yazınız onaylandı: Mikroservislerde Hata Ayıklama ve Korelasyon Kimliği', false, TIMESTAMP '2025-09-15 10:36:00'),
  (37, 3, 'BLOG:37:Blog yazınız onaylandı: Monolitten Mikroservise Geçişte Sınır Çizimi', false, TIMESTAMP '2025-10-15 10:37:00'),
  (38, 4, 'BLOG:38:Blog yazınız onaylandı: Domain Odaklı Tasarım ve Aggregate Sınırları', false, TIMESTAMP '2025-11-15 10:38:00'),
  (39, 5, 'BLOG:39:Blog yazınız onaylandı: Hexagonal Mimari ile Altyapıdan Bağımsız Testler', false, TIMESTAMP '2025-12-15 10:39:00'),
  (40, 6, 'BLOG:40:Blog yazınız onaylandı: CQRS ve Okuma/Yazma Ayrımının Faydaları', false, TIMESTAMP '2025-03-15 10:40:00'),
  (41, 2, 'BLOG:41:Blog yazınız reddedildi: NoSQL ve İlişkisel Veri: Ne Zaman Hangisi?', false, TIMESTAMP '2025-09-20 16:41:00'),
  (42, 3, 'BLOG:42:Blog yazınız reddedildi: Elasticsearch ile Tam Metin Arama Temelleri', false, TIMESTAMP '2025-10-20 16:42:00'),
  (43, 4, 'BLOG:43:Blog yazınız reddedildi: GraphQL Şema Tasarımında Yaygın Tuzaklar', false, TIMESTAMP '2025-11-20 16:43:00'),
  (44, 5, 'BLOG:44:Blog yazınız reddedildi: WebSockets ve Sunucu Gönderilen Olaylar', false, TIMESTAMP '2025-12-20 16:44:00'),
  (45, 6, 'BLOG:45:Blog yazınız reddedildi: Performans Profilleme: CPU ve Bellek İpuçları', false, TIMESTAMP '2025-04-20 16:45:00'),
  (46, 2, 'BLOG:46:Blog yazınız reddedildi: Bellek Sızıntılarını Bulmak için Heap Dump Okuma', false, TIMESTAMP '2025-05-20 16:46:00');

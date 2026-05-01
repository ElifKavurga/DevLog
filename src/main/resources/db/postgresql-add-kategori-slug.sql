-- Eski DevLog veritabanları: Kategori.slug sütunu yoksa ekleyin (uygulama açılışında da otomatik denenir).
ALTER TABLE kategori ADD COLUMN IF NOT EXISTS slug VARCHAR(200);

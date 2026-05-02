-- Kalıcı yorumlar (EclipseLink create-or-extend-tables ile de oluşabilir; manuel kurulum için)
CREATE TABLE IF NOT EXISTS yorum (
    id BIGSERIAL PRIMARY KEY,
    metin TEXT NOT NULL,
    olusturulma_tarihi TIMESTAMP NOT NULL,
    blog_id BIGINT NOT NULL REFERENCES blog (id) ON DELETE CASCADE,
    kullanici_id BIGINT NOT NULL REFERENCES kullanici (id)
);

CREATE INDEX IF NOT EXISTS idx_yorum_blog ON yorum (blog_id);
CREATE INDEX IF NOT EXISTS idx_yorum_kullanici ON yorum (kullanici_id);

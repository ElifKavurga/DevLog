-- DevLog: yazarlık talebi sütunu + sistem_log tablosu (EJBException / DDL hatalarında manuel çalıştırın)
-- PostgreSQL
--
-- Tam sıfırlama (geliştirme): pgAdmin → Query Tool
--   DROP DATABASE IF EXISTS "DevLog";
--   CREATE DATABASE "DevLog" OWNER postgres;
-- Sonra uygulamayı yeniden deploy edin; EclipseLink tabloları oluşturur, örnek kullanıcılar
-- DataInitializer ile eklenir (admin, okur_demo, yazar_demo, yonetici_demo — şifre 1234).

ALTER TABLE kullanici
    ADD COLUMN IF NOT EXISTS yazarlik_talep_etti BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE kullanici SET yazarlik_talep_etti = FALSE WHERE yazarlik_talep_etti IS NULL;

CREATE TABLE IF NOT EXISTS sistem_log (
    id              BIGSERIAL PRIMARY KEY,
    kullanici_bilgisi VARCHAR(500),
    islem           VARCHAR(2000) NOT NULL,
    tarih           TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_sistem_log_tarih ON sistem_log (tarih DESC);

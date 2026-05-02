CREATE TABLE IF NOT EXISTS bildirim (
    id BIGSERIAL PRIMARY KEY,
    alici_id BIGINT NOT NULL REFERENCES kullanici (id),
    mesaj VARCHAR(2000) NOT NULL,
    okundu_mu BOOLEAN NOT NULL DEFAULT FALSE,
    tarih TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_bildirim_alici ON bildirim (alici_id);
CREATE INDEX IF NOT EXISTS idx_bildirim_alici_okunmamis ON bildirim (alici_id, okundu_mu);

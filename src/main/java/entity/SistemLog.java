package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "sistem_log")
public class SistemLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kullanici_bilgisi", length = 500)
    private String kullaniciBilgisi;

    @Column(name = "islem", length = 2000, nullable = false)
    private String islem;

    @Column(name = "tarih", nullable = false)
    private LocalDateTime tarih;

    public SistemLog() {}

    @PrePersist
    void tarihVarsayilan() {
        if (tarih == null) {
            tarih = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKullaniciBilgisi() {
        return kullaniciBilgisi;
    }

    public void setKullaniciBilgisi(String kullaniciBilgisi) {
        this.kullaniciBilgisi = kullaniciBilgisi;
    }

    public String getIslem() {
        return islem;
    }

    public void setIslem(String islem) {
        this.islem = islem;
    }

    public LocalDateTime getTarih() {
        return tarih;
    }

    public void setTarih(LocalDateTime tarih) {
        this.tarih = tarih;
    }
}

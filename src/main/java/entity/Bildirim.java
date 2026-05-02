package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "bildirim")
public class Bildirim implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alici_id", nullable = false)
    private Kullanici alici;

    @Column(length = 2000, nullable = false)
    private String mesaj;

    @Column(name = "okundu_mu", nullable = false)
    private boolean okunduMu = false;

    @Column(nullable = false)
    private LocalDateTime tarih;

    public Bildirim() {}

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

    public Kullanici getAlici() {
        return alici;
    }

    public void setAlici(Kullanici alici) {
        this.alici = alici;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public boolean isOkunduMu() {
        return okunduMu;
    }

    public void setOkunduMu(boolean okunduMu) {
        this.okunduMu = okunduMu;
    }

    public LocalDateTime getTarih() {
        return tarih;
    }

    public void setTarih(LocalDateTime tarih) {
        this.tarih = tarih;
    }
}

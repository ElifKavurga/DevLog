package entity;

import enums.DurumTip;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "blog")
public class Blog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String baslik;

    @Column(length = 500)
    private String ozet;

    @Column(columnDefinition = "TEXT")
    private String icerik;

    private LocalDateTime olusturulmaTarihi;

    @Enumerated(EnumType.STRING)
    private DurumTip durum;

    @ManyToOne
    private Kullanici yazar;

    @ManyToOne
    private Kategori kategori;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL)
    private List<Degerlendirme> degerlendirmeler;

    public Blog() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getOzet() {
        return ozet;
    }

    public void setOzet(String ozet) {
        this.ozet = ozet;
    }

    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }

    public LocalDateTime getOlusturulmaTarihi() {
        return olusturulmaTarihi;
    }

    public void setOlusturulmaTarihi(LocalDateTime olusturulmaTarihi) {
        this.olusturulmaTarihi = olusturulmaTarihi;
    }

    public DurumTip getDurum() {
        return durum;
    }

    public void setDurum(DurumTip durum) {
        this.durum = durum;
    }

    public Kullanici getYazar() {
        return yazar;
    }

    public void setYazar(Kullanici yazar) {
        this.yazar = yazar;
    }

    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    public List<Degerlendirme> getDegerlendirmeler() {
        return degerlendirmeler;
    }

    public void setDegerlendirmeler(List<Degerlendirme> degerlendirmeler) {
        this.degerlendirmeler = degerlendirmeler;
    }
}

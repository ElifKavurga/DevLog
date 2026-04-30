package entity;

import enums.DurumTip;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "blog")
public class Blog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String baslik;

    @Column(length = 500)
    private String ozet;

    /** Markdown veya düz metin gövde. */
    @Column(columnDefinition = "TEXT")
    private String icerik;

    @Column(name = "kapak_gorseli_url", length = 2048)
    private String kapakGorseliUrl;

    @Column(name = "tahmini_okuma_suresi")
    private Integer tahminiOkumaSuresi;

    /** Virgülle ayrılmış etiketler (örn. "jakarta,jsf,postgres"). */
    @Column(length = 2000)
    private String etiketler;

    private LocalDateTime olusturulmaTarihi;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private DurumTip durum;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "yazar_id", nullable = false)
    private Kullanici yazar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kategori_id")
    private Kategori kategori;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Degerlendirme> degerlendirmeler = new ArrayList<>();

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

    public String getKapakGorseliUrl() {
        return kapakGorseliUrl;
    }

    public void setKapakGorseliUrl(String kapakGorseliUrl) {
        this.kapakGorseliUrl = kapakGorseliUrl;
    }

    public Integer getTahminiOkumaSuresi() {
        return tahminiOkumaSuresi;
    }

    public void setTahminiOkumaSuresi(Integer tahminiOkumaSuresi) {
        this.tahminiOkumaSuresi = tahminiOkumaSuresi;
    }

    public String getEtiketler() {
        return etiketler;
    }

    public void setEtiketler(String etiketler) {
        this.etiketler = etiketler;
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

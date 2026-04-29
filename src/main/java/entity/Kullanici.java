package entity;

import enums.RolTip;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
public class Kullanici implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ad;
    private String soyad;
    private String kullaniciAdi;
    private String eposta;
    private String sifre;

    @Enumerated(EnumType.STRING)
    private RolTip rol;

    @OneToMany(mappedBy = "yazar", cascade = CascadeType.ALL)
    private List<Blog> bloglar;

    @OneToMany(mappedBy = "kullanici", cascade = CascadeType.ALL)
    private List<Degerlendirme> degerlendirmeler;

    public Kullanici() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public String getKullaniciAdi() {
        return kullaniciAdi;
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
    }

    public String getEposta() {
        return eposta;
    }

    public void setEposta(String eposta) {
        this.eposta = eposta;
    }

    public String getSifre() {
        return sifre;
    }

    public void setSifre(String sifre) {
        this.sifre = sifre;
    }

    public RolTip getRol() {
        return rol;
    }

    public void setRol(RolTip rol) {
        this.rol = rol;
    }

    public List<Blog> getBloglar() {
        return bloglar;
    }

    public void setBloglar(List<Blog> bloglar) {
        this.bloglar = bloglar;
    }

    public List<Degerlendirme> getDegerlendirmeler() {
        return degerlendirmeler;
    }

    public void setDegerlendirmeler(List<Degerlendirme> degerlendirmeler) {
        this.degerlendirmeler = degerlendirmeler;
    }
}

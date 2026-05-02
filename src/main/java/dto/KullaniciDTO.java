package dto;

import enums.RolTip;

import java.io.Serializable;

/**
 * Oturum özeti ve iç içe blog/yorum görünümleri için UI alanları.
 */
public class KullaniciDTO implements Serializable {

    private Long id;
    private String ad;
    private String soyad;
    private String kullaniciAdi;
    private String eposta;
    private RolTip rol;
    private boolean yazarlikTalepEtti;

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

    public RolTip getRol() {
        return rol;
    }

    public void setRol(RolTip rol) {
        this.rol = rol;
    }

    public boolean isYazarlikTalepEtti() {
        return yazarlikTalepEtti;
    }

    public void setYazarlikTalepEtti(boolean yazarlikTalepEtti) {
        this.yazarlikTalepEtti = yazarlikTalepEtti;
    }
}

package dto;

import enums.DurumTip;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BlogDTO implements Serializable {

    private Long id;
    private String baslik;
    private String ozet;
    private String icerik;
    private String kapakGorseliUrl;
    private Integer tahminiOkumaSuresi;
    private String etiketler;
    private LocalDateTime olusturulmaTarihi;
    private DurumTip durum;
    private KategoriDTO kategori;
    private KullaniciDTO yazar;
    private List<YorumDTO> yorumlar = new ArrayList<>();

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

    public KategoriDTO getKategori() {
        return kategori;
    }

    public void setKategori(KategoriDTO kategori) {
        this.kategori = kategori;
    }

    public KullaniciDTO getYazar() {
        return yazar;
    }

    public void setYazar(KullaniciDTO yazar) {
        this.yazar = yazar;
    }

    public List<YorumDTO> getYorumlar() {
        return yorumlar;
    }

    public void setYorumlar(List<YorumDTO> yorumlar) {
        this.yorumlar = yorumlar != null ? yorumlar : new ArrayList<>();
    }
}

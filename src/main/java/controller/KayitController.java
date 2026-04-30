package controller;

import entity.Kullanici;
import enums.RolTip;
import facade.KullaniciFacade;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("kayitController")
@RequestScoped
public class KayitController {

    @Inject
    private KullaniciFacade kullaniciFacade;

    @Inject
    private OturumBean oturumBean;

    private String adSoyad;
    private String kullaniciAdi;
    private String eposta;
    private String sifre;
    private String sifreTekrar;
    private String hataMesaji;

    public String kayitOl() {
        hataMesaji = null;
        if (adSoyad == null || adSoyad.isBlank()) {
            hataMesaji = "Ad soyad zorunludur.";
            return null;
        }
        if (kullaniciAdi == null || kullaniciAdi.isBlank()) {
            hataMesaji = "Kullanıcı adı zorunludur.";
            return null;
        }
        if (eposta == null || eposta.isBlank()) {
            hataMesaji = "E-posta zorunludur.";
            return null;
        }
        String trimMail = eposta.trim();
        if (!trimMail.contains("@") || trimMail.indexOf('@') == trimMail.length() - 1) {
            hataMesaji = "Geçerli bir e-posta adresi girin.";
            return null;
        }
        if (sifre == null || sifre.length() < 4) {
            hataMesaji = "Şifre en az 4 karakter olmalıdır.";
            return null;
        }
        if (!sifre.equals(sifreTekrar)) {
            hataMesaji = "Şifreler eşleşmiyor.";
            return null;
        }
        String trimUser = kullaniciAdi.trim();
        if (kullaniciFacade.epostaKullaniliyorMu(trimMail)) {
            hataMesaji = "Bu e-posta ile zaten kayıt var.";
            return null;
        }
        if (kullaniciFacade.kullaniciAdiKullaniliyorMu(trimUser)) {
            hataMesaji = "Bu kullanıcı adı alınmış.";
            return null;
        }

        String[] adParca = adSoyad.trim().split("\\s+", 2);
        String ad = adParca[0];
        String soyad = adParca.length > 1 ? adParca[1] : "";

        Kullanici yeni = new Kullanici();
        yeni.setAd(ad);
        yeni.setSoyad(soyad);
        yeni.setKullaniciAdi(trimUser);
        yeni.setEposta(trimMail);
        yeni.setSifre(sifre);
        yeni.setRol(RolTip.OKUR);

        kullaniciFacade.olustur(yeni);
        oturumBean.girisYap(yeni);
        temizleForm();
        return "/public/index?faces-redirect=true";
    }

    private void temizleForm() {
        adSoyad = null;
        kullaniciAdi = null;
        eposta = null;
        sifre = null;
        sifreTekrar = null;
    }

    public String getAdSoyad() {
        return adSoyad;
    }

    public void setAdSoyad(String adSoyad) {
        this.adSoyad = adSoyad;
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

    public String getSifreTekrar() {
        return sifreTekrar;
    }

    public void setSifreTekrar(String sifreTekrar) {
        this.sifreTekrar = sifreTekrar;
    }

    public String getHataMesaji() {
        return hataMesaji;
    }
}

package controller;

import entity.Kullanici;
import facade.KullaniciFacade;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("giriController")
@RequestScoped
public class GiriController {

    @Inject
    private KullaniciFacade kullaniciFacade;

    @Inject
    private OturumBean oturumBean;

    private String epostaVeyaKullaniciAdi;
    private String sifre;
    private String hataMesaji;

    public String girisYap() {
        hataMesaji = null;
        if (epostaVeyaKullaniciAdi == null || epostaVeyaKullaniciAdi.isBlank()
                || sifre == null || sifre.isEmpty()) {
            hataMesaji = "E-posta veya kullanıcı adı ile şifre zorunludur.";
            return null;
        }
        Kullanici k = kullaniciFacade.girisYapEpostaVeyaKullaniciAdi(epostaVeyaKullaniciAdi.trim(), sifre);
        if (k == null) {
            hataMesaji = "Bilgiler eşleşmedi. E-posta / kullanıcı adı veya şifreyi kontrol edin.";
            return null;
        }
        oturumBean.girisYap(k);
        return "/public/index?faces-redirect=true";
    }

    public String getEpostaVeyaKullaniciAdi() {
        return epostaVeyaKullaniciAdi;
    }

    public void setEpostaVeyaKullaniciAdi(String epostaVeyaKullaniciAdi) {
        this.epostaVeyaKullaniciAdi = epostaVeyaKullaniciAdi;
    }

    public String getSifre() {
        return sifre;
    }

    public void setSifre(String sifre) {
        this.sifre = sifre;
    }

    public String getHataMesaji() {
        return hataMesaji;
    }
}

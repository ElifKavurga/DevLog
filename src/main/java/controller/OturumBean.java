package controller;

import entity.Kullanici;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;

/**
 * Oturum açmış kullanıcı bilgisi (HTTP oturumu ile aynı ömür).
 */
@Named("oturum")
@SessionScoped
public class OturumBean implements Serializable {

    private Kullanici aktifKullanici;

    public Kullanici getAktifKullanici() {
        return aktifKullanici;
    }

    public boolean isGirisYapildi() {
        return aktifKullanici != null;
    }

    public void girisYap(Kullanici kullanici) {
        this.aktifKullanici = kullanici;
    }

    public String cikisYap() {
        aktifKullanici = null;
        return "/public/index?faces-redirect=true";
    }
}

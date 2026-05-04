package bean.admin;

import bean.OturumBean;
import bean.panel.ProfilBean;
import entity.Kullanici;
import entity.SistemLog;
import enums.RolTip;
import facadeLocal.KullaniciFacadeLocal;
import facadeLocal.SistemLogFacadeLocal;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named("yazarTalepBean")
@ViewScoped
public class YazarTalepBean implements Serializable {

    @Inject
    private OturumBean oturum;

    @Inject
    private KullaniciFacadeLocal kullaniciFacade;

    @Inject
    private SistemLogFacadeLocal sistemLogFacade;

    private List<Kullanici> talepler = new ArrayList<>();

    public String hazirla() {
        if (!oturum.isGirisYapildi()) {
            return "/auth/giris?faces-redirect=true";
        }
        if (!oturum.isAdmin()) {
            return "/panel/bloglarim?faces-redirect=true";
        }
        yukleListe();
        return null;
    }

    private void yukleListe() {
        List<Kullanici> list = kullaniciFacade.yazarlikTalebiBekleyenleriListele();
        talepler = list != null ? new ArrayList<>(list) : new ArrayList<>();
    }

    public String onayla(Long kullaniciId) {
        if (!oturum.isAdmin()) {
            return null;
        }
        Kullanici k = kullaniciFacade.bul(kullaniciId);
        if (k == null || !k.isYazarlikTalepEtti()) {
            yukleListe();
            return null;
        }
        k.setRol(RolTip.YAZAR);
        k.setYazarlikTalepEtti(false);
        kullaniciFacade.guncelle(k);
        logYaz(k, "Kullanıcı yazar yapıldı.");
        yukleListe();
        return null;
    }

    public String reddet(Long kullaniciId) {
        if (!oturum.isAdmin()) {
            return null;
        }
        Kullanici k = kullaniciFacade.bul(kullaniciId);
        if (k == null || !k.isYazarlikTalepEtti()) {
            yukleListe();
            return null;
        }
        k.setYazarlikTalepEtti(false);
        kullaniciFacade.guncelle(k);
        logYaz(k, "Yazarlık talebi reddedildi.");
        yukleListe();
        return null;
    }

    private void logYaz(Kullanici k, String islem) {
        SistemLog log = new SistemLog();
        log.setKullaniciBilgisi(ProfilBean.kullaniciLogKimligi(k));
        log.setIslem(islem);
        log.setTarih(LocalDateTime.now());
        sistemLogFacade.olustur(log);
    }

    public List<Kullanici> getTalepler() {
        return talepler;
    }

    public int getTalepSayisi() {
        return talepler.size();
    }

    public String kullaniciOzeti(Kullanici k) {
        if (k == null) {
            return "—";
        }
        String ad = (k.getAd() != null ? k.getAd() : "").trim();
        String soy = (k.getSoyad() != null ? k.getSoyad() : "").trim();
        String birlesik = (ad + " " + soy).trim();
        if (!birlesik.isEmpty()) {
            return birlesik;
        }
        if (k.getKullaniciAdi() != null && !k.getKullaniciAdi().isBlank()) {
            return k.getKullaniciAdi();
        }
        return "—";
    }

    public String kullaniciHarfi(Kullanici k) {
        String oz = kullaniciOzeti(k);
        if (oz == null || oz.isBlank() || "—".equals(oz)) {
            return "?";
        }
        return oz.substring(0, 1).toUpperCase();
    }
}

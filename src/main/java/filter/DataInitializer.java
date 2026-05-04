package filter;

import entity.Kategori;
import entity.Kullanici;
import enums.RolTip;
import facadeLocal.KategoriFacadeLocal;
import facadeLocal.KullaniciFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;

import java.util.List;

@Singleton
public class DataInitializer {

    @EJB
    private KullaniciFacadeLocal kullaniciFacade;

    @EJB
    private KategoriFacadeLocal kategoriFacade;

    public void seedDefaults() {
        seedAdminIfAbsent();
        seedDefaultKategoriIfAbsent();
        seedOrnekOkurIfAbsent();
        seedOrnekYazarIfAbsent();
        seedOrnekYoneticiIfAbsent();
    }

    private void seedAdminIfAbsent() {
        if (kullaniciFacade.kullaniciAdiKullaniliyorMu("admin")) {
            return;
        }
        Kullanici admin = new Kullanici();
        admin.setKullaniciAdi("admin");
        admin.setSifre("1234");
        admin.setEposta("admin@devlog.com");
        admin.setAd("System");
        admin.setSoyad("Admin");
        admin.setRol(RolTip.ADMIN);
        kullaniciFacade.olustur(admin);
    }

    private void seedDefaultKategoriIfAbsent() {
        List<Kategori> mevcut = kategoriFacade.listele();
        if (mevcut != null && !mevcut.isEmpty()) {
            return;
        }
        Kategori genel = new Kategori();
        genel.setIsim("Genel");
        genel.setSlug("genel");
        kategoriFacade.olustur(genel);
    }

    private void seedOrnekOkurIfAbsent() {
        if (kullaniciFacade.kullaniciAdiKullaniliyorMu("okur_demo")) {
            return;
        }
        Kullanici u = new Kullanici();
        u.setKullaniciAdi("okur_demo");
        u.setSifre("1234");
        u.setEposta("okur_demo@devlog.com");
        u.setAd("Demo");
        u.setSoyad("Okur");
        u.setRol(RolTip.OKUR);
        u.setYazarlikTalepEtti(false);
        kullaniciFacade.olustur(u);
    }

    private void seedOrnekYazarIfAbsent() {
        if (kullaniciFacade.kullaniciAdiKullaniliyorMu("yazar_demo")) {
            return;
        }
        Kullanici u = new Kullanici();
        u.setKullaniciAdi("yazar_demo");
        u.setSifre("1234");
        u.setEposta("yazar_demo@devlog.com");
        u.setAd("Demo");
        u.setSoyad("Yazar");
        u.setRol(RolTip.YAZAR);
        u.setYazarlikTalepEtti(false);
        kullaniciFacade.olustur(u);
    }

    private void seedOrnekYoneticiIfAbsent() {
        if (kullaniciFacade.kullaniciAdiKullaniliyorMu("yonetici_demo")) {
            return;
        }
        Kullanici u = new Kullanici();
        u.setKullaniciAdi("yonetici_demo");
        u.setSifre("1234");
        u.setEposta("yonetici_demo@devlog.com");
        u.setAd("Demo");
        u.setSoyad("Yönetici");
        u.setRol(RolTip.YONETICI);
        u.setYazarlikTalepEtti(false);
        kullaniciFacade.olustur(u);
    }
}

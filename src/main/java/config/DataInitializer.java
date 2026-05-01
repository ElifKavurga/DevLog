package config;

import entity.Kategori;
import entity.Kullanici;
import enums.RolTip;
import facadeLocal.KategoriFacadeLocal;
import facadeLocal.KullaniciFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;

import java.util.List;

/**
 * Uygulama açılışında varsayılan veriler (admin kullanıcısı, boşsa varsayılan kategori).
 * Tetikleme: {@link DevLogSeedListener} (web bağlamı hazır olduktan sonra; şema uyumu için).
 */
@Singleton
public class DataInitializer {

    @EJB
    private KullaniciFacadeLocal kullaniciFacade;

    @EJB
    private KategoriFacadeLocal kategoriFacade;

    public void seedDefaults() {
        seedAdminIfAbsent();
        seedDefaultKategoriIfAbsent();
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
}

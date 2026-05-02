package controller.panel;

import controller.OturumBean;
import entity.Bildirim;
import facadeLocal.BildirimFacadeLocal;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Oturum boyunca bildirim listesi önbelleği; zil tıklanınca okundu işaretlenir.
 */
@Named("bildirimController")
@SessionScoped
public class BildirimController implements Serializable {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.forLanguageTag("tr"));

    @Inject
    private BildirimFacadeLocal bildirimFacade;

    @Inject
    private OturumBean oturum;

    private List<Bildirim> bildirimler = new ArrayList<>();

    public String hazirla() {
        if (!oturum.isGirisYapildi() || oturum.getAktifKullanici() == null) {
            return "/auth/giris?faces-redirect=true";
        }
        Long kid = oturum.getAktifKullanici().getId();
        List<Bildirim> list = bildirimFacade.kullaniciyaGoreBildirimleriGetir(kid);
        bildirimler = list != null ? new ArrayList<>(list) : new ArrayList<>();
        return null;
    }

    /**
     * Zil: tüm okunmamışları okundu yap, bildirimler sayfasına yönlendir.
     */
    public String zilTiklaVeListeyeGit() {
        if (!oturum.isGirisYapildi() || oturum.getAktifKullanici() == null) {
            return null;
        }
        bildirimFacade.tumunuOkunduYap(oturum.getAktifKullanici().getId());
        return "/panel/bildirimler?faces-redirect=true";
    }

    public int getOkunmamisSayisi() {
        if (!oturum.isGirisYapildi() || oturum.getAktifKullanici() == null) {
            return 0;
        }
        long c = bildirimFacade.kullaniciyaGoreOkunmamisBildirimSayisi(oturum.getAktifKullanici().getId());
        return (int) Math.min(c, 99L);
    }

    public List<Bildirim> getBildirimler() {
        return bildirimler;
    }

    public String tarihMetni(Bildirim b) {
        if (b == null || b.getTarih() == null) {
            return "—";
        }
        return b.getTarih().format(FMT);
    }

    public String okunduEtiketi(Bildirim b) {
        if (b == null) {
            return "";
        }
        return b.isOkunduMu() ? "READ" : "NEW";
    }
}

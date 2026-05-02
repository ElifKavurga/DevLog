package controller.panel;

import controller.OturumController;
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
    private OturumController oturum;

    private List<Bildirim> bildirimler = new ArrayList<>();

    public String hazirla() {
        if (!oturum.isGirisYapildi() || oturum.getAktifKullaniciEntity() == null) {
            return "/auth/giris?faces-redirect=true";
        }
        Long kid = oturum.getAktifKullaniciEntity().getId();
        List<Bildirim> list = bildirimFacade.kullaniciyaGoreBildirimleriGetir(kid);
        bildirimler = list != null ? new ArrayList<>(list) : new ArrayList<>();
        return null;
    }

    /**
     * Zil: tüm okunmamışları okundu yap, bildirimler sayfasına yönlendir.
     */
    public String zilTiklaVeListeyeGit() {
        if (!oturum.isGirisYapildi() || oturum.getAktifKullaniciEntity() == null) {
            return null;
        }
        bildirimFacade.tumunuOkunduYap(oturum.getAktifKullaniciEntity().getId());
        return "/panel/bildirimler?faces-redirect=true";
    }

    public int getOkunmamisSayisi() {
        if (!oturum.isGirisYapildi() || oturum.getAktifKullaniciEntity() == null) {
            return 0;
        }
        long c = bildirimFacade.kullaniciyaGoreOkunmamisBildirimSayisi(oturum.getAktifKullaniciEntity().getId());
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

    public boolean bildirimBlogDetayLinkli(Bildirim bn) {
        return bn != null && bn.getMesaj() != null && bn.getMesaj().startsWith("BLOG:");
    }

    public Long bildirimBlogDetayId(Bildirim bn) {
        if (!bildirimBlogDetayLinkli(bn)) {
            return null;
        }
        String[] p = bn.getMesaj().split(":", 3);
        if (p.length >= 2) {
            try {
                return Long.parseLong(p[1].trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    public boolean bildirimAdminOnayLinkli(Bildirim bn) {
        return bn != null && bn.getMesaj() != null && bn.getMesaj().startsWith("ANLIK:ONAY_BEKLEYEN:");
    }

    public boolean bildirimYazarTalepLinkli(Bildirim bn) {
        return bn != null && bn.getMesaj() != null && bn.getMesaj().startsWith("ANLIK:YAZAR_TALEPLERI:");
    }
}

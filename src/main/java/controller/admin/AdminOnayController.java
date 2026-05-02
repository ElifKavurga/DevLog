package controller.admin;

import controller.OturumBean;
import controller.panel.ProfilController;
import entity.Blog;
import entity.Kullanici;
import entity.SistemLog;
import enums.DurumTip;
import enums.RolTip;
import facadeLocal.BildirimFacadeLocal;
import facadeLocal.BlogFacadeLocal;
import facadeLocal.SistemLogFacadeLocal;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Named("adminOnayController")
@ViewScoped
public class AdminOnayController implements Serializable {

    private static final DateTimeFormatter TARIH_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Inject
    private BlogFacadeLocal blogFacade;

    @Inject
    private OturumBean oturum;

    @Inject
    private SistemLogFacadeLocal sistemLogFacade;

    @Inject
    private BildirimFacadeLocal bildirimFacade;

    private List<Blog> bekleyenler = new ArrayList<>();

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
        List<Blog> list = blogFacade.onayBekleyenleriListele();
        bekleyenler = list != null ? new ArrayList<>(list) : new ArrayList<>();
    }

    public String onayla(Long blogId) {
        if (!oturum.isAdmin()) {
            return null;
        }
        Blog b = blogFacade.bul(blogId);
        if (b == null || b.getDurum() != DurumTip.ONAY_BEKLIYOR) {
            yukleListe();
            return null;
        }
        b.setDurum(DurumTip.YAYINLANDI);
        blogFacade.guncelle(b);
        adminLogKaydet("Admin bir blog yazısını onayladı.");
        if (b.getYazar() != null && b.getYazar().getId() != null && b.getYazar().getRol() == RolTip.YAZAR) {
            String baslikEt = b.getBaslik() != null && !b.getBaslik().isBlank() ? b.getBaslik().trim() : "Başlıksız";
            bildirimFacade.aliciyaMesajOlustur(b.getYazar().getId(),
                    "BLOG:" + b.getId() + ":Blog yazınız onaylandı: " + baslikEt);
        }
        yukleListe();
        return null;
    }

    public String reddet(Long blogId) {
        if (!oturum.isAdmin()) {
            return null;
        }
        Blog b = blogFacade.bul(blogId);
        if (b == null || b.getDurum() != DurumTip.ONAY_BEKLIYOR) {
            yukleListe();
            return null;
        }
        b.setDurum(DurumTip.REDDEDILDI);
        blogFacade.guncelle(b);
        adminLogKaydet("Admin bir blog yazısını reddetti.");
        yukleListe();
        return null;
    }

    private void adminLogKaydet(String islem) {
        Kullanici admin = oturum.getAktifKullanici();
        SistemLog log = new SistemLog();
        log.setKullaniciBilgisi(ProfilController.kullaniciLogKimligi(admin));
        log.setIslem(islem);
        log.setTarih(LocalDateTime.now());
        sistemLogFacade.olustur(log);
    }

    /** Ön izleme sayfası: onay sonrası onay kuyruğuna dön. */
    public String onaylaVeKuyregeGit(Long blogId) {
        onayla(blogId);
        return "/admin/admin-onay?faces-redirect=true";
    }

    /** Ön izleme sayfası: ret sonrası onay kuyruğuna dön. */
    public String reddetVeKuyregeGit(Long blogId) {
        reddet(blogId);
        return "/admin/admin-onay?faces-redirect=true";
    }

    public String yenile() {
        if (!oturum.isAdmin()) {
            return null;
        }
        yukleListe();
        return null;
    }

    public List<Blog> getBekleyenler() {
        return bekleyenler;
    }

    public int getBekleyenSayisi() {
        return bekleyenler.size();
    }

    public String yazarOzeti(Blog blog) {
        if (blog == null || blog.getYazar() == null) {
            return "—";
        }
        Kullanici y = blog.getYazar();
        String ad = (y.getAd() != null ? y.getAd() : "").trim();
        String soy = (y.getSoyad() != null ? y.getSoyad() : "").trim();
        String birlesik = (ad + " " + soy).trim();
        if (!birlesik.isEmpty()) {
            return birlesik;
        }
        if (y.getKullaniciAdi() != null && !y.getKullaniciAdi().isBlank()) {
            return y.getKullaniciAdi();
        }
        return "—";
    }

    public String yazarHarfi(Blog blog) {
        String oz = yazarOzeti(blog);
        if (oz == null || oz.isBlank() || "—".equals(oz)) {
            return "?";
        }
        return oz.substring(0, 1).toUpperCase();
    }

    public String kategoriEtiket(Blog blog) {
        if (blog == null || blog.getKategori() == null) {
            return "—";
        }
        String isim = blog.getKategori().getIsim();
        return isim != null && !isim.isBlank() ? isim : "—";
    }

    public String tarihMetni(Blog blog) {
        if (blog == null || blog.getOlusturulmaTarihi() == null) {
            return "—";
        }
        return blog.getOlusturulmaTarihi().format(TARIH_FMT);
    }
}

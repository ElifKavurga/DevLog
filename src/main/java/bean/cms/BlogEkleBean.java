package bean.cms;

import bean.OturumBean;
import bean.panel.ProfilBean;
import entity.Blog;
import entity.Kategori;
import entity.Kullanici;
import entity.SistemLog;
import enums.DurumTip;
import enums.RolTip;
import facadeLocal.BildirimFacadeLocal;
import facadeLocal.BlogFacadeLocal;
import facadeLocal.KategoriFacadeLocal;
import facadeLocal.KullaniciFacadeLocal;
import facadeLocal.SistemLogFacadeLocal;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named("blogEkleBean")
@ViewScoped
public class BlogEkleBean implements Serializable {

    @Inject
    private BlogFacadeLocal blogFacade;

    @Inject
    private KategoriFacadeLocal kategoriFacade;

    @Inject
    private OturumBean oturum;

    @Inject
    private SistemLogFacadeLocal sistemLogFacade;

    @Inject
    private BildirimFacadeLocal bildirimFacade;

    @Inject
    private KullaniciFacadeLocal kullaniciFacade;

    private List<Kategori> kategoriler = new ArrayList<>();

    private Blog yeniBlog = new Blog();

    private Long kategoriId;
    private String hataMesaji;

    @PostConstruct
    public void init() {
        yukleKategoriler();
    }

    private void yukleKategoriler() {
        List<Kategori> raw = kategoriFacade.listele();
        kategoriler = raw != null ? new ArrayList<>(raw) : new ArrayList<>();
    }

    public String hazirla() {
        if (!oturum.isGirisYapildi()) {
            return "/auth/giris?faces-redirect=true";
        }
        if (!oturum.isYazmayaYetkili()) {
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Yetkisiz erişim", "Blog oluşturma yalnızca yazar ve yöneticiler içindir."));
            fc.getExternalContext().getFlash().setKeepMessages(true);
            return "/public/index?faces-redirect=true";
        }
        if (kategoriler == null || kategoriler.isEmpty()) {
            yukleKategoriler();
        }
        return null;
    }

    public String kaydet() {
        hataMesaji = null;
        if (!oturum.isGirisYapildi() || oturum.getAktifKullaniciEntity() == null) {
            return "/auth/giris?faces-redirect=true";
        }
        if (!oturum.isYazmayaYetkili()) {
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Yetkisiz erişim", "Blog kaydetme yalnızca yazar ve yöneticiler içindir."));
            fc.getExternalContext().getFlash().setKeepMessages(true);
            return "/public/index?faces-redirect=true";
        }
        if (yeniBlog.getBaslik() == null || yeniBlog.getBaslik().isBlank()) {
            hataMesaji = "Başlık zorunludur.";
            return null;
        }
        if (kategoriId == null) {
            hataMesaji = "Kategori seçin.";
            return null;
        }
        Kategori kat = kategoriFacade.bul(kategoriId);
        if (kat == null) {
            hataMesaji = "Geçersiz kategori.";
            return null;
        }

        Kullanici yazar = oturum.getAktifKullaniciEntity();
        Blog b = yeniBlogdanKaliciBlog(yeniBlog, kat, yazar, LocalDateTime.now(), DurumTip.ONAY_BEKLIYOR);

        blogFacade.olustur(b);
        SistemLog log = new SistemLog();
        log.setKullaniciBilgisi(ProfilBean.kullaniciLogKimligi(yazar));
        log.setIslem("Kullanıcı yeni bir blog yazısı oluşturdu.");
        log.setTarih(LocalDateTime.now());
        sistemLogFacade.olustur(log);
        String baslikEt = b.getBaslik() != null && !b.getBaslik().isBlank() ? b.getBaslik().trim() : "Başlıksız";
        for (Kullanici admin : kullaniciFacade.rolIleListele(RolTip.ADMIN)) {
            if (admin != null && admin.getId() != null) {
                bildirimFacade.aliciyaMesajOlustur(admin.getId(),
                        "ANLIK:ONAY_BEKLEYEN:Onay bekleyen yeni bir blog eklendi: " + baslikEt);
            }
        }
        temizleForm();
        return "/panel/bloglarim?faces-redirect=true";
    }

    private static Blog yeniBlogdanKaliciBlog(Blog form, Kategori kategori, Kullanici yazar,
                                              LocalDateTime olusturulmaTarihi, DurumTip durum) {
        if (form == null) {
            throw new IllegalArgumentException("form");
        }
        if (kategori == null || yazar == null) {
            throw new IllegalArgumentException("kategori/yazar");
        }
        Blog b = new Blog();
        b.setBaslik(form.getBaslik() != null ? form.getBaslik().trim() : null);
        b.setOzet(form.getOzet() != null ? form.getOzet().trim() : null);
        b.setIcerik(form.getIcerik());
        b.setTahminiOkumaSuresi(form.getTahminiOkumaSuresi());
        b.setKapakGorseliUrl(form.getKapakGorseliUrl() != null && !form.getKapakGorseliUrl().isBlank()
                ? form.getKapakGorseliUrl().trim() : null);
        b.setEtiketler(form.getEtiketler() != null && !form.getEtiketler().isBlank() ? form.getEtiketler().trim() : null);
        b.setOlusturulmaTarihi(olusturulmaTarihi);
        b.setDurum(durum);
        b.setYazar(yazar);
        b.setKategori(kategori);
        return b;
    }

    private void temizleForm() {
        yeniBlog = new Blog();
        kategoriId = null;
    }

    public List<Kategori> getKategoriler() {
        return kategoriler;
    }

    public Blog getYeniBlog() {
        return yeniBlog;
    }

    public void setYeniBlog(Blog yeniBlog) {
        if (yeniBlog != null) {
            this.yeniBlog = yeniBlog;
        }
    }

    public Long getKategoriId() {
        return kategoriId;
    }

    public void setKategoriId(Long kategoriId) {
        this.kategoriId = kategoriId;
    }

    public String getHataMesaji() {
        return hataMesaji;
    }
}

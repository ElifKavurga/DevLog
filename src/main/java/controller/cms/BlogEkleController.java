package controller.cms;

import controller.OturumBean;
import controller.panel.ProfilController;
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

@Named("blogEkleController")
@ViewScoped
public class BlogEkleController implements Serializable {

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

    private String baslik;
    private Long kategoriId;
    private String ozet;
    private String icerik;
    private Integer tahminiOkumaSuresi;
    private String kapakGorseliUrl;
    private String etiketler;
    private String hataMesaji;

    @PostConstruct
    public void init() {
        kategoriler = kategoriFacade.listele();
        if (kategoriler == null) {
            kategoriler = new ArrayList<>();
        }
    }

    /**
     * Giriş zorunluluğu; view açılışında çağrılır.
     */
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
            kategoriler = kategoriFacade.listele();
            if (kategoriler == null) {
                kategoriler = new ArrayList<>();
            }
        }
        return null;
    }

    public String kaydet() {
        hataMesaji = null;
        if (!oturum.isGirisYapildi() || oturum.getAktifKullanici() == null) {
            return "/auth/giris?faces-redirect=true";
        }
        if (!oturum.isYazmayaYetkili()) {
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Yetkisiz erişim", "Blog kaydetme yalnızca yazar ve yöneticiler içindir."));
            fc.getExternalContext().getFlash().setKeepMessages(true);
            return "/public/index?faces-redirect=true";
        }
        if (baslik == null || baslik.isBlank()) {
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

        Kullanici yazar = oturum.getAktifKullanici();
        Blog b = new Blog();
        b.setBaslik(baslik.trim());
        b.setOzet(ozet != null ? ozet.trim() : null);
        b.setIcerik(icerik != null ? icerik : null);
        b.setTahminiOkumaSuresi(tahminiOkumaSuresi);
        b.setKapakGorseliUrl(kapakGorseliUrl != null && !kapakGorseliUrl.isBlank() ? kapakGorseliUrl.trim() : null);
        b.setEtiketler(etiketler != null && !etiketler.isBlank() ? etiketler.trim() : null);
        b.setOlusturulmaTarihi(LocalDateTime.now());
        b.setDurum(DurumTip.ONAY_BEKLIYOR);
        b.setYazar(yazar);
        b.setKategori(kat);

        blogFacade.olustur(b);
        SistemLog log = new SistemLog();
        log.setKullaniciBilgisi(ProfilController.kullaniciLogKimligi(yazar));
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

    private void temizleForm() {
        baslik = null;
        kategoriId = null;
        ozet = null;
        icerik = null;
        tahminiOkumaSuresi = null;
        kapakGorseliUrl = null;
        etiketler = null;
    }

    public List<Kategori> getKategoriler() {
        return kategoriler;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public Long getKategoriId() {
        return kategoriId;
    }

    public void setKategoriId(Long kategoriId) {
        this.kategoriId = kategoriId;
    }

    public String getOzet() {
        return ozet;
    }

    public void setOzet(String ozet) {
        this.ozet = ozet;
    }

    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }

    public Integer getTahminiOkumaSuresi() {
        return tahminiOkumaSuresi;
    }

    public void setTahminiOkumaSuresi(Integer tahminiOkumaSuresi) {
        this.tahminiOkumaSuresi = tahminiOkumaSuresi;
    }

    public String getKapakGorseliUrl() {
        return kapakGorseliUrl;
    }

    public void setKapakGorseliUrl(String kapakGorseliUrl) {
        this.kapakGorseliUrl = kapakGorseliUrl;
    }

    public String getEtiketler() {
        return etiketler;
    }

    public void setEtiketler(String etiketler) {
        this.etiketler = etiketler;
    }

    public String getHataMesaji() {
        return hataMesaji;
    }
}

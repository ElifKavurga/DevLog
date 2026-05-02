package controller.cms;

import controller.OturumController;
import controller.panel.ProfilController;
import dto.BlogDTO;
import dto.KategoriDTO;
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
import mapper.BlogMapper;
import mapper.KategoriMapper;

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
    private OturumController oturum;

    @Inject
    private SistemLogFacadeLocal sistemLogFacade;

    @Inject
    private BildirimFacadeLocal bildirimFacade;

    @Inject
    private KullaniciFacadeLocal kullaniciFacade;

    private List<KategoriDTO> kategoriler = new ArrayList<>();

    /** Form alanları (Markdown gövdesi dahil); JSF doğrudan özelliklere bağlanır. */
    private BlogDTO blogDTO = new BlogDTO();

    private Long kategoriId;
    private String hataMesaji;

    @PostConstruct
    public void init() {
        yukleKategoriler();
    }

    private void yukleKategoriler() {
        List<Kategori> raw = kategoriFacade.listele();
        kategoriler = KategoriMapper.toDtoList(raw != null ? raw : List.of());
    }

    /**
     * Sayfa ilk açılışında yetki ve kategori listesi. POST geri gönderiminde tekrar çalışmaz
     * (model güncellemesi ve Markdown senkronu için).
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
        if (blogDTO.getBaslik() == null || blogDTO.getBaslik().isBlank()) {
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
        Blog b = BlogMapper.toNewBlogEntity(blogDTO, kat, yazar, LocalDateTime.now(), DurumTip.ONAY_BEKLIYOR);

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
        blogDTO = new BlogDTO();
        kategoriId = null;
    }

    public List<KategoriDTO> getKategoriler() {
        return kategoriler;
    }

    public BlogDTO getBlogDTO() {
        return blogDTO;
    }

    public void setBlogDTO(BlogDTO blogDTO) {
        if (blogDTO != null) {
            this.blogDTO = blogDTO;
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

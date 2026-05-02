package controller.cms;

import controller.OturumController;
import dto.BlogDTO;
import entity.Blog;
import enums.DurumTip;
import facadeLocal.BlogFacadeLocal;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mapper.BlogMapper;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Named("yazarPanelController")
@ViewScoped
public class YazarPanelController implements Serializable {

    private static final DateTimeFormatter OLUSTURMA_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Inject
    private BlogFacadeLocal blogFacade;

    @Inject
    private OturumController oturum;

    private List<BlogDTO> bloglar = new ArrayList<>();

    public String hazirla() {
        if (!oturum.isGirisYapildi() || oturum.getAktifKullaniciEntity() == null) {
            return "/auth/giris?faces-redirect=true";
        }
        if (!oturum.isYazmayaYetkili()) {
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Erişim yok", "Bloglarım yalnızca yazar ve yöneticiler içindir."));
            fc.getExternalContext().getFlash().setKeepMessages(true);
            return "/public/index?faces-redirect=true";
        }
        List<Blog> raw = blogFacade.yazaraGoreListele(oturum.getAktifKullaniciEntity().getId());
        bloglar = BlogMapper.toListItems(raw != null ? raw : List.of());
        return null;
    }

    public List<BlogDTO> getBloglar() {
        return bloglar;
    }

    public int getToplamBlogSayisi() {
        return bloglar.size();
    }

    public int getYayindaBlogSayisi() {
        return (int) bloglar.stream().filter(b -> b.getDurum() == DurumTip.YAYINLANDI).count();
    }

    public int getOnayBekleyenSayisi() {
        return (int) bloglar.stream()
                .filter(b -> b.getDurum() == DurumTip.ONAY_BEKLIYOR || b.getDurum() == DurumTip.BEKLIYOR)
                .count();
    }

    public int getDigerDurumSayisi() {
        return (int) bloglar.stream()
                .filter(b -> b.getDurum() != DurumTip.YAYINLANDI
                        && b.getDurum() != DurumTip.ONAY_BEKLIYOR
                        && b.getDurum() != DurumTip.BEKLIYOR)
                .count();
    }

    public String olusturulmaMetni(BlogDTO blog) {
        if (blog == null || blog.getOlusturulmaTarihi() == null) {
            return "—";
        }
        return blog.getOlusturulmaTarihi().format(OLUSTURMA_FMT);
    }

    public String durumDotSinifi(DurumTip durum) {
        if (durum == null) {
            return "bg-secondary";
        }
        return switch (durum) {
            case YAYINLANDI -> "bg-success";
            case ONAY_BEKLIYOR, BEKLIYOR -> "bg-warning";
            case REDDEDILDI -> "bg-danger";
            case TASLAK -> "bg-secondary";
        };
    }

    public String kategoriBaslik(BlogDTO blog) {
        if (blog == null || blog.getKategori() == null) {
            return "—";
        }
        String isim = blog.getKategori().getIsim();
        return isim != null && !isim.isBlank() ? isim : "—";
    }

    public String durumEtiketi(DurumTip durum) {
        if (durum == null) {
            return "—";
        }
        return switch (durum) {
            case YAYINLANDI -> "Yayında";
            case ONAY_BEKLIYOR, BEKLIYOR -> "Onay bekliyor";
            case REDDEDILDI -> "Reddedildi";
            case TASLAK -> "Taslak";
        };
    }

    public String durumRenkSinifi(DurumTip durum) {
        if (durum == null) {
            return "text-secondary";
        }
        return switch (durum) {
            case YAYINLANDI -> "text-success";
            case ONAY_BEKLIYOR, BEKLIYOR -> "text-warning";
            case REDDEDILDI -> "text-danger";
            case TASLAK -> "text-secondary";
        };
    }

    public String ortalamaMetin(Long blogId) {
        if (blogId == null) {
            return "—";
        }
        Double v = blogFacade.ortalamaPuan(blogId);
        if (v == null) {
            return "—";
        }
        return String.format("%.1f", v);
    }
}

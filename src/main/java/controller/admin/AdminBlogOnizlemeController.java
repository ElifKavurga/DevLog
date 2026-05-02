package controller.admin;

import controller.OturumBean;
import entity.Blog;
import entity.Kullanici;
import enums.DurumTip;
import facadeLocal.BlogFacadeLocal;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Admin: onay kuyruğundaki blogu public detay ile aynı blok görünümünde ön izler.
 */
@Named("adminBlogOnizlemeController")
@ViewScoped
public class AdminBlogOnizlemeController implements Serializable {

    private static final DateTimeFormatter TARIH_FMT = DateTimeFormatter.ofPattern("d MMM yyyy", java.util.Locale.forLanguageTag("tr"));

    private static final List<String> UNSPLASH_KAPAK_URLS = List.of(
            "https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=1200&q=80",
            "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=1200&q=80",
            "https://images.unsplash.com/photo-1555949963-ff9fe0c870eb?auto=format&fit=crop&w=1200&q=80",
            "https://images.unsplash.com/photo-1504639725590-34d0984388bd?auto=format&fit=crop&w=1200&q=80",
            "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=1200&q=80",
            "https://images.unsplash.com/photo-1677442136019-21780ecad995?auto=format&fit=crop&w=1200&q=80"
    );

    @Inject
    private BlogFacadeLocal blogFacade;

    @Inject
    private OturumBean oturum;

    private Long id;
    private Blog blog;

    public String hazirla() {
        if (!oturum.isGirisYapildi()) {
            return "/auth/giris?faces-redirect=true";
        }
        if (!oturum.isAdmin()) {
            return "/panel/bloglarim?faces-redirect=true";
        }
        yukle();
        return null;
    }

    public void yukle() {
        if (id == null) {
            blog = null;
            return;
        }
        Blog b = blogFacade.bul(id);
        if (b == null || b.getDurum() != DurumTip.ONAY_BEKLIYOR) {
            blog = null;
            return;
        }
        blog = b;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Blog getBlog() {
        return blog;
    }

    public String kapakUrl() {
        if (blog == null) {
            return UNSPLASH_KAPAK_URLS.get(0);
        }
        if (blog.getKapakGorseliUrl() != null && !blog.getKapakGorseliUrl().isBlank()) {
            return blog.getKapakGorseliUrl().trim();
        }
        if (blog.getId() == null) {
            return UNSPLASH_KAPAK_URLS.get(0);
        }
        return UNSPLASH_KAPAK_URLS.get(Math.floorMod(blog.getId().intValue(), UNSPLASH_KAPAK_URLS.size()));
    }

    public String yildizMetin() {
        if (blog == null || blog.getId() == null) {
            return "—";
        }
        Double v = blogFacade.ortalamaPuan(blog.getId());
        if (v == null) {
            return "—";
        }
        return String.format("%.1f", v);
    }

    public String tahminiOkuma() {
        if (blog != null && blog.getTahminiOkumaSuresi() != null && blog.getTahminiOkumaSuresi() > 0) {
            return blog.getTahminiOkumaSuresi() + " dk";
        }
        if (blog == null || blog.getIcerik() == null || blog.getIcerik().isBlank()) {
            return "1 dk";
        }
        int words = blog.getIcerik().trim().split("\\s+").length;
        int mins = Math.max(1, words / 200);
        return mins + " dk";
    }

    public String yazarAdi() {
        if (blog == null || blog.getYazar() == null) {
            return "—";
        }
        Kullanici y = blog.getYazar();
        String ad = y.getAd() != null ? y.getAd() : "";
        String soy = y.getSoyad() != null ? y.getSoyad() : "";
        String full = (ad + " " + soy).trim();
        if (!full.isEmpty()) {
            return full;
        }
        if (y.getKullaniciAdi() != null && !y.getKullaniciAdi().isBlank()) {
            return y.getKullaniciAdi();
        }
        return "—";
    }

    public String kategoriEtiket() {
        if (blog != null && blog.getKategori() != null && blog.getKategori().getKategoriAdi() != null) {
            return blog.getKategori().getKategoriAdi();
        }
        return "Genel";
    }

    public String yayinTarihiMetni() {
        LocalDateTime t = blog != null ? blog.getOlusturulmaTarihi() : null;
        if (t == null) {
            return "—";
        }
        return t.format(TARIH_FMT);
    }

    public String surumEtiketi() {
        LocalDateTime t = blog != null ? blog.getOlusturulmaTarihi() : null;
        if (t == null) {
            return "—";
        }
        return String.format("v%d.%02d.%02d", t.getYear(), t.getMonthValue(), t.getDayOfMonth());
    }

    public List<String> getIcerikSatirlari() {
        if (blog == null || blog.getIcerik() == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(blog.getIcerik().split("\\r?\\n", -1));
    }
}

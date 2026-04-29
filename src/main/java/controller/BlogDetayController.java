package controller;

import entity.Blog;
import entity.Kullanici;
import facade.BlogFacade;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Named("blogDetayController")
@ViewScoped
public class BlogDetayController implements Serializable {

    private static final List<String> UNSPLASH_KAPAK_URLS = List.of(
            "https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=800&q=80",
            "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=800&q=80",
            "https://images.unsplash.com/photo-1555949963-ff9fe0c870eb?auto=format&fit=crop&w=800&q=80",
            "https://images.unsplash.com/photo-1504639725590-34d0984388bd?auto=format&fit=crop&w=800&q=80",
            "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=800&q=80",
            "https://images.unsplash.com/photo-1677442136019-21780ecad995?auto=format&fit=crop&w=800&q=80"
    );

    @Inject
    private BlogFacade blogFacade;

    private Long id;
    private Blog blog;

    public void yukle() {
        if (id != null) {
            blog = blogFacade.bul(id);
        } else {
            blog = null;
        }
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
        if (blog == null || blog.getId() == null) {
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

    public String yazarHarfi() {
        String n = yazarAdi();
        if (n == null || n.isBlank() || "—".equals(n)) {
            return "?";
        }
        return n.substring(0, 1).toUpperCase();
    }

    public String kategoriEtiket() {
        if (blog != null && blog.getKategori() != null && blog.getKategori().getKategoriAdi() != null) {
            return blog.getKategori().getKategoriAdi();
        }
        return "Genel";
    }

    public String surumEtiketi() {
        LocalDateTime t = blog != null ? blog.getOlusturulmaTarihi() : null;
        if (t == null) {
            return "—";
        }
        return String.format("v%d.%02d.%02d", t.getYear(), t.getMonthValue(), t.getDayOfMonth());
    }
}

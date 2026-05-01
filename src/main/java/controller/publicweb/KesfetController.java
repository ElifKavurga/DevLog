package controller.publicweb;

import entity.Blog;
import facade.BlogFacade;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("kesfetController")
@ViewScoped
public class KesfetController implements Serializable {

    private static final Logger LOG = Logger.getLogger(KesfetController.class.getName());

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

    private List<Blog> yayinlananBloglar = new ArrayList<>();

    public void hazirla() {
        try {
            List<Blog> liste = blogFacade.yayinlananlariListele();
            yayinlananBloglar = liste != null ? new ArrayList<>(liste) : new ArrayList<>();
        } catch (RuntimeException e) {
            LOG.log(Level.WARNING, "Yayınlanan blog listesi yüklenemedi.", e);
            yayinlananBloglar = new ArrayList<>();
        }
    }

    public List<Blog> getYayinlananBloglar() {
        return yayinlananBloglar;
    }

    public String kapakUrl(Blog blog, int index) {
        if (blog != null && blog.getKapakGorseliUrl() != null && !blog.getKapakGorseliUrl().isBlank()) {
            return blog.getKapakGorseliUrl().trim();
        }
        return UNSPLASH_KAPAK_URLS.get(Math.floorMod(index, UNSPLASH_KAPAK_URLS.size()));
    }

    public String yazarAdi(Blog blog) {
        if (blog == null || blog.getYazar() == null) {
            return "—";
        }
        var y = blog.getYazar();
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

    public String yazarHarfi(Blog blog) {
        String n = yazarAdi(blog);
        if (n == null || n.isBlank() || "—".equals(n)) {
            return "?";
        }
        return n.substring(0, 1).toUpperCase();
    }

    public String kategoriEtiket(Blog blog) {
        if (blog != null && blog.getKategori() != null && blog.getKategori().getKategoriAdi() != null) {
            return blog.getKategori().getKategoriAdi();
        }
        return "Genel";
    }

    public String surumEtiketi(Blog blog) {
        LocalDateTime t = blog != null ? blog.getOlusturulmaTarihi() : null;
        if (t == null) {
            return "—";
        }
        return String.format("v%d.%02d.%02d", t.getYear(), t.getMonthValue(), t.getDayOfMonth());
    }

    public String tahminiOkuma(Blog blog) {
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

    public String yildizMetin(Blog blog) {
        if (blog == null || blog.getId() == null) {
            return "—";
        }
        Double v = blogFacade.ortalamaPuan(blog.getId());
        if (v == null) {
            return "—";
        }
        return String.format("%.1f", v);
    }
}

package controller.publicweb;

import controller.OturumBean;
import entity.Blog;
import entity.Degerlendirme;
import entity.Kullanici;
import enums.DurumTip;
import facadeLocal.BlogFacadeLocal;
import facadeLocal.DegerlendirmeFacadeLocal;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Named("blogDetayController")
@ViewScoped
public class BlogDetayController implements Serializable {

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
    private DegerlendirmeFacadeLocal degerlendirmeFacade;

    @Inject
    private OturumBean oturum;

    private Long id;
    private Blog blog;
    private Integer kullaniciPuani;
    /** UI: yorum alanı (kalıcı kayıt yok; gönderimde bilgilendirme). */
    private String yorumMetni;

    public void yukle() {
        yorumMetni = null;
        kullaniciPuani = null;
        if (id == null) {
            blog = null;
            return;
        }
        Blog b = blogFacade.bul(id);
        if (b == null || b.getDurum() != DurumTip.YAYINLANDI) {
            blog = null;
            return;
        }
        blog = b;
        yukleKullaniciPuani();
    }

    private void yukleKullaniciPuani() {
        kullaniciPuani = null;
        if (blog == null || !oturum.isGirisYapildi() || oturum.getAktifKullanici() == null) {
            return;
        }
        Degerlendirme d = degerlendirmeFacade.bulBlogVeKullanici(blog.getId(), oturum.getAktifKullanici().getId());
        if (d != null) {
            kullaniciPuani = d.getPuan();
        }
    }

    public String puanVer(int puan) {
        if (!oturum.isGirisYapildi() || oturum.getAktifKullanici() == null || blog == null) {
            return null;
        }
        if (puan < 1 || puan > 5) {
            return null;
        }
        degerlendirmeFacade.kaydetVeyaGuncellePuan(blog.getId(), oturum.getAktifKullanici().getId(), puan);
        kullaniciPuani = puan;
        return null;
    }

    public boolean yildizDolu(int yildiz) {
        return kullaniciPuani != null && kullaniciPuani >= yildiz;
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

    public Integer getKullaniciPuani() {
        return kullaniciPuani;
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

    /**
     * Blog gövdesini satır satır editör görünümü için döndürür (boş satırlar korunur).
     */
    public List<String> getIcerikSatirlari() {
        if (blog == null || blog.getIcerik() == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(blog.getIcerik().split("\\r?\\n", -1));
    }

    public String getYorumMetni() {
        return yorumMetni;
    }

    public void setYorumMetni(String yorumMetni) {
        this.yorumMetni = yorumMetni;
    }

    public String yorumGonder() {
        if (blog == null) {
            return null;
        }
        String t = yorumMetni != null ? yorumMetni.trim() : "";
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (t.isEmpty()) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Uyarı", "Yorum boş olamaz."));
            return null;
        }
        yorumMetni = "";
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Bilgi",
                "Bu sürümde yorumlar henüz kalıcı olarak kaydedilmiyor; metniniz yalnızca arayüzde işlendi."));
        return null;
    }
}

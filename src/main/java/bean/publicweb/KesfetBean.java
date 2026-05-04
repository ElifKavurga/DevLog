package bean.publicweb;

import entity.Blog;
import entity.Kategori;
import facadeLocal.BlogFacadeLocal;
import facadeLocal.KategoriFacadeLocal;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("kesfetBean")
@ViewScoped
public class KesfetBean implements Serializable {

    private static final Logger LOG = Logger.getLogger(KesfetBean.class.getName());

    @Inject
    private BlogFacadeLocal blogFacade;

    @Inject
    private KategoriFacadeLocal kategoriFacade;

    private List<Long> seciliKategoriIds = new ArrayList<>();
    private String aramaMetni = "";
    private List<Blog> yayinlananBloglar = new ArrayList<>();
    private List<Kategori> kategoriler = new ArrayList<>();

    public void hazirla() {
        istektenSeciliKategorileriOku();
        istektenAramaOku();
        yukleKategoriler();
        yukleBloglar();
    }

    private void istektenSeciliKategorileriOku() {
        seciliKategoriIds = new ArrayList<>();
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null) {
            return;
        }
        Map<String, String[]> map = fc.getExternalContext().getRequestParameterValuesMap();
        if (map == null) {
            return;
        }
        String[] raw = map.get("kategoriId");
        if (raw == null || raw.length == 0) {
            return;
        }
        LinkedHashSet<Long> set = new LinkedHashSet<>();
        for (String s : raw) {
            if (s == null || s.isBlank()) {
                continue;
            }
            try {
                long v = Long.parseLong(s.trim());
                if (v > 0) {
                    set.add(v);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        seciliKategoriIds = new ArrayList<>(set);
    }

    private void istektenAramaOku() {
        aramaMetni = "";
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null) {
            return;
        }
        String q = fc.getExternalContext().getRequestParameterMap().get("q");
        if (q == null) {
            return;
        }
        String t = q.trim();
        if (t.length() > 200) {
            t = t.substring(0, 200);
        }
        aramaMetni = t;
    }

    private void yukleKategoriler() {
        try {
            List<Kategori> list = kategoriFacade.listeleIdArtan();
            kategoriler = list != null ? new ArrayList<>(list) : new ArrayList<>();
        } catch (RuntimeException e) {
            LOG.log(Level.WARNING, "Kategori listesi yüklenemedi.", e);
            kategoriler = new ArrayList<>();
        }
    }

    private void yukleBloglar() {
        try {
            List<Long> kidParams = null;
            if (!seciliKategoriIds.isEmpty()) {
                List<Long> gecerli = new ArrayList<>();
                for (Long id : seciliKategoriIds) {
                    if (id != null && kategoriFacade.bul(id) != null) {
                        gecerli.add(id);
                    }
                }
                if (gecerli.isEmpty()) {
                    yayinlananBloglar = new ArrayList<>();
                    return;
                }
                kidParams = gecerli;
            }
            String qAra = aramaMetni != null && !aramaMetni.isBlank() ? aramaMetni : null;
            List<Blog> liste = blogFacade.yayinlananFiltrele(kidParams, qAra);
            yayinlananBloglar = liste != null ? new ArrayList<>(liste) : new ArrayList<>();
        } catch (RuntimeException e) {
            LOG.log(Level.WARNING, "Blog listesi yüklenemedi.", e);
            yayinlananBloglar = new ArrayList<>();
        }
    }

    public List<Long> getSeciliKategoriIds() {
        return seciliKategoriIds;
    }

    public boolean kategoriSecili(Long kategoriKey) {
        return kategoriKey != null && seciliKategoriIds.contains(kategoriKey);
    }

    public List<Long> kategoriLinkIcinSecim(Long toggleId) {
        LinkedHashSet<Long> s = new LinkedHashSet<>(seciliKategoriIds);
        if (toggleId == null) {
            return new ArrayList<>(s);
        }
        if (s.contains(toggleId)) {
            s.remove(toggleId);
        } else {
            s.add(toggleId);
        }
        return new ArrayList<>(s);
    }

    public String kategoriFiltreUrl(Long toggleKategoriId) {
        FacesContext fc = FacesContext.getCurrentInstance();
        String ctx = "";
        if (fc != null) {
            String c = fc.getExternalContext().getRequestContextPath();
            if (c != null) {
                ctx = c;
            }
        }
        String base = ctx + "/public/index.xhtml";
        List<Long> ids = kategoriLinkIcinSecim(toggleKategoriId);
        if (ids.isEmpty()) {
            if (aramaMetni == null || aramaMetni.isBlank()) {
                return base;
            }
            StringBuilder u = new StringBuilder(base).append('?');
            aramaQueryEkle(u);
            return u.toString();
        }
        StringBuilder u = new StringBuilder(base).append('?');
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                u.append('&');
            }
            u.append("kategoriId=").append(ids.get(i));
        }
        aramaQueryEkle(u);
        return u.toString();
    }

    public String kesfetTemizUrl() {
        FacesContext fc = FacesContext.getCurrentInstance();
        String ctx = "";
        if (fc != null) {
            String c = fc.getExternalContext().getRequestContextPath();
            if (c != null) {
                ctx = c;
            }
        }
        return ctx + "/public/index.xhtml";
    }

    public boolean isFiltreAktif() {
        return seciliKategoriIds != null && !seciliKategoriIds.isEmpty();
    }

    public String getAramaMetni() {
        return aramaMetni != null ? aramaMetni : "";
    }

    public boolean isAramaAktif() {
        return aramaMetni != null && !aramaMetni.isBlank();
    }

    private void aramaQueryEkle(StringBuilder u) {
        if (aramaMetni == null || aramaMetni.isBlank()) {
            return;
        }
        if (u.indexOf("?") < 0) {
            u.append('?');
        } else if (u.charAt(u.length() - 1) != '?') {
            u.append('&');
        }
        u.append("q=").append(urlEncodeQ(aramaMetni.trim()));
    }

    private static String urlEncodeQ(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8).replace("+", "%20");
    }

    public List<Blog> getYayinlananBloglar() {
        return yayinlananBloglar;
    }

    public List<Kategori> getKategoriler() {
        return kategoriler;
    }

    public String getSeciliKategoriOzeti() {
        if (seciliKategoriIds == null || seciliKategoriIds.isEmpty()) {
            return null;
        }
        List<String> adlar = new ArrayList<>();
        for (Long id : seciliKategoriIds) {
            try {
                Kategori k = kategoriFacade.bul(id);
                if (k != null && k.getKategoriAdi() != null) {
                    adlar.add(k.getKategoriAdi());
                }
            } catch (RuntimeException ignored) {
            }
        }
        if (adlar.isEmpty()) {
            return null;
        }
        return String.join(", ", adlar);
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

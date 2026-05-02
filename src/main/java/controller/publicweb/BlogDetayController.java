package controller.publicweb;

import controller.OturumController;
import controller.panel.ProfilController;
import dto.BlogDTO;
import dto.YorumDTO;
import entity.Degerlendirme;
import entity.Kullanici;
import entity.SistemLog;
import enums.DurumTip;
import enums.RolTip;
import facadeLocal.BildirimFacadeLocal;
import facadeLocal.BlogFacadeLocal;
import facadeLocal.DegerlendirmeFacadeLocal;
import facadeLocal.SistemLogFacadeLocal;
import facadeLocal.YorumFacadeLocal;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mapper.BlogMapper;

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
    private static final DateTimeFormatter YORUM_TARIH_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", java.util.Locale.forLanguageTag("tr"));

    @Inject
    private BlogFacadeLocal blogFacade;

    @Inject
    private DegerlendirmeFacadeLocal degerlendirmeFacade;

    @Inject
    private YorumFacadeLocal yorumFacade;

    @Inject
    private SistemLogFacadeLocal sistemLogFacade;

    @Inject
    private BildirimFacadeLocal bildirimFacade;

    @Inject
    private OturumController oturum;

    private Long id;
    private BlogDTO blog;
    private Integer kullaniciPuani;
    private String yorumMetni;

    public void yukle() {
        yorumMetni = null;
        kullaniciPuani = null;
        if (id == null) {
            blog = null;
            return;
        }
        var b = blogFacade.bul(id);
        if (b == null || b.getDurum() != DurumTip.YAYINLANDI) {
            blog = null;
            return;
        }
        blog = BlogMapper.toDetail(blogFacade.bulBlogDetayPublic(id));
        yukleKullaniciPuani();
    }

    private void yukleKullaniciPuani() {
        kullaniciPuani = null;
        if (blog == null || !oturum.isGirisYapildi() || oturum.getAktifKullaniciEntity() == null) {
            return;
        }
        Degerlendirme d = degerlendirmeFacade.bulBlogVeKullanici(blog.getId(), oturum.getAktifKullaniciEntity().getId());
        if (d != null) {
            kullaniciPuani = d.getPuan();
        }
    }

    private void blogDetayiYenile() {
        if (id == null) {
            return;
        }
        Long blogId = id;
        blog = null;
        blog = BlogMapper.toDetail(blogFacade.bulBlogDetayPublic(blogId));
        yukleKullaniciPuani();
    }

    private void yazaraBildirimGonder(String mesajGovdesi) {
        if (blog == null || blog.getYazar() == null || blog.getYazar().getId() == null) {
            return;
        }
        if (blog.getYazar().getRol() != RolTip.YAZAR) {
            return;
        }
        Long yazarId = blog.getYazar().getId();
        if (oturum.isGirisYapildi() && oturum.getAktifKullaniciEntity() != null
                && yazarId.equals(oturum.getAktifKullaniciEntity().getId())) {
            return;
        }
        String govde = mesajGovdesi != null ? mesajGovdesi : "";
        bildirimFacade.aliciyaMesajOlustur(yazarId, "BLOG:" + blog.getId() + ":" + govde);
    }

    private void sistemLogKaydet(String islem) {
        Kullanici k = oturum.isGirisYapildi() ? oturum.getAktifKullaniciEntity() : null;
        SistemLog log = new SistemLog();
        log.setKullaniciBilgisi(ProfilController.kullaniciLogKimligi(k));
        log.setIslem(islem);
        log.setTarih(LocalDateTime.now());
        sistemLogFacade.olustur(log);
    }

    public String puanVer(int puan) {
        if (!oturum.isGirisYapildi() || oturum.getAktifKullaniciEntity() == null || blog == null) {
            return null;
        }
        if (puan < 1 || puan > 5) {
            return null;
        }
        degerlendirmeFacade.kaydetVeyaGuncellePuan(blog.getId(), oturum.getAktifKullaniciEntity().getId(), puan);
        kullaniciPuani = puan;
        sistemLogKaydet("Kullanıcı bir bloga yorum yaptı.");
        yazaraBildirimGonder("Blog yazınıza yeni bir yorum/puan eklendi.");
        blogDetayiYenile();
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

    public BlogDTO getBlog() {
        return blog;
    }

    public Integer getKullaniciPuani() {
        return kullaniciPuani;
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

    public String yorumKullaniciAdi(YorumDTO y) {
        if (y == null || y.getKullanici() == null) {
            return "kullanici";
        }
        String ad = y.getKullanici().getKullaniciAdi();
        if (ad == null || ad.isBlank()) {
            return "kullanici";
        }
        return ad.trim();
    }

    public String yorumTarihMetni(YorumDTO y) {
        if (y == null || y.getOlusturulmaTarihi() == null) {
            return "—";
        }
        return y.getOlusturulmaTarihi().format(YORUM_TARIH_FMT);
    }

    public String yorumEkle() {
        if (blog == null) {
            return null;
        }
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (!oturum.isGirisYapildi() || oturum.getAktifKullaniciEntity() == null) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Uyarı", "Yorum yazmak için giriş yapın."));
            return null;
        }
        String t = yorumMetni != null ? yorumMetni.trim() : "";
        if (t.isEmpty()) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Uyarı", "Yorum boş olamaz."));
            return null;
        }
        Kullanici k = oturum.getAktifKullaniciEntity();
        Long blogId = blog.getId();
        yorumFacade.olustur(blogId, k.getId(), t);
        yorumMetni = "";
        sistemLogKaydet("Kullanıcı bir bloga yorum yaptı.");
        yazaraBildirimGonder("Blog yazınıza yeni bir yorum/puan eklendi.");
        id = blogId;
        this.blog = BlogMapper.toDetail(blogFacade.bulBlogDetayPublic(blog.getId()));
        yukleKullaniciPuani();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Bilgi", "Yorumunuz kaydedildi."));
        return null;
    }

    public String yorumGonder() {
        return yorumEkle();
    }
}

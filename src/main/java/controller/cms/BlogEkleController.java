package controller.cms;

import controller.OturumBean;
import entity.Blog;
import entity.Kategori;
import entity.Kullanici;
import enums.DurumTip;
import facadeLocal.BlogFacadeLocal;
import facadeLocal.KategoriFacadeLocal;
import jakarta.annotation.PostConstruct;
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

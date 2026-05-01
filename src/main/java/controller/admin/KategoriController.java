package controller.admin;

import controller.OturumBean;
import entity.Kategori;
import facadeLocal.KategoriFacadeLocal;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Named("kategoriController")
@ViewScoped
public class KategoriController implements Serializable {

    @Inject
    private KategoriFacadeLocal kategoriFacade;

    @Inject
    private OturumBean oturum;

    private List<Kategori> kategoriler = new ArrayList<>();

    private String yeniIsim;
    private String hataMesaji;

    private Long duzenlemeId;
    private String duzenlemeIsim;

    public String hazirla() {
        if (!oturum.isGirisYapildi()) {
            return "/auth/giris?faces-redirect=true";
        }
        if (!oturum.isAdmin()) {
            return "/panel/bloglarim?faces-redirect=true";
        }
        yukleListe();
        return null;
    }

    private void yukleListe() {
        List<Kategori> list = kategoriFacade.listeleIdArtan();
        kategoriler = list != null ? new ArrayList<>(list) : new ArrayList<>();
    }

    /**
     * İsimden URL uyumlu slug: küçük harf, harf/rakam dışı tire.
     */
    public String slugUret(String isim) {
        if (isim == null || isim.isBlank()) {
            return "kategori";
        }
        String s = isim.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        s = s.replaceAll("^-+", "").replaceAll("-+$", "");
        return s.isEmpty() ? "kategori" : s;
    }

    public String ekle() {
        hataMesaji = null;
        if (!oturum.isAdmin()) {
            return null;
        }
        if (yeniIsim == null || yeniIsim.isBlank()) {
            hataMesaji = "Kategori adı girin.";
            return null;
        }
        Kategori k = new Kategori();
        k.setIsim(yeniIsim.trim());
        k.setSlug(slugUret(yeniIsim.trim()));
        kategoriFacade.olustur(k);
        yeniIsim = null;
        yukleListe();
        return null;
    }

    public String duzenlemeyeBasla(Kategori k) {
        hataMesaji = null;
        if (k == null) {
            duzenlemeId = null;
            duzenlemeIsim = null;
            return null;
        }
        duzenlemeId = k.getId();
        duzenlemeIsim = k.getIsim();
        return null;
    }

    public String kaydetDuzenle() {
        hataMesaji = null;
        if (!oturum.isAdmin() || duzenlemeId == null) {
            iptalDuzenle();
            return null;
        }
        if (duzenlemeIsim == null || duzenlemeIsim.isBlank()) {
            hataMesaji = "Kategori adı boş olamaz.";
            return null;
        }
        Kategori k = kategoriFacade.bul(duzenlemeId);
        if (k == null) {
            iptalDuzenle();
            yukleListe();
            return null;
        }
        k.setIsim(duzenlemeIsim.trim());
        k.setSlug(slugUret(duzenlemeIsim.trim()));
        kategoriFacade.guncelle(k);
        iptalDuzenle();
        yukleListe();
        return null;
    }

    public String iptalDuzenle() {
        duzenlemeId = null;
        duzenlemeIsim = null;
        return null;
    }

    public String sil(Long kategoriId) {
        hataMesaji = null;
        if (!oturum.isAdmin() || kategoriId == null) {
            return null;
        }
        if (kategoriFacade.blogSayisi(kategoriId) > 0) {
            hataMesaji = "Bu kategoriye bağlı blog kayıtları var; önce blogları başka kategoriye taşıyın veya silin.";
            return null;
        }
        Kategori k = kategoriFacade.bul(kategoriId);
        if (k != null) {
            kategoriFacade.sil(k);
        }
        if (duzenlemeId != null && duzenlemeId.equals(kategoriId)) {
            duzenlemeId = null;
            duzenlemeIsim = null;
        }
        yukleListe();
        return null;
    }

    public List<Kategori> getKategoriler() {
        return kategoriler;
    }

    public String getYeniIsim() {
        return yeniIsim;
    }

    public void setYeniIsim(String yeniIsim) {
        this.yeniIsim = yeniIsim;
    }

    public String getHataMesaji() {
        return hataMesaji;
    }

    public Long getDuzenlemeId() {
        return duzenlemeId;
    }

    public String getDuzenlemeIsim() {
        return duzenlemeIsim;
    }

    public void setDuzenlemeIsim(String duzenlemeIsim) {
        this.duzenlemeIsim = duzenlemeIsim;
    }

    public boolean isDuzenlemeModu() {
        return duzenlemeId != null;
    }

    public long blogSayisi(Kategori k) {
        if (k == null || k.getId() == null) {
            return 0L;
        }
        return kategoriFacade.blogSayisi(k.getId());
    }
}

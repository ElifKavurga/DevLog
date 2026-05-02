package controller.publicweb;

import entity.Kategori;
import facadeLocal.KategoriFacadeLocal;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Herkese açık kategori listesi (Keşfet / Kategoriler sayfası).
 */
@Named("publicKategoriController")
@RequestScoped
public class PublicKategoriController implements Serializable {

    @Inject
    private KategoriFacadeLocal kategoriFacade;

    private List<Kategori> kategoriler = new ArrayList<>();

    @PostConstruct
    public void yukle() {
        List<Kategori> list = kategoriFacade.listeleIdArtan();
        kategoriler = list != null ? new ArrayList<>(list) : new ArrayList<>();
    }

    public List<Kategori> getKategoriler() {
        return kategoriler;
    }

    public String slugGoster(Kategori k) {
        if (k == null) {
            return "";
        }
        if (k.getSlug() != null && !k.getSlug().isBlank()) {
            return k.getSlug();
        }
        return "—";
    }

    public long blogSayisi(Long kategoriId) {
        if (kategoriId == null) {
            return 0L;
        }
        return kategoriFacade.blogSayisi(kategoriId);
    }
}

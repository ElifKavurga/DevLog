package controller.publicweb;

import dto.KategoriDTO;
import entity.Kategori;
import facadeLocal.KategoriFacadeLocal;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mapper.KategoriMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("publicKategoriController")
@RequestScoped
public class PublicKategoriController implements Serializable {

    @Inject
    private KategoriFacadeLocal kategoriFacade;

    private List<KategoriDTO> kategoriler = new ArrayList<>();

    @PostConstruct
    public void yukle() {
        List<Kategori> list = kategoriFacade.listeleIdArtan();
        kategoriler = KategoriMapper.toDtoList(list != null ? list : List.of());
    }

    public List<KategoriDTO> getKategoriler() {
        return kategoriler;
    }

    public String slugGoster(KategoriDTO k) {
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

package controller.panel;

import controller.OturumController;
import dto.YorumDTO;
import entity.Yorum;
import facadeLocal.YorumFacadeLocal;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mapper.YorumMapper;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Named("yorumlarimController")
@ViewScoped
public class YorumlarimController implements Serializable {

    private static final DateTimeFormatter TARIH_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.forLanguageTag("tr"));

    @Inject
    private OturumController oturum;

    @Inject
    private YorumFacadeLocal yorumFacade;

    private List<YorumDTO> yorumlar = new ArrayList<>();

    public String hazirla() {
        if (!oturum.isGirisYapildi() || oturum.getAktifKullaniciEntity() == null) {
            return "/auth/giris?faces-redirect=true";
        }
        Long kid = oturum.getAktifKullaniciEntity().getId();
        List<Yorum> list = yorumFacade.kullaniciyaGoreListele(kid);
        yorumlar = YorumMapper.toDtoList(list != null ? list : List.of());
        return null;
    }

    public List<YorumDTO> getYorumlar() {
        return yorumlar;
    }

    public String tarihMetni(YorumDTO y) {
        if (y == null || y.getOlusturulmaTarihi() == null) {
            return "—";
        }
        return y.getOlusturulmaTarihi().format(TARIH_FMT);
    }
}

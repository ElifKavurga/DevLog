package bean.panel;

import bean.OturumBean;
import entity.Yorum;
import facadeLocal.YorumFacadeLocal;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Named("yorumlarimBean")
@ViewScoped
public class YorumlarimBean implements Serializable {

    private static final DateTimeFormatter TARIH_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.forLanguageTag("tr"));

    @Inject
    private OturumBean oturum;

    @Inject
    private YorumFacadeLocal yorumFacade;

    private List<Yorum> yorumlar = new ArrayList<>();

    public String hazirla() {
        if (!oturum.isGirisYapildi() || oturum.getAktifKullaniciEntity() == null) {
            return "/auth/giris?faces-redirect=true";
        }
        Long kid = oturum.getAktifKullaniciEntity().getId();
        List<Yorum> list = yorumFacade.kullaniciyaGoreListele(kid);
        yorumlar = list != null ? new ArrayList<>(list) : new ArrayList<>();
        return null;
    }

    public List<Yorum> getYorumlar() {
        return yorumlar;
    }

    public String tarihMetni(Yorum y) {
        if (y == null || y.getOlusturulmaTarihi() == null) {
            return "—";
        }
        return y.getOlusturulmaTarihi().format(TARIH_FMT);
    }
}

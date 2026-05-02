package controller.panel;

import controller.OturumBean;
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

@Named("yorumlarimController")
@ViewScoped
public class YorumlarimController implements Serializable {

    private static final DateTimeFormatter TARIH_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.forLanguageTag("tr"));

    @Inject
    private OturumBean oturum;

    @Inject
    private YorumFacadeLocal yorumFacade;

    private List<Yorum> yorumlar = new ArrayList<>();

    public String hazirla() {
        if (!oturum.isGirisYapildi() || oturum.getAktifKullanici() == null) {
            return "/auth/giris?faces-redirect=true";
        }
        Long kid = oturum.getAktifKullanici().getId();
        List<Yorum> list = yorumFacade.kullaniciyaGoreListele(kid);
        yorumlar = list != null ? new ArrayList<>(list) : new ArrayList<>();
        return null;
    }

    public List<Yorum> getYorumlar() {
        return yorumlar;
    }

    public String blogBaslik(Yorum y) {
        if (y == null || y.getBlog() == null) {
            return "—";
        }
        String b = y.getBlog().getBaslik();
        return b != null && !b.isBlank() ? b : "Başlıksız";
    }

    public Long blogId(Yorum y) {
        if (y == null || y.getBlog() == null) {
            return null;
        }
        return y.getBlog().getId();
    }

    public String tarihMetni(Yorum y) {
        if (y == null || y.getOlusturulmaTarihi() == null) {
            return "—";
        }
        return y.getOlusturulmaTarihi().format(TARIH_FMT);
    }
}

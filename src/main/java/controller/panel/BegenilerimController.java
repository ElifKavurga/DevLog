package controller.panel;

import controller.OturumBean;
import entity.Blog;
import entity.Degerlendirme;
import enums.DurumTip;
import facadeLocal.DegerlendirmeFacadeLocal;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("begenilerimController")
@ViewScoped
public class BegenilerimController implements Serializable {

    @Inject
    private OturumBean oturum;

    @Inject
    private DegerlendirmeFacadeLocal degerlendirmeFacade;

    private List<Degerlendirme> degerlendirmeler = new ArrayList<>();

    public String hazirla() {
        if (!oturum.isGirisYapildi() || oturum.getAktifKullanici() == null) {
            return "/auth/giris?faces-redirect=true";
        }
        Long kid = oturum.getAktifKullanici().getId();
        List<Degerlendirme> list = degerlendirmeFacade.kullaniciyaGoreListele(kid);
        degerlendirmeler = list != null ? new ArrayList<>(list) : new ArrayList<>();
        return null;
    }

    public List<Degerlendirme> getDegerlendirmeler() {
        return degerlendirmeler;
    }

    public String blogBaslik(Degerlendirme d) {
        if (d == null || d.getBlog() == null) {
            return "—";
        }
        String b = d.getBlog().getBaslik();
        return b != null && !b.isBlank() ? b : "Başlıksız";
    }

    public boolean blogYayinda(Degerlendirme d) {
        if (d == null || d.getBlog() == null) {
            return false;
        }
        return d.getBlog().getDurum() == DurumTip.YAYINLANDI;
    }

    public Long blogId(Degerlendirme d) {
        if (d == null || d.getBlog() == null) {
            return null;
        }
        return d.getBlog().getId();
    }
}

package controller.admin;

import controller.OturumBean;
import entity.Blog;
import enums.DurumTip;
import facadeLocal.BlogFacadeLocal;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Yayında ve reddedilmiş bloglar (veri modelinde yönetici kimliği tutulmadığı için genel işlem geçmişi).
 */
@Named("onayIslemlerimController")
@ViewScoped
public class OnayIslemlerimController implements Serializable {

    @Inject
    private OturumBean oturum;

    @Inject
    private BlogFacadeLocal blogFacade;

    private List<Blog> gecmis = new ArrayList<>();

    public String hazirla() {
        if (!oturum.isGirisYapildi()) {
            return "/auth/giris?faces-redirect=true";
        }
        if (!oturum.isAdmin()) {
            return "/panel/bloglarim?faces-redirect=true";
        }
        List<Blog> yayin = blogFacade.durumaGoreListele(DurumTip.YAYINLANDI);
        List<Blog> red = blogFacade.durumaGoreListele(DurumTip.REDDEDILDI);
        gecmis = new ArrayList<>();
        if (yayin != null) {
            gecmis.addAll(yayin);
        }
        if (red != null) {
            gecmis.addAll(red);
        }
        gecmis.sort(Comparator.comparing(Blog::getId, Comparator.nullsLast(Comparator.reverseOrder())));
        return null;
    }

    public List<Blog> getGecmis() {
        return gecmis;
    }

    public int getGecmisSayisi() {
        return gecmis.size();
    }

    public String durumEtiket(Blog b) {
        if (b == null || b.getDurum() == null) {
            return "—";
        }
        return switch (b.getDurum()) {
            case YAYINLANDI -> "YAYINLANDI";
            case REDDEDILDI -> "REDDEDILDI";
            default -> b.getDurum().name();
        };
    }

    public boolean yayinda(Blog b) {
        return b != null && b.getDurum() == DurumTip.YAYINLANDI;
    }
}

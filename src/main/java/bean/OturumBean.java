package bean;

import entity.Kullanici;
import enums.RolTip;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;

@Named("oturumBean")
@SessionScoped
public class OturumBean implements Serializable {

    private Kullanici aktifKullanici;

    public Kullanici getAktifKullanici() {
        return aktifKullanici;
    }

    public Kullanici getAktifKullaniciEntity() {
        return aktifKullanici;
    }

    public boolean isGirisYapildi() {
        return aktifKullanici != null;
    }

    public boolean isAdmin() {
        return aktifKullanici != null && aktifKullanici.getRol() == RolTip.ADMIN;
    }

    public boolean isYazmayaYetkili() {
        return aktifKullanici != null
                && (aktifKullanici.getRol() == RolTip.YAZAR
                || aktifKullanici.getRol() == RolTip.YONETICI
                || aktifKullanici.getRol() == RolTip.ADMIN);
    }

    /** Arayüzde gösterilecek rol adı (sidebar vb.). */
    public String getRolEtiketi() {
        if (aktifKullanici == null || aktifKullanici.getRol() == null) {
            return "";
        }
        return switch (aktifKullanici.getRol()) {
            case ADMIN -> "Admin";
            case YONETICI -> "Yönetici";
            case YAZAR -> "Yazar";
            case OKUR -> "Okuyucu";
        };
    }

    public void girisYap(Kullanici kullanici) {
        this.aktifKullanici = kullanici;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", aktifKullanici);
    }

    public void aktifKullaniciyiGuncelle(Kullanici kullanici) {
        this.aktifKullanici = kullanici;
    }

    public String menuGitProfil() {
        if (!isGirisYapildi()) {
            uyariGirisGerekli();
            return null;
        }
        return "/panel/profil?faces-redirect=true";
    }

    public String cikisYap() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/public/index?faces-redirect=true";
    }

    private void uyariGirisGerekli() {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "Bunu yapabilmek için lütfen giriş yapın.", ""));
    }

    public String menuGitBloglarim() {
        if (!isGirisYapildi()) {
            uyariGirisGerekli();
            return null;
        }
        if (!isYazmayaYetkili()) {
            uyariYazmaYetkisiYok();
            return "/public/index?faces-redirect=true";
        }
        return "/panel/bloglarim?faces-redirect=true";
    }

    public String menuGitYeniBlog() {
        if (!isGirisYapildi()) {
            uyariGirisGerekli();
            return null;
        }
        if (!isYazmayaYetkili()) {
            uyariYazmaYetkisiYok();
            return "/public/index?faces-redirect=true";
        }
        return "/panel/yeni-blog?faces-redirect=true";
    }

    private void uyariYazmaYetkisiYok() {
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "Blog yazma yetkiniz yok.", "Bu işlem yalnızca yazar ve yöneticiler içindir."));
        fc.getExternalContext().getFlash().setKeepMessages(true);
    }
}

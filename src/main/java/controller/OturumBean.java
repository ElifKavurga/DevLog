package controller;

import entity.Kullanici;
import enums.RolTip;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;

/**
 * Oturum açmış kullanıcı bilgisi (HTTP oturumu ile aynı ömür).
 */
@Named("oturum")
@SessionScoped
public class OturumBean implements Serializable {

    private Kullanici aktifKullanici;

    public Kullanici getAktifKullanici() {
        return aktifKullanici;
    }

    public boolean isGirisYapildi() {
        return aktifKullanici != null;
    }

    /**
     * CMS admin menüsü ve sayfaları için; yalnızca {@link RolTip#ADMIN}.
     */
    public boolean isAdmin() {
        return aktifKullanici != null && aktifKullanici.getRol() == RolTip.ADMIN;
    }

    public void girisYap(Kullanici kullanici) {
        this.aktifKullanici = kullanici;
    }

    public String cikisYap() {
        aktifKullanici = null;
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
        return "/panel/bloglarim?faces-redirect=true";
    }

    public String menuGitYeniBlog() {
        if (!isGirisYapildi()) {
            uyariGirisGerekli();
            return null;
        }
        return "/panel/yeni-blog?faces-redirect=true";
    }
}

package controller;

import dto.KullaniciDTO;
import entity.Kullanici;
import enums.RolTip;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mapper.KullaniciMapper;

import java.io.Serializable;

@Named("oturumController")
@SessionScoped
public class OturumController implements Serializable {

    private Kullanici aktifKullanici;

    public KullaniciDTO getAktifKullanici() {
        return KullaniciMapper.toDto(aktifKullanici);
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

    public void girisYap(Kullanici kullanici) {
        this.aktifKullanici = kullanici;
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

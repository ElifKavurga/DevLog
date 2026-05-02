package controller.panel;

import controller.OturumBean;
import entity.Kullanici;
import entity.SistemLog;
import enums.RolTip;
import facadeLocal.BildirimFacadeLocal;
import facadeLocal.KullaniciFacadeLocal;
import facadeLocal.SistemLogFacadeLocal;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;

@Named("profilController")
@ViewScoped
public class ProfilController implements Serializable {

    @Inject
    private OturumBean oturum;

    @Inject
    private KullaniciFacadeLocal kullaniciFacade;

    @Inject
    private SistemLogFacadeLocal sistemLogFacade;

    @Inject
    private BildirimFacadeLocal bildirimFacade;

    private String mevcutSifre;
    private String yeniSifre;
    private String yeniSifreTekrar;

    /** Profil formu: düzenlenebilir alanlar */
    private String duzenleKullaniciAdi;
    private String duzenleEposta;

    public String hazirla() {
        if (!oturum.isGirisYapildi() || oturum.getAktifKullanici() == null) {
            return "/auth/giris?faces-redirect=true";
        }
        yenileKullaniciOturumdan();
        Kullanici k = oturum.getAktifKullanici();
        duzenleKullaniciAdi = k.getKullaniciAdi();
        duzenleEposta = k.getEposta();
        return null;
    }

    public String profilBilgiGuncelle() {
        if (!oturum.isGirisYapildi() || oturum.getAktifKullanici() == null) {
            return null;
        }
        Kullanici k = kullaniciFacade.bul(oturum.getAktifKullanici().getId());
        if (k == null) {
            mesaj(FacesMessage.SEVERITY_ERROR, "Kullanıcı bulunamadı.");
            return null;
        }
        if (duzenleKullaniciAdi == null || duzenleKullaniciAdi.isBlank()) {
            mesaj(FacesMessage.SEVERITY_WARN, "Kullanıcı adı zorunludur.");
            return null;
        }
        if (duzenleEposta == null || duzenleEposta.isBlank()) {
            mesaj(FacesMessage.SEVERITY_WARN, "E-posta zorunludur.");
            return null;
        }
        String trimUser = duzenleKullaniciAdi.trim();
        String trimMail = duzenleEposta.trim();
        if (!trimMail.contains("@") || trimMail.indexOf('@') == trimMail.length() - 1) {
            mesaj(FacesMessage.SEVERITY_WARN, "Geçerli bir e-posta girin.");
            return null;
        }
        if (kullaniciFacade.kullaniciAdiKullaniliyorMu(trimUser)
                && (k.getKullaniciAdi() == null || !trimUser.equalsIgnoreCase(k.getKullaniciAdi()))) {
            mesaj(FacesMessage.SEVERITY_WARN, "Bu kullanıcı adı kullanılıyor.");
            return null;
        }
        if (kullaniciFacade.epostaKullaniliyorMu(trimMail)
                && (k.getEposta() == null || !trimMail.equalsIgnoreCase(k.getEposta()))) {
            mesaj(FacesMessage.SEVERITY_WARN, "Bu e-posta kullanılıyor.");
            return null;
        }
        k.setKullaniciAdi(trimUser);
        k.setEposta(trimMail);
        Kullanici merged = kullaniciFacade.guncelle(k);
        oturum.aktifKullaniciyiGuncelle(merged);
        mesaj(FacesMessage.SEVERITY_INFO, "Profil bilgileri güncellendi.");
        return null;
    }

    private void yenileKullaniciOturumdan() {
        Long id = oturum.getAktifKullanici().getId();
        if (id != null) {
            Kullanici db = kullaniciFacade.bul(id);
            if (db != null) {
                oturum.aktifKullaniciyiGuncelle(db);
            }
        }
    }

    public String sifreDegistir() {
        if (!oturum.isGirisYapildi()) {
            return null;
        }
        Kullanici k = kullaniciFacade.bul(oturum.getAktifKullanici().getId());
        if (k == null) {
            mesaj(FacesMessage.SEVERITY_ERROR, "Oturum bulunamadı.");
            return null;
        }
        if (mevcutSifre == null || mevcutSifre.isBlank()) {
            mesaj(FacesMessage.SEVERITY_WARN, "Mevcut şifreyi girin.");
            return null;
        }
        if (!mevcutSifre.equals(k.getSifre())) {
            mesaj(FacesMessage.SEVERITY_ERROR, "Mevcut şifre hatalı.");
            return null;
        }
        if (yeniSifre == null || yeniSifre.length() < 4) {
            mesaj(FacesMessage.SEVERITY_WARN, "Yeni şifre en az 4 karakter olmalıdır.");
            return null;
        }
        if (!yeniSifre.equals(yeniSifreTekrar)) {
            mesaj(FacesMessage.SEVERITY_WARN, "Yeni şifreler eşleşmiyor.");
            return null;
        }
        k.setSifre(yeniSifre);
        Kullanici merged = kullaniciFacade.guncelle(k);
        oturum.aktifKullaniciyiGuncelle(merged);
        mevcutSifre = null;
        yeniSifre = null;
        yeniSifreTekrar = null;
        mesaj(FacesMessage.SEVERITY_INFO, "Şifreniz güncellendi.");
        return null;
    }

    public String yazarlikTalebiGonder() {
        if (!oturum.isGirisYapildi()) {
            return null;
        }
        Kullanici k = kullaniciFacade.bul(oturum.getAktifKullanici().getId());
        if (k == null) {
            mesaj(FacesMessage.SEVERITY_ERROR, "Kullanıcı bulunamadı.");
            return null;
        }
        if (k.getRol() != RolTip.OKUR) {
            mesaj(FacesMessage.SEVERITY_WARN, "Yalnızca okur hesapları yazarlık talebinde bulunabilir.");
            return null;
        }
        if (k.isYazarlikTalepEtti()) {
            mesaj(FacesMessage.SEVERITY_INFO, "Zaten bir talebiniz kayıtlı.");
            return null;
        }
        k.setYazarlikTalepEtti(true);
        Kullanici merged = kullaniciFacade.guncelle(k);
        oturum.aktifKullaniciyiGuncelle(merged);
        logYaz(merged, "Kullanıcı yazarlık talebinde bulundu.");
        String ka = merged.getKullaniciAdi() != null && !merged.getKullaniciAdi().isBlank()
                ? merged.getKullaniciAdi().trim() : "Kullanıcı";
        for (Kullanici admin : kullaniciFacade.rolIleListele(RolTip.ADMIN)) {
            if (admin != null && admin.getId() != null) {
                bildirimFacade.aliciyaMesajOlustur(admin.getId(),
                        "Yeni bir yazarlık talebi var: " + ka);
            }
        }
        mesaj(FacesMessage.SEVERITY_INFO, "Yazarlık talebiniz yöneticiye iletildi.");
        return null;
    }

    private void logYaz(Kullanici k, String islem) {
        SistemLog log = new SistemLog();
        log.setKullaniciBilgisi(kullaniciLogKimligi(k));
        log.setIslem(islem);
        log.setTarih(LocalDateTime.now());
        sistemLogFacade.olustur(log);
    }

    public static String kullaniciLogKimligi(Kullanici k) {
        if (k == null) {
            return "—";
        }
        if (k.getEposta() != null && !k.getEposta().isBlank()) {
            return k.getEposta().trim();
        }
        String ad = k.getAd() != null ? k.getAd() : "";
        String soy = k.getSoyad() != null ? k.getSoyad() : "";
        String birlesik = (ad + " " + soy).trim();
        if (!birlesik.isEmpty()) {
            return birlesik;
        }
        if (k.getKullaniciAdi() != null && !k.getKullaniciAdi().isBlank()) {
            return k.getKullaniciAdi();
        }
        return "—";
    }

    private void mesaj(FacesMessage.Severity sev, String detay) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(sev, detay, ""));
    }

    public boolean isYazarlikTalebiGosterme() {
        if (!oturum.isGirisYapildi() || oturum.getAktifKullanici() == null) {
            return false;
        }
        Kullanici k = oturum.getAktifKullanici();
        return k.getRol() == RolTip.OKUR;
    }

    public String getMevcutSifre() {
        return mevcutSifre;
    }

    public void setMevcutSifre(String mevcutSifre) {
        this.mevcutSifre = mevcutSifre;
    }

    public String getYeniSifre() {
        return yeniSifre;
    }

    public void setYeniSifre(String yeniSifre) {
        this.yeniSifre = yeniSifre;
    }

    public String getYeniSifreTekrar() {
        return yeniSifreTekrar;
    }

    public void setYeniSifreTekrar(String yeniSifreTekrar) {
        this.yeniSifreTekrar = yeniSifreTekrar;
    }

    public String getDuzenleKullaniciAdi() {
        return duzenleKullaniciAdi;
    }

    public void setDuzenleKullaniciAdi(String duzenleKullaniciAdi) {
        this.duzenleKullaniciAdi = duzenleKullaniciAdi;
    }

    public String getDuzenleEposta() {
        return duzenleEposta;
    }

    public void setDuzenleEposta(String duzenleEposta) {
        this.duzenleEposta = duzenleEposta;
    }
}

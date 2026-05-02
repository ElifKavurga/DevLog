package controller.panel;

import controller.OturumBean;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Kalıcı yorum entity'si olmadığı için bilgilendirme satırları; ileride gerçek liste bağlanabilir.
 */
@Named("yorumlarimController")
@ViewScoped
public class YorumlarimController implements Serializable {

    @Inject
    private OturumBean oturum;

    private final List<String> bilgiSatirlari = new ArrayList<>();

    public String hazirla() {
        if (!oturum.isGirisYapildi()) {
            return "/auth/giris?faces-redirect=true";
        }
        bilgiSatirlari.clear();
        bilgiSatirlari.add("# Henüz kalıcı yorum kaydı yok (bu sürümde yorumlar blog sayfasında işlenir).");
        bilgiSatirlari.add("# Blog detayında yorum alanından metin gönderebilirsiniz.");
        bilgiSatirlari.add("# Oturum: " + (oturum.getAktifKullanici() != null ? oturum.getAktifKullanici().getKullaniciAdi() : "—"));
        return null;
    }

    public List<String> getBilgiSatirlari() {
        return bilgiSatirlari;
    }
}

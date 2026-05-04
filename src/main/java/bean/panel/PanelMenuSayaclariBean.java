package bean.panel;

import bean.OturumBean;
import facadeLocal.BlogFacadeLocal;
import facadeLocal.KullaniciFacadeLocal;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named("panelMenuSayaclariBean")
@RequestScoped
public class PanelMenuSayaclariBean implements Serializable {

    @Inject
    private BlogFacadeLocal blogFacade;

    @Inject
    private KullaniciFacadeLocal kullaniciFacade;

    @Inject
    private OturumBean oturum;

    public long getOnayBekleyenSayisi() {
        if (!oturum.isGirisYapildi() || !oturum.isAdmin()) {
            return 0L;
        }
        return blogFacade.onayBekleyenSayisi();
    }

    public long getYazarlikTalepSayisi() {
        if (!oturum.isGirisYapildi() || !oturum.isAdmin()) {
            return 0L;
        }
        return kullaniciFacade.yazarlikTalebiBekleyenSayisi();
    }
}

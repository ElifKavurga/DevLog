package controller.panel;

import controller.OturumController;
import facadeLocal.BlogFacadeLocal;
import facadeLocal.KullaniciFacadeLocal;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

/**
 * Yan menüde admin rozetleri için sayımlar (her istekte güncel).
 */
@Named("panelMenuSayaclariController")
@RequestScoped
public class PanelMenuSayaclariController implements Serializable {

    @Inject
    private BlogFacadeLocal blogFacade;

    @Inject
    private KullaniciFacadeLocal kullaniciFacade;

    @Inject
    private OturumController oturum;

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

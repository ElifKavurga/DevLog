package controller.panel;

import controller.OturumBean;
import facadeLocal.BlogFacadeLocal;
import facadeLocal.KullaniciFacadeLocal;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

/**
 * Yan menüde admin rozetleri için sayımlar (her istekte güncel).
 */
@Named("panelMenuSayaclari")
@RequestScoped
public class PanelMenuSayaclari implements Serializable {

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

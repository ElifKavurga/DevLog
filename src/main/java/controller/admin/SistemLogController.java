package controller.admin;

import controller.OturumBean;
import entity.SistemLog;
import facadeLocal.SistemLogFacadeLocal;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Named("sistemLogController")
@ViewScoped
public class SistemLogController implements Serializable {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Inject
    private OturumBean oturum;

    @Inject
    private SistemLogFacadeLocal sistemLogFacade;

    private List<SistemLog> loglar = new ArrayList<>();

    public String hazirla() {
        if (!oturum.isGirisYapildi()) {
            return "/auth/giris?faces-redirect=true";
        }
        if (!oturum.isAdmin()) {
            return "/panel/bloglarim?faces-redirect=true";
        }
        List<SistemLog> list = sistemLogFacade.tariheGoreAzalanListele();
        loglar = list != null ? new ArrayList<>(list) : new ArrayList<>();
        return null;
    }

    public List<SistemLog> getLoglar() {
        return loglar;
    }

    public int getLogSayisi() {
        return loglar.size();
    }

    public String logTarihParcasi(SistemLog log) {
        if (log == null || log.getTarih() == null) {
            return "[—]";
        }
        return "[" + log.getTarih().format(FMT) + "]";
    }

    public String logKimParcasi(SistemLog log) {
        if (log == null) {
            return "[—]";
        }
        String kim = log.getKullaniciBilgisi() != null && !log.getKullaniciBilgisi().isBlank()
                ? log.getKullaniciBilgisi() : "—";
        return "[" + kim + "]";
    }

    public String logIslemParcasi(SistemLog log) {
        if (log == null || log.getIslem() == null) {
            return "";
        }
        return ": " + log.getIslem();
    }
}

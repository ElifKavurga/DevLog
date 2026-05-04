package bean.admin;

import bean.OturumBean;
import entity.SistemLog;
import facadeLocal.SistemLogFacadeLocal;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Named("sistemLogBean")
@RequestScoped
public class SistemLogBean implements Serializable {

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
        veritabanindanYenile();
        return null;
    }

    private void veritabanindanYenile() {
        loglar = new ArrayList<>();
        if (!oturum.isGirisYapildi() || !oturum.isAdmin()) {
            return;
        }
        try {
            sistemLogFacade.ensureSistemLogTableExists();
        } catch (RuntimeException ignored) {
        }
        List<SistemLog> list = sistemLogFacade.tariheGoreAzalanListele();
        loglar = list != null ? new ArrayList<>(list) : new ArrayList<>();
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

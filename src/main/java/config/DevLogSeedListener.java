package config;

import facadeLocal.KategoriFacadeLocal;
import facadeLocal.KullaniciFacadeLocal;
import facadeLocal.SistemLogFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JPA / DDL tamamlandıktan ve web bağlamı ayağa kalktıktan sonra veri tohumlaması yapılır.
 * {@code @Startup} EJB'den önce persistence hazır olmayabildiği için {@link DataInitializer} buradan tetiklenir.
 */
@WebListener
public class DevLogSeedListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(DevLogSeedListener.class.getName());

    @EJB
    private KategoriFacadeLocal kategoriFacade;

    @EJB
    private DataInitializer dataInitializer;

    @EJB
    private KullaniciFacadeLocal kullaniciFacade;

    @EJB
    private SistemLogFacadeLocal sistemLogFacade;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            kategoriFacade.listele();
        } catch (RuntimeException e) {
            LOG.log(Level.WARNING, "Açılış: ilk ORM/şema erişimi atlandı: " + e.getMessage());
        }
        try {
            kategoriFacade.ensureSlugColumnExists();
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Açılış: kategori.slug doğrulaması başarısız (uygulama yine de ayağa kalkar).", e);
        }
        try {
            kullaniciFacade.listele();
        } catch (RuntimeException e) {
            LOG.log(Level.WARNING, "Açılış: kullanici listesi atlandı: " + e.getMessage());
        }
        try {
            kullaniciFacade.ensureYazarlikTalepColumnExists();
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Açılış: kullanici.yazarlik_talep_etti doğrulaması başarısız.", e);
        }
        try {
            sistemLogFacade.ensureSistemLogTableExists();
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Açılış: sistem_log tablosu oluşturulamadı.", e);
        }
        try {
            dataInitializer.seedDefaults();
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Açılış: varsayılan veri tohumlaması başarısız (admin/kategori eksik kalabilir).", e);
        }
    }
}

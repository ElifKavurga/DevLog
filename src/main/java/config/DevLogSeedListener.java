package config;

import facade.KategoriFacade;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * JPA / DDL tamamlandıktan ve web bağlamı ayağa kalktıktan sonra veri tohumlaması yapılır.
 * {@code @Startup} EJB'den önce persistence hazır olmayabildiği için {@link DataInitializer} buradan tetiklenir.
 */
@WebListener
public class DevLogSeedListener implements ServletContextListener {

    @EJB
    private KategoriFacade kategoriFacade;

    @EJB
    private DataInitializer dataInitializer;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        kategoriFacade.ensureSlugColumnExists();
        dataInitializer.seedDefaults();
    }
}

package facade;

import entity.SistemLog;
import facadeLocal.SistemLogFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

/**
 * Sistem olay kayıtları; projede AbstractFacade olmadığı için doğrudan EntityManager kullanılır.
 */
@Stateless
public class SistemLogFacade implements SistemLogFacadeLocal {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @Override
    public void olustur(SistemLog entity) {
        if (entity == null) {
            return;
        }
        em.persist(entity);
        em.flush();
    }

    @Override
    public List<SistemLog> tariheGoreAzalanListele() {
        return em.createQuery(
                        "SELECT s FROM SistemLog s ORDER BY s.tarih DESC",
                        SistemLog.class)
                .setMaxResults(2000)
                .getResultList();
    }

    @Override
    public void ensureSistemLogTableExists() {
        try {
            em.createNativeQuery(
                            "CREATE TABLE IF NOT EXISTS sistem_log ("
                                    + " id BIGSERIAL PRIMARY KEY,"
                                    + " kullanici_bilgisi VARCHAR(500),"
                                    + " islem VARCHAR(2000) NOT NULL,"
                                    + " tarih TIMESTAMP NOT NULL)")
                    .executeUpdate();
        } catch (RuntimeException e) {
            // tablo zaten var veya yetki; log yazma kritik değil
        }
    }
}

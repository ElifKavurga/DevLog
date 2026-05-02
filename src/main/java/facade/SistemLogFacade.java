package facade;

import entity.SistemLog;
import facadeLocal.SistemLogFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SistemLog> cq = cb.createQuery(SistemLog.class);
        Root<SistemLog> root = cq.from(SistemLog.class);
        cq.select(root);
        cq.orderBy(cb.desc(root.get("tarih")));
        return em.createQuery(cq).setMaxResults(2000).getResultList();
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

package facade;

import entity.Kategori;
import facadeLocal.KategoriFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class KategoriFacade implements KategoriFacadeLocal {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public void olustur(Kategori entity) {
        em.persist(entity);
        em.flush();
    }

    public Kategori guncelle(Kategori entity) {
        Kategori merged = em.merge(entity);
        em.flush();
        return merged;
    }

    public void sil(Kategori entity) {
        Kategori merged = em.merge(entity);
        em.remove(merged);
    }

    public List<Kategori> listele() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Kategori> cq = cb.createQuery(Kategori.class);
        Root<Kategori> root = cq.from(Kategori.class);
        CriteriaQuery<Kategori> all = cq.select(root);
        TypedQuery<Kategori> q = em.createQuery(all);
        return q.getResultList();
    }

    public List<Kategori> listeleIdArtan() {
        return em.createQuery("SELECT k FROM Kategori k ORDER BY k.id ASC", Kategori.class).getResultList();
    }

    public long blogSayisi(Long kategoriId) {
        if (kategoriId == null) {
            return 0L;
        }
        Long c = em.createQuery("SELECT COUNT(b) FROM Blog b WHERE b.kategori.id = :kid", Long.class)
                .setParameter("kid", kategoriId)
                .getSingleResult();
        return c == null ? 0L : c;
    }

    /**
     * Eski PostgreSQL şemalarında {@code slug} yoksa ekler; {@code @Startup} singleton'ından önce
     * EclipseLink DDL çalışmayabildiği için uygulama açılışında çağrılır.
     */
    public void ensureSlugColumnExists() {
        em.createNativeQuery("ALTER TABLE kategori ADD COLUMN IF NOT EXISTS slug VARCHAR(200)").executeUpdate();
    }

    public Kategori bul(Long id) {
        if (id == null) {
            return null;
        }
        return em.find(Kategori.class, id);
    }
}

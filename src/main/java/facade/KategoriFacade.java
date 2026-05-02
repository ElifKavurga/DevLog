package facade;

import entity.Blog;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class KategoriFacade implements KategoriFacadeLocal {

    private static final Logger LOG = Logger.getLogger(KategoriFacade.class.getName());

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
        cq.select(root);
        TypedQuery<Kategori> q = em.createQuery(cq);
        return q.getResultList();
    }

    @Override
    public List<Kategori> listeleIdArtan() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Kategori> cq = cb.createQuery(Kategori.class);
        Root<Kategori> root = cq.from(Kategori.class);
        cq.select(root);
        cq.orderBy(cb.asc(root.get("id")));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public long blogSayisi(Long kategoriId) {
        if (kategoriId == null) {
            return 0L;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Blog> root = cq.from(Blog.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("kategori").get("id"), kategoriId));
        Long c = em.createQuery(cq).getSingleResult();
        return c == null ? 0L : c;
    }

    @Override
    public void ensureSlugColumnExists() {
        try {
            Object row = em.createNativeQuery(
                            "SELECT EXISTS (SELECT 1 FROM information_schema.tables "
                                    + "WHERE table_schema = current_schema() AND table_name = 'kategori')")
                    .getSingleResult();
            boolean kategoriTablosuVar = false;
            if (row instanceof Boolean b) {
                kategoriTablosuVar = b;
            } else if (row instanceof Number n) {
                kategoriTablosuVar = n.longValue() != 0;
            } else if (row != null) {
                kategoriTablosuVar = Boolean.parseBoolean(row.toString());
            }
            if (!kategoriTablosuVar) {
                return;
            }
            em.createNativeQuery("ALTER TABLE kategori ADD COLUMN IF NOT EXISTS slug VARCHAR(200)").executeUpdate();
        } catch (RuntimeException e) {
            LOG.log(Level.WARNING, "kategori.slug doğrulaması atlandı: {0}", e.getMessage());
        }
    }

    public Kategori bul(Long id) {
        if (id == null) {
            return null;
        }
        return em.find(Kategori.class, id);
    }
}

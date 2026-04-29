package facade;

import entity.Blog;
import enums.DurumTip;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class BlogFacade {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public void olustur(Blog entity) {
        em.persist(entity);
        em.flush();
    }

    public Blog guncelle(Blog entity) {
        Blog merged = em.merge(entity);
        em.flush();
        return merged;
    }

    public void sil(Blog entity) {
        Blog merged = em.merge(entity);
        em.remove(merged);
    }

    public List<Blog> listele() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Blog> cq = cb.createQuery(Blog.class);
        Root<Blog> root = cq.from(Blog.class);
        CriteriaQuery<Blog> all = cq.select(root);
        TypedQuery<Blog> q = em.createQuery(all);
        return q.getResultList();
    }

    public List<Blog> durumaGoreListele(DurumTip durum) {
        String jpql = "SELECT DISTINCT b FROM Blog b "
                + "LEFT JOIN FETCH b.yazar LEFT JOIN FETCH b.kategori "
                + "WHERE b.durum = :durum";
        return em.createQuery(jpql, Blog.class)
                .setParameter("durum", durum)
                .getResultList();
    }

    public Blog bul(Long id) {
        if (id == null) {
            return null;
        }
        var q = em.createQuery(
                "SELECT DISTINCT b FROM Blog b "
                        + "LEFT JOIN FETCH b.yazar LEFT JOIN FETCH b.kategori "
                        + "WHERE b.id = :id",
                Blog.class);
        q.setParameter("id", id);
        var list = q.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public Double ortalamaPuan(Long blogId) {
        if (blogId == null) {
            return null;
        }
        try {
            var q = em.createQuery(
                    "SELECT AVG(d.puan) FROM Degerlendirme d WHERE d.blog.id = :id",
                    Double.class);
            q.setParameter("id", blogId);
            Double v = q.getSingleResult();
            if (v == null || v.isNaN()) {
                return null;
            }
            return Math.round(v * 10.0) / 10.0;
        } catch (RuntimeException e) {
            return null;
        }
    }
}

package facade;

import entity.Blog;
import entity.Degerlendirme;
import enums.DurumTip;
import facadeLocal.BlogFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class BlogFacade implements BlogFacadeLocal {

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

    /**
     * Keşfet ana sayfa: yalnızca yayında olan bloglar, en yeni önce.
     */
    public List<Blog> yayinlananlariListele() {
        return em.createQuery(
                        "SELECT DISTINCT b FROM Blog b "
                                + "LEFT JOIN FETCH b.yazar LEFT JOIN FETCH b.kategori "
                                + "WHERE b.durum = :durum ORDER BY b.id DESC",
                        Blog.class)
                .setParameter("durum", DurumTip.YAYINLANDI)
                .getResultList();
    }

    @Override
    public List<Blog> kategoriyeGoreListele(Long kategoriId, DurumTip durum) {
        if (kategoriId == null || durum == null) {
            return List.of();
        }
        return em.createQuery(
                        "SELECT DISTINCT b FROM Blog b "
                                + "LEFT JOIN FETCH b.yazar LEFT JOIN FETCH b.kategori "
                                + "WHERE b.kategori.id = :kid AND b.durum = :durum ORDER BY b.id DESC",
                        Blog.class)
                .setParameter("kid", kategoriId)
                .setParameter("durum", durum)
                .getResultList();
    }

    @Override
    public List<Blog> kategorilereGoreListele(List<Long> kategoriIds, DurumTip durum) {
        if (kategoriIds == null || kategoriIds.isEmpty() || durum == null) {
            return List.of();
        }
        return em.createQuery(
                        "SELECT DISTINCT b FROM Blog b "
                                + "LEFT JOIN FETCH b.yazar LEFT JOIN FETCH b.kategori "
                                + "WHERE b.kategori.id IN :kids AND b.durum = :durum ORDER BY b.id DESC",
                        Blog.class)
                .setParameter("kids", kategoriIds)
                .setParameter("durum", durum)
                .getResultList();
    }

    /**
     * Admin onay kuyruğu: yalnızca {@link DurumTip#ONAY_BEKLIYOR}, en yeni önce.
     */
    public List<Blog> onayBekleyenleriListele() {
        return em.createQuery(
                        "SELECT DISTINCT b FROM Blog b "
                                + "LEFT JOIN FETCH b.yazar LEFT JOIN FETCH b.kategori "
                                + "WHERE b.durum = :durum ORDER BY b.id DESC",
                        Blog.class)
                .setParameter("durum", DurumTip.ONAY_BEKLIYOR)
                .getResultList();
    }

    /**
     * Belirli yazarın tüm blog kayıtları (panel listesi); en yeni önce.
     */
    public List<Blog> yazaraGoreListele(Long yazarId) {
        if (yazarId == null) {
            return List.of();
        }
        return em.createQuery(
                        "SELECT b FROM Blog b LEFT JOIN FETCH b.kategori WHERE b.yazar.id = :yid ORDER BY b.id DESC",
                        Blog.class)
                .setParameter("yid", yazarId)
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

    @Override
    public Blog bulBlogDetayPublic(Long id) {
        if (id == null) {
            return null;
        }
        List<Blog> blogs = em.createQuery(
                        "SELECT DISTINCT b FROM Blog b "
                                + "LEFT JOIN FETCH b.yazar LEFT JOIN FETCH b.kategori "
                                + "LEFT JOIN FETCH b.yorumlar y LEFT JOIN FETCH y.kullanici "
                                + "WHERE b.id = :id",
                        Blog.class)
                .setParameter("id", id)
                .getResultList();
        if (blogs.isEmpty()) {
            return null;
        }
        Blog b = blogs.get(0);
        em.createQuery(
                        "SELECT d FROM Degerlendirme d JOIN FETCH d.kullanici WHERE d.blog.id = :bid",
                        Degerlendirme.class)
                .setParameter("bid", id)
                .getResultList();
        return b;
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

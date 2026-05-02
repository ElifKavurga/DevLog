package facade;

import entity.Blog;
import entity.Degerlendirme;
import entity.Yorum;
import enums.DurumTip;
import facadeLocal.BlogFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        cq.select(root);
        return em.createQuery(cq).getResultList();
    }

    public List<Blog> durumaGoreListele(DurumTip durum) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Blog> cq = cb.createQuery(Blog.class);
        Root<Blog> root = cq.from(Blog.class);
        root.fetch("yazar", JoinType.LEFT);
        root.fetch("kategori", JoinType.LEFT);
        cq.select(root).distinct(true);
        cq.where(cb.equal(root.get("durum"), durum));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<Blog> yayinlananlariListele() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Blog> cq = cb.createQuery(Blog.class);
        Root<Blog> root = cq.from(Blog.class);
        root.fetch("yazar", JoinType.LEFT);
        root.fetch("kategori", JoinType.LEFT);
        cq.select(root).distinct(true);
        cq.where(cb.equal(root.get("durum"), DurumTip.YAYINLANDI));
        cq.orderBy(cb.desc(root.get("id")));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<Blog> yayinlananFiltrele(List<Long> kategoriIds, String arama) {
        String likePat = aramaLikeOrNull(arama);
        boolean hasKat = kategoriIds != null && !kategoriIds.isEmpty();
        boolean hasArama = likePat != null;

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Blog> cq = cb.createQuery(Blog.class);
        Root<Blog> root = cq.from(Blog.class);
        root.fetch("yazar", JoinType.LEFT);
        root.fetch("kategori", JoinType.LEFT);
        cq.select(root).distinct(true);

        List<Predicate> preds = new ArrayList<>();
        preds.add(cb.equal(root.get("durum"), DurumTip.YAYINLANDI));
        if (hasKat) {
            preds.add(root.get("kategori").get("id").in(kategoriIds));
        }
        if (hasArama) {
            var baslik = cb.lower(cb.coalesce(root.get("baslik"), cb.literal("")));
            var ozet = cb.lower(cb.coalesce(root.get("ozet"), cb.literal("")));
            var icerik = cb.lower(cb.coalesce(root.get("icerik"), cb.literal("")));
            preds.add(cb.or(
                    cb.like(baslik, likePat),
                    cb.like(ozet, likePat),
                    cb.like(icerik, likePat)));
        }
        cq.where(cb.and(preds.toArray(Predicate[]::new)));
        cq.orderBy(cb.desc(root.get("id")));
        return em.createQuery(cq).getResultList();
    }

    private static String aramaLikeOrNull(String arama) {
        if (arama == null) {
            return null;
        }
        String t = arama.trim();
        if (t.length() > 200) {
            t = t.substring(0, 200);
        }
        t = t.toLowerCase(Locale.ROOT).replace("%", "").replace("_", "");
        if (t.isEmpty()) {
            return null;
        }
        return "%" + t + "%";
    }

    @Override
    public List<Blog> kategoriyeGoreListele(Long kategoriId, DurumTip durum) {
        if (kategoriId == null || durum == null) {
            return List.of();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Blog> cq = cb.createQuery(Blog.class);
        Root<Blog> root = cq.from(Blog.class);
        root.fetch("yazar", JoinType.LEFT);
        root.fetch("kategori", JoinType.LEFT);
        cq.select(root).distinct(true);
        cq.where(
                cb.equal(root.get("kategori").get("id"), kategoriId),
                cb.equal(root.get("durum"), durum));
        cq.orderBy(cb.desc(root.get("id")));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<Blog> kategorilereGoreListele(List<Long> kategoriIds, DurumTip durum) {
        if (kategoriIds == null || kategoriIds.isEmpty() || durum == null) {
            return List.of();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Blog> cq = cb.createQuery(Blog.class);
        Root<Blog> root = cq.from(Blog.class);
        root.fetch("yazar", JoinType.LEFT);
        root.fetch("kategori", JoinType.LEFT);
        cq.select(root).distinct(true);
        cq.where(
                root.get("kategori").get("id").in(kategoriIds),
                cb.equal(root.get("durum"), durum));
        cq.orderBy(cb.desc(root.get("id")));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<Blog> onayBekleyenleriListele() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Blog> cq = cb.createQuery(Blog.class);
        Root<Blog> root = cq.from(Blog.class);
        root.fetch("yazar", JoinType.LEFT);
        root.fetch("kategori", JoinType.LEFT);
        cq.select(root).distinct(true);
        cq.where(cb.equal(root.get("durum"), DurumTip.ONAY_BEKLIYOR));
        cq.orderBy(cb.desc(root.get("id")));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public long onayBekleyenSayisi() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Blog> root = cq.from(Blog.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("durum"), DurumTip.ONAY_BEKLIYOR));
        Long c = em.createQuery(cq).getSingleResult();
        return c != null ? c : 0L;
    }

    @Override
    public List<Blog> yazaraGoreListele(Long yazarId) {
        if (yazarId == null) {
            return List.of();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Blog> cq = cb.createQuery(Blog.class);
        Root<Blog> root = cq.from(Blog.class);
        root.fetch("kategori", JoinType.LEFT);
        cq.select(root);
        cq.where(cb.equal(root.get("yazar").get("id"), yazarId));
        cq.orderBy(cb.desc(root.get("id")));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public Blog bul(Long id) {
        if (id == null) {
            return null;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Blog> cq = cb.createQuery(Blog.class);
        Root<Blog> root = cq.from(Blog.class);
        root.fetch("yazar", JoinType.LEFT);
        root.fetch("kategori", JoinType.LEFT);
        cq.select(root).distinct(true);
        cq.where(cb.equal(root.get("id"), id));
        List<Blog> list = em.createQuery(cq).getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Blog bulBlogDetayPublic(Long id) {
        if (id == null) {
            return null;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Blog> cq = cb.createQuery(Blog.class);
        Root<Blog> root = cq.from(Blog.class);
        root.fetch("yazar", JoinType.LEFT);
        root.fetch("kategori", JoinType.LEFT);
        Fetch<Blog, Yorum> yFetch = root.fetch("yorumlar", JoinType.LEFT);
        yFetch.fetch("kullanici", JoinType.LEFT);
        cq.select(root).distinct(true);
        cq.where(cb.equal(root.get("id"), id));
        TypedQuery<Blog> q = em.createQuery(cq);
        q.setHint(QueryHints.REFRESH, HintValues.TRUE);
        List<Blog> blogs = q.getResultList();
        if (blogs.isEmpty()) {
            return null;
        }
        Blog b = blogs.get(0);
        CriteriaQuery<Degerlendirme> cqD = cb.createQuery(Degerlendirme.class);
        Root<Degerlendirme> dr = cqD.from(Degerlendirme.class);
        dr.fetch("kullanici", JoinType.INNER);
        cqD.select(dr).distinct(true);
        cqD.where(cb.equal(dr.get("blog").get("id"), id));
        em.createQuery(cqD).getResultList();
        return b;
    }

    @Override
    public Double ortalamaPuan(Long blogId) {
        if (blogId == null) {
            return null;
        }
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Double> cq = cb.createQuery(Double.class);
            Root<Degerlendirme> r = cq.from(Degerlendirme.class);
            cq.select(cb.avg(r.get("puan")));
            cq.where(cb.equal(r.get("blog").get("id"), blogId));
            Double v = em.createQuery(cq).getSingleResult();
            if (v == null || v.isNaN()) {
                return null;
            }
            return Math.round(v * 10.0) / 10.0;
        } catch (RuntimeException e) {
            return null;
        }
    }
}

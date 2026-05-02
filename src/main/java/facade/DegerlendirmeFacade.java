package facade;

import entity.Blog;
import entity.Degerlendirme;
import entity.Kullanici;
import facadeLocal.DegerlendirmeFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class DegerlendirmeFacade implements DegerlendirmeFacadeLocal {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public void olustur(Degerlendirme entity) {
        em.persist(entity);
        em.flush();
    }

    public Degerlendirme guncelle(Degerlendirme entity) {
        Degerlendirme merged = em.merge(entity);
        em.flush();
        return merged;
    }

    public void sil(Degerlendirme entity) {
        Degerlendirme merged = em.merge(entity);
        em.remove(merged);
    }

    public List<Degerlendirme> listele() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Degerlendirme> cq = cb.createQuery(Degerlendirme.class);
        Root<Degerlendirme> root = cq.from(Degerlendirme.class);
        cq.select(root);
        return em.createQuery(cq).getResultList();
    }

    @Override
    public Degerlendirme bulBlogVeKullanici(Long blogId, Long kullaniciId) {
        if (blogId == null || kullaniciId == null) {
            return null;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Degerlendirme> cq = cb.createQuery(Degerlendirme.class);
        Root<Degerlendirme> root = cq.from(Degerlendirme.class);
        cq.select(root);
        cq.where(
                cb.equal(root.get("blog").get("id"), blogId),
                cb.equal(root.get("kullanici").get("id"), kullaniciId));
        List<Degerlendirme> sonuc = em.createQuery(cq).getResultList();
        return sonuc.isEmpty() ? null : sonuc.get(0);
    }

    @Override
    public void kaydetVeyaGuncellePuan(Long blogId, Long kullaniciId, int puan) {
        if (blogId == null || kullaniciId == null || puan < 1 || puan > 5) {
            return;
        }
        Degerlendirme mevcut = bulBlogVeKullanici(blogId, kullaniciId);
        if (mevcut == null) {
            Degerlendirme d = new Degerlendirme();
            d.setPuan(puan);
            d.setBlog(em.getReference(Blog.class, blogId));
            d.setKullanici(em.getReference(Kullanici.class, kullaniciId));
            em.persist(d);
            em.flush();
        } else {
            mevcut.setPuan(puan);
            em.merge(mevcut);
            em.flush();
        }
    }

    @Override
    public List<Degerlendirme> kullaniciyaGoreListele(Long kullaniciId) {
        if (kullaniciId == null) {
            return List.of();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Degerlendirme> cq = cb.createQuery(Degerlendirme.class);
        Root<Degerlendirme> root = cq.from(Degerlendirme.class);
        root.fetch("kullanici", JoinType.INNER);
        Fetch<Degerlendirme, Blog> bf = root.fetch("blog", JoinType.INNER);
        bf.fetch("kategori", JoinType.LEFT);
        cq.select(root).distinct(true);
        cq.where(cb.equal(root.get("kullanici").get("id"), kullaniciId));
        cq.orderBy(cb.desc(root.get("id")));
        return em.createQuery(cq).getResultList();
    }
}

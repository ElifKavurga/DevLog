package facade;

import entity.Blog;
import entity.Degerlendirme;
import entity.Kullanici;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class DegerlendirmeFacade {

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
        CriteriaQuery<Degerlendirme> all = cq.select(root);
        TypedQuery<Degerlendirme> q = em.createQuery(all);
        return q.getResultList();
    }

    public Degerlendirme bulBlogVeKullanici(Long blogId, Long kullaniciId) {
        if (blogId == null || kullaniciId == null) {
            return null;
        }
        List<Degerlendirme> sonuc = em.createQuery(
                        "SELECT d FROM Degerlendirme d WHERE d.blog.id = :bid AND d.kullanici.id = :kid",
                        Degerlendirme.class)
                .setParameter("bid", blogId)
                .setParameter("kid", kullaniciId)
                .getResultList();
        return sonuc.isEmpty() ? null : sonuc.get(0);
    }

    /**
     * Aynı kullanıcı aynı blog için ikinci kez puan verirse güncellenir.
     */
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
}

package facade;

import entity.Kategori;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class KategoriFacade {

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
}

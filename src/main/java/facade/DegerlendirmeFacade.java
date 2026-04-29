package facade;

import entity.Degerlendirme;
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
}

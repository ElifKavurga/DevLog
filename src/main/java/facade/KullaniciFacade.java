package facade;

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
public class KullaniciFacade {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public void olustur(Kullanici entity) {
        em.persist(entity);
        em.flush();
    }

    public Kullanici guncelle(Kullanici entity) {
        Kullanici merged = em.merge(entity);
        em.flush();
        return merged;
    }

    public void sil(Kullanici entity) {
        Kullanici merged = em.merge(entity);
        em.remove(merged);
    }

    public List<Kullanici> listele() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Kullanici> cq = cb.createQuery(Kullanici.class);
        Root<Kullanici> root = cq.from(Kullanici.class);
        CriteriaQuery<Kullanici> all = cq.select(root);
        TypedQuery<Kullanici> q = em.createQuery(all);
        return q.getResultList();
    }

    public Kullanici girisYap(String eposta, String sifre) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Kullanici> cq = cb.createQuery(Kullanici.class);
        Root<Kullanici> root = cq.from(Kullanici.class);

        cq.where(cb.equal(root.get("eposta"), eposta), cb.equal(root.get("sifre"), sifre));
        CriteriaQuery<Kullanici> all = cq.select(root);
        TypedQuery<Kullanici> q = em.createQuery(all);

        List<Kullanici> bulunan = q.getResultList();
        if (bulunan.isEmpty()) {
            return null;
        }
        return bulunan.getFirst();
    }
}

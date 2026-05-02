package facade;

import entity.Bildirim;
import entity.Kullanici;
import facadeLocal.BildirimFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class BildirimFacade implements BildirimFacadeLocal {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @Override
    public void olustur(Bildirim entity) {
        if (entity == null) {
            return;
        }
        em.persist(entity);
        em.flush();
    }

    @Override
    public long kullaniciyaGoreOkunmamisBildirimSayisi(Long kullaniciId) {
        if (kullaniciId == null) {
            return 0L;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Bildirim> root = cq.from(Bildirim.class);
        cq.select(cb.count(root));
        cq.where(
                cb.equal(root.get("alici").get("id"), kullaniciId),
                cb.isFalse(root.get("okunduMu")));
        Long c = em.createQuery(cq).getSingleResult();
        return c != null ? c : 0L;
    }

    @Override
    public List<Bildirim> kullaniciyaGoreBildirimleriGetir(Long kullaniciId) {
        if (kullaniciId == null) {
            return List.of();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Bildirim> cq = cb.createQuery(Bildirim.class);
        Root<Bildirim> root = cq.from(Bildirim.class);
        cq.select(root);
        cq.where(cb.equal(root.get("alici").get("id"), kullaniciId));
        cq.orderBy(cb.desc(root.get("tarih")));
        return em.createQuery(cq).setMaxResults(500).getResultList();
    }

    @Override
    public void aliciyaMesajOlustur(Long aliciId, String mesaj) {
        if (aliciId == null || mesaj == null || mesaj.isBlank()) {
            return;
        }
        Bildirim n = new Bildirim();
        n.setAlici(em.getReference(Kullanici.class, aliciId));
        n.setMesaj(mesaj.trim());
        n.setOkunduMu(false);
        n.setTarih(LocalDateTime.now());
        em.persist(n);
        em.flush();
    }

    @Override
    public void tumunuOkunduYap(Long kullaniciId) {
        if (kullaniciId == null) {
            return;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<Bildirim> cu = cb.createCriteriaUpdate(Bildirim.class);
        Root<Bildirim> root = cu.from(Bildirim.class);
        cu.set("okunduMu", true);
        cu.where(
                cb.equal(root.get("alici").get("id"), kullaniciId),
                cb.isFalse(root.get("okunduMu")));
        em.createQuery(cu).executeUpdate();
        em.flush();
    }
}

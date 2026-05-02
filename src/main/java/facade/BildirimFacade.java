package facade;

import entity.Bildirim;
import entity.Kullanici;
import facadeLocal.BildirimFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
        Long c = em.createQuery(
                        "SELECT COUNT(b) FROM Bildirim b WHERE b.alici.id = :kid AND b.okunduMu = false",
                        Long.class)
                .setParameter("kid", kullaniciId)
                .getSingleResult();
        return c != null ? c : 0L;
    }

    @Override
    public List<Bildirim> kullaniciyaGoreBildirimleriGetir(Long kullaniciId) {
        if (kullaniciId == null) {
            return List.of();
        }
        return em.createQuery(
                        "SELECT b FROM Bildirim b WHERE b.alici.id = :kid ORDER BY b.tarih DESC",
                        Bildirim.class)
                .setParameter("kid", kullaniciId)
                .setMaxResults(500)
                .getResultList();
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
        em.createQuery(
                        "UPDATE Bildirim b SET b.okunduMu = true WHERE b.alici.id = :kid AND b.okunduMu = false")
                .setParameter("kid", kullaniciId)
                .executeUpdate();
        em.flush();
    }
}

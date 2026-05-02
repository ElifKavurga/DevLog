package facade;

import entity.Kullanici;
import enums.RolTip;
import facadeLocal.KullaniciFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class KullaniciFacade implements KullaniciFacadeLocal {

    private static final Logger LOG = Logger.getLogger(KullaniciFacade.class.getName());

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
        cq.select(root);
        return em.createQuery(cq).getResultList();
    }

    @Override
    public Kullanici girisYap(String eposta, String sifre) {
        return girisYapEpostaVeyaKullaniciAdi(eposta, sifre);
    }

    @Override
    public Kullanici girisYapEpostaVeyaKullaniciAdi(String epostaVeyaKullaniciAdi, String sifre) {
        if (epostaVeyaKullaniciAdi == null || epostaVeyaKullaniciAdi.isBlank() || sifre == null) {
            return null;
        }
        String login = epostaVeyaKullaniciAdi.trim();
        String loginLower = login.toLowerCase(Locale.ROOT);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Kullanici> cq = cb.createQuery(Kullanici.class);
        Root<Kullanici> root = cq.from(Kullanici.class);
        cq.select(root);
        Predicate epostaEsit = cb.equal(cb.lower(root.get("eposta")), loginLower);
        Predicate kullaniciAdiEsit = cb.equal(cb.lower(root.get("kullaniciAdi")), loginLower);
        cq.where(cb.and(cb.or(epostaEsit, kullaniciAdiEsit), cb.equal(root.get("sifre"), sifre)));
        TypedQuery<Kullanici> q = em.createQuery(cq);
        List<Kullanici> bulunan = q.getResultList();
        if (bulunan.isEmpty()) {
            return null;
        }
        return bulunan.getFirst();
    }

    @Override
    public boolean epostaKullaniliyorMu(String eposta) {
        if (eposta == null || eposta.isBlank()) {
            return false;
        }
        String e = eposta.trim().toLowerCase(Locale.ROOT);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Kullanici> root = cq.from(Kullanici.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(cb.lower(root.get("eposta")), e));
        Long c = em.createQuery(cq).getSingleResult();
        return c != null && c > 0;
    }

    @Override
    public boolean kullaniciAdiKullaniliyorMu(String kullaniciAdi) {
        if (kullaniciAdi == null || kullaniciAdi.isBlank()) {
            return false;
        }
        String u = kullaniciAdi.trim().toLowerCase(Locale.ROOT);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Kullanici> root = cq.from(Kullanici.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(cb.lower(root.get("kullaniciAdi")), u));
        Long c = em.createQuery(cq).getSingleResult();
        return c != null && c > 0;
    }

    @Override
    public Kullanici bul(Long id) {
        if (id == null) {
            return null;
        }
        return em.find(Kullanici.class, id);
    }

    @Override
    public List<Kullanici> yazarlikTalebiBekleyenleriListele() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Kullanici> cq = cb.createQuery(Kullanici.class);
        Root<Kullanici> root = cq.from(Kullanici.class);
        cq.select(root);
        cq.where(cb.isTrue(root.get("yazarlikTalepEtti")));
        cq.orderBy(cb.asc(root.get("id")));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public long yazarlikTalebiBekleyenSayisi() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Kullanici> root = cq.from(Kullanici.class);
        cq.select(cb.count(root));
        cq.where(cb.isTrue(root.get("yazarlikTalepEtti")));
        Long c = em.createQuery(cq).getSingleResult();
        return c != null ? c : 0L;
    }

    @Override
    public List<Kullanici> rolIleListele(RolTip rol) {
        if (rol == null) {
            return List.of();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Kullanici> cq = cb.createQuery(Kullanici.class);
        Root<Kullanici> root = cq.from(Kullanici.class);
        cq.select(root);
        cq.where(cb.equal(root.get("rol"), rol));
        cq.orderBy(cb.asc(root.get("id")));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public void ensureYazarlikTalepColumnExists() {
        try {
            @SuppressWarnings("unchecked")
            List<Object> rows = em.createNativeQuery(
                            "SELECT table_name FROM information_schema.tables "
                                    + "WHERE table_schema = current_schema() AND lower(table_name) = 'kullanici'")
                    .getResultList();
            if (rows.isEmpty()) {
                return;
            }
            String tableName = String.valueOf(rows.get(0));
            String safe = tableName.replace("\"", "");
            String quoted = "\"" + safe + "\"";
            em.createNativeQuery(
                            "ALTER TABLE " + quoted
                                    + " ADD COLUMN IF NOT EXISTS yazarlik_talep_etti BOOLEAN NOT NULL DEFAULT FALSE")
                    .executeUpdate();
        } catch (RuntimeException e) {
            LOG.log(Level.WARNING, "kullanici.yazarlik_talep_etti doğrulaması atlandı: {0}", e.getMessage());
        }
    }
}

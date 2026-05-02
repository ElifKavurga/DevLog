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
import jakarta.persistence.criteria.Root;

import java.util.List;
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
        CriteriaQuery<Kullanici> all = cq.select(root);
        TypedQuery<Kullanici> q = em.createQuery(all);
        return q.getResultList();
    }

    /**
     * E-posta ile giriş (geriye dönük).
     */
    public Kullanici girisYap(String eposta, String sifre) {
        return girisYapEpostaVeyaKullaniciAdi(eposta, sifre);
    }

    /**
     * E-posta veya kullanıcı adı + şifre ile tek sorgu.
     */
    public Kullanici girisYapEpostaVeyaKullaniciAdi(String epostaVeyaKullaniciAdi, String sifre) {
        if (epostaVeyaKullaniciAdi == null || epostaVeyaKullaniciAdi.isBlank() || sifre == null) {
            return null;
        }
        String login = epostaVeyaKullaniciAdi.trim();
        String jpql = "SELECT k FROM Kullanici k WHERE (LOWER(k.eposta) = LOWER(:login) OR LOWER(k.kullaniciAdi) = LOWER(:login)) AND k.sifre = :sifre";
        List<Kullanici> bulunan = em.createQuery(jpql, Kullanici.class)
                .setParameter("login", login)
                .setParameter("sifre", sifre)
                .getResultList();
        if (bulunan.isEmpty()) {
            return null;
        }
        return bulunan.getFirst();
    }

    public boolean epostaKullaniliyorMu(String eposta) {
        if (eposta == null || eposta.isBlank()) {
            return false;
        }
        Number c = em.createQuery("SELECT COUNT(k) FROM Kullanici k WHERE LOWER(k.eposta) = LOWER(:e)", Number.class)
                .setParameter("e", eposta.trim())
                .getSingleResult();
        return c != null && c.longValue() > 0;
    }

    public boolean kullaniciAdiKullaniliyorMu(String kullaniciAdi) {
        if (kullaniciAdi == null || kullaniciAdi.isBlank()) {
            return false;
        }
        Number c = em.createQuery("SELECT COUNT(k) FROM Kullanici k WHERE LOWER(k.kullaniciAdi) = LOWER(:u)", Number.class)
                .setParameter("u", kullaniciAdi.trim())
                .getSingleResult();
        return c != null && c.longValue() > 0;
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
        return em.createQuery(
                        "SELECT k FROM Kullanici k WHERE k.yazarlikTalepEtti = true ORDER BY k.id ASC",
                        Kullanici.class)
                .getResultList();
    }

    @Override
    public long yazarlikTalebiBekleyenSayisi() {
        Long c = em.createQuery(
                        "SELECT COUNT(k) FROM Kullanici k WHERE k.yazarlikTalepEtti = true",
                        Long.class)
                .getSingleResult();
        return c != null ? c : 0L;
    }

    @Override
    public List<Kullanici> rolIleListele(RolTip rol) {
        if (rol == null) {
            return List.of();
        }
        return em.createQuery(
                        "SELECT k FROM Kullanici k WHERE k.rol = :r ORDER BY k.id ASC",
                        Kullanici.class)
                .setParameter("r", rol)
                .getResultList();
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

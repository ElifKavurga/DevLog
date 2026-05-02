package facade;

import entity.Blog;
import entity.Kullanici;
import entity.Yorum;
import facadeLocal.YorumFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class YorumFacade implements YorumFacadeLocal {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    @Override
    public void olustur(Long blogId, Long kullaniciId, String metin) {
        if (blogId == null || kullaniciId == null || metin == null || metin.isBlank()) {
            return;
        }
        Yorum y = new Yorum();
        y.setMetin(metin.trim());
        y.setBlog(em.getReference(Blog.class, blogId));
        y.setKullanici(em.getReference(Kullanici.class, kullaniciId));
        em.persist(y);
        em.flush();
    }

    @Override
    public List<Yorum> kullaniciyaGoreListele(Long kullaniciId) {
        if (kullaniciId == null) {
            return List.of();
        }
        return em.createQuery(
                        "SELECT y FROM Yorum y JOIN FETCH y.blog b WHERE y.kullanici.id = :kid ORDER BY y.olusturulmaTarihi DESC",
                        Yorum.class)
                .setParameter("kid", kullaniciId)
                .getResultList();
    }
}

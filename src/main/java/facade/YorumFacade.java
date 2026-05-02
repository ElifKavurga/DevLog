package facade;

import entity.Blog;
import entity.Kullanici;
import entity.Yorum;
import facadeLocal.YorumFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Yorum> cq = cb.createQuery(Yorum.class);
        Root<Yorum> root = cq.from(Yorum.class);
        root.fetch("blog", JoinType.INNER);
        cq.select(root);
        cq.where(cb.equal(root.get("kullanici").get("id"), kullaniciId));
        cq.orderBy(cb.desc(root.get("olusturulmaTarihi")));
        return em.createQuery(cq).getResultList();
    }
}

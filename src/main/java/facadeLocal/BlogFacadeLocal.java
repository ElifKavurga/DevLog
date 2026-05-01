package facadeLocal;

import entity.Blog;
import enums.DurumTip;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface BlogFacadeLocal {

    void olustur(Blog entity);

    Blog guncelle(Blog entity);

    void sil(Blog entity);

    List<Blog> listele();

    List<Blog> durumaGoreListele(DurumTip durum);

    List<Blog> yayinlananlariListele();

    List<Blog> onayBekleyenleriListele();

    List<Blog> yazaraGoreListele(Long yazarId);

    Blog bul(Long id);

    Double ortalamaPuan(Long blogId);
}

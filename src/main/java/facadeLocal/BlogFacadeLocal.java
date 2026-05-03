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

    List<Blog> yayinlananFiltrele(List<Long> kategoriIds, String arama);

    List<Blog> kategoriyeGoreListele(Long kategoriId, DurumTip durum);

    List<Blog> kategorilereGoreListele(List<Long> kategoriIds, DurumTip durum);

    List<Blog> onayBekleyenleriListele();

    long onayBekleyenSayisi();

    List<Blog> yazaraGoreListele(Long yazarId);

    Blog bul(Long id);

    Blog bulBlogDetayPublic(Long id);

    Double ortalamaPuan(Long blogId);
}

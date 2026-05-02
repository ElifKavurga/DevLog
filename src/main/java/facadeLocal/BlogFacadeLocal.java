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

    /**
     * Belirli kategori ve duruma göre bloglar; yazar ve kategori FETCH ile gelir, en yeni önce.
     */
    List<Blog> kategoriyeGoreListele(Long kategoriId, DurumTip durum);

    /**
     * Birden fazla kategoriden herhangi birine ait (OR) ve verilen durumdaki bloglar; FETCH ile yazar ve kategori.
     */
    List<Blog> kategorilereGoreListele(List<Long> kategoriIds, DurumTip durum);

    List<Blog> onayBekleyenleriListele();

    List<Blog> yazaraGoreListele(Long yazarId);

    Blog bul(Long id);

    /**
     * Blog detay (yorumlar ve yorum yazarları FETCH); değerlendirme satırları ayrı sorgu ile önbelleğe alınır.
     */
    Blog bulBlogDetayPublic(Long id);

    Double ortalamaPuan(Long blogId);
}

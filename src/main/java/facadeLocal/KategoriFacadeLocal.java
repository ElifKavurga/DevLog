package facadeLocal;

import entity.Kategori;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface KategoriFacadeLocal {

    void olustur(Kategori entity);

    Kategori guncelle(Kategori entity);

    void sil(Kategori entity);

    List<Kategori> listele();

    List<Kategori> listeleIdArtan();

    long blogSayisi(Long kategoriId);

    void ensureSlugColumnExists();

    Kategori bul(Long id);
}

package facadeLocal;

import entity.Degerlendirme;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface DegerlendirmeFacadeLocal {

    void olustur(Degerlendirme entity);

    Degerlendirme guncelle(Degerlendirme entity);

    void sil(Degerlendirme entity);

    List<Degerlendirme> listele();

    Degerlendirme bulBlogVeKullanici(Long blogId, Long kullaniciId);

    void kaydetVeyaGuncellePuan(Long blogId, Long kullaniciId, int puan);
}

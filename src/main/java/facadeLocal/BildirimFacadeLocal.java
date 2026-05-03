package facadeLocal;

import entity.Bildirim;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface BildirimFacadeLocal {

    void olustur(Bildirim entity);

    long kullaniciyaGoreOkunmamisBildirimSayisi(Long kullaniciId);

    List<Bildirim> kullaniciyaGoreBildirimleriGetir(Long kullaniciId);

    void aliciyaMesajOlustur(Long aliciId, String mesaj);

    void tumunuOkunduYap(Long kullaniciId);
}

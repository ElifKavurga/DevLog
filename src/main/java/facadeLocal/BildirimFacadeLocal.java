package facadeLocal;

import entity.Bildirim;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface BildirimFacadeLocal {

    void olustur(Bildirim entity);

    /** Okunmamış bildirim sayısı. */
    long kullaniciyaGoreOkunmamisBildirimSayisi(Long kullaniciId);

    List<Bildirim> kullaniciyaGoreBildirimleriGetir(Long kullaniciId);

    /** Kısa yol: alıcıya mesaj kaydı (alıcı yönetilen referans). */
    void aliciyaMesajOlustur(Long aliciId, String mesaj);

    /** Belirtilen kullanıcının tüm okunmamış bildirimlerini okundu yapar. */
    void tumunuOkunduYap(Long kullaniciId);
}

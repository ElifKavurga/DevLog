package facadeLocal;

import entity.Yorum;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface YorumFacadeLocal {

    /** {@code blogId} ve {@code kullaniciId} için yönetilen referanslarla kalıcı yorum oluşturur. */
    void olustur(Long blogId, Long kullaniciId, String metin);

    List<Yorum> kullaniciyaGoreListele(Long kullaniciId);
}

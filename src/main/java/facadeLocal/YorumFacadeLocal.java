package facadeLocal;

import entity.Yorum;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface YorumFacadeLocal {

    void olustur(Long blogId, Long kullaniciId, String metin);

    List<Yorum> kullaniciyaGoreListele(Long kullaniciId);
}

package facadeLocal;

import entity.SistemLog;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface SistemLogFacadeLocal {

    void olustur(SistemLog entity);

    List<SistemLog> tariheGoreAzalanListele();

    /** Tablo yoksa oluşturur (log sayfası / persist hatalarını önler). */
    void ensureSistemLogTableExists();
}

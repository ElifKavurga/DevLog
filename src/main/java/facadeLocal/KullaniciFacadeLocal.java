package facadeLocal;

import entity.Kullanici;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface KullaniciFacadeLocal {

    void olustur(Kullanici entity);

    Kullanici guncelle(Kullanici entity);

    void sil(Kullanici entity);

    List<Kullanici> listele();

    Kullanici girisYap(String eposta, String sifre);

    Kullanici girisYapEpostaVeyaKullaniciAdi(String epostaVeyaKullaniciAdi, String sifre);

    boolean epostaKullaniliyorMu(String eposta);

    boolean kullaniciAdiKullaniliyorMu(String kullaniciAdi);
}

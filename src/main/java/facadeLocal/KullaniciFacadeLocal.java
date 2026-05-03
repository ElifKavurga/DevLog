package facadeLocal;

import entity.Kullanici;
import enums.RolTip;
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

    boolean sifreDogrula(Kullanici kullanici, String duzMetinSifre);

    boolean epostaKullaniliyorMu(String eposta);

    boolean kullaniciAdiKullaniliyorMu(String kullaniciAdi);

    Kullanici bul(Long id);

    List<Kullanici> yazarlikTalebiBekleyenleriListele();

    long yazarlikTalebiBekleyenSayisi();

    List<Kullanici> rolIleListele(RolTip rol);

    void ensureYazarlikTalepColumnExists();
}

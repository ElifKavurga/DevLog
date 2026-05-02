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

    boolean epostaKullaniliyorMu(String eposta);

    boolean kullaniciAdiKullaniliyorMu(String kullaniciAdi);

    Kullanici bul(Long id);

    /**
     * Yazarlık talebi işaretli kullanıcılar (admin kuyruğu).
     */
    List<Kullanici> yazarlikTalebiBekleyenleriListele();

    List<Kullanici> rolIleListele(RolTip rol);

    /**
     * Eski şemalarda {@code yazarlik_talep_etti} yoksa ekler; giriş JPQL'i tüm alanları çektiği için sütun şarttır.
     */
    void ensureYazarlikTalepColumnExists();
}

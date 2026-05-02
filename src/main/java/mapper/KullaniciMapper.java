package mapper;

import dto.KullaniciDTO;
import entity.Kullanici;

public final class KullaniciMapper {

    private KullaniciMapper() {
    }

    public static KullaniciDTO toDto(Kullanici k) {
        if (k == null) {
            return null;
        }
        KullaniciDTO d = new KullaniciDTO();
        d.setId(k.getId());
        d.setAd(k.getAd());
        d.setSoyad(k.getSoyad());
        d.setKullaniciAdi(k.getKullaniciAdi());
        d.setEposta(k.getEposta());
        d.setRol(k.getRol());
        d.setYazarlikTalepEtti(k.isYazarlikTalepEtti());
        return d;
    }
}

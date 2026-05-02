package mapper;

import dto.YorumDTO;
import entity.Yorum;

import java.util.ArrayList;
import java.util.List;

public final class YorumMapper {

    private YorumMapper() {
    }

    public static YorumDTO toDto(Yorum y) {
        if (y == null) {
            return null;
        }
        YorumDTO d = new YorumDTO();
        d.setId(y.getId());
        d.setMetin(y.getMetin());
        d.setOlusturulmaTarihi(y.getOlusturulmaTarihi());
        d.setKullanici(KullaniciMapper.toDto(y.getKullanici()));
        if (y.getBlog() != null) {
            d.setBlogId(y.getBlog().getId());
            String baslik = y.getBlog().getBaslik();
            d.setBlogBaslik(baslik != null && !baslik.isBlank() ? baslik : "Başlıksız");
        }
        return d;
    }

    public static List<YorumDTO> toDtoList(List<Yorum> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<YorumDTO> out = new ArrayList<>(list.size());
        for (Yorum y : list) {
            out.add(toDto(y));
        }
        return out;
    }
}

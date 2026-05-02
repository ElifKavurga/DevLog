package mapper;

import dto.KategoriDTO;
import entity.Kategori;

import java.util.ArrayList;
import java.util.List;

public final class KategoriMapper {

    private KategoriMapper() {
    }

    public static KategoriDTO toDto(Kategori k) {
        if (k == null) {
            return null;
        }
        KategoriDTO d = new KategoriDTO();
        d.setId(k.getId());
        d.setIsim(k.getIsim());
        d.setSlug(k.getSlug());
        return d;
    }

    public static List<KategoriDTO> toDtoList(List<Kategori> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<KategoriDTO> out = new ArrayList<>(list.size());
        for (Kategori k : list) {
            out.add(toDto(k));
        }
        return out;
    }
}

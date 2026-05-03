package mapper;

import dto.BlogDTO;
import dto.YorumDTO;
import entity.Blog;
import entity.Kategori;
import entity.Kullanici;
import entity.Yorum;
import enums.DurumTip;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BlogMapper {

    private BlogMapper() {
    }

    public static BlogDTO toListItem(Blog b) {
        if (b == null) {
            return null;
        }
        BlogDTO d = baseFields(b);
        d.setYorumlar(Collections.emptyList());
        return d;
    }

    public static BlogDTO toDetail(Blog b) {
        if (b == null) {
            return null;
        }
        BlogDTO d = baseFields(b);
        List<YorumDTO> yorumDtos = new ArrayList<>();
        if (b.getYorumlar() != null) {
            for (Yorum y : b.getYorumlar()) {
                if (y != null) {
                    yorumDtos.add(YorumMapper.toDto(y));
                }
            }
        }
        d.setYorumlar(yorumDtos);
        return d;
    }

    private static BlogDTO baseFields(Blog b) {
        BlogDTO d = new BlogDTO();
        d.setId(b.getId());
        d.setBaslik(b.getBaslik());
        d.setOzet(b.getOzet());
        d.setIcerik(b.getIcerik());
        d.setKapakGorseliUrl(b.getKapakGorseliUrl());
        d.setTahminiOkumaSuresi(b.getTahminiOkumaSuresi());
        d.setEtiketler(b.getEtiketler());
        d.setOlusturulmaTarihi(b.getOlusturulmaTarihi());
        d.setDurum(b.getDurum());
        d.setKategori(KategoriMapper.toDto(b.getKategori()));
        d.setYazar(KullaniciMapper.toDto(b.getYazar()));
        return d;
    }

    public static List<BlogDTO> toListItems(List<Blog> blogs) {
        if (blogs == null || blogs.isEmpty()) {
            return new ArrayList<>();
        }
        List<BlogDTO> out = new ArrayList<>(blogs.size());
        for (Blog b : blogs) {
            BlogDTO dto = toListItem(b);
            if (dto != null) {
                out.add(dto);
            }
        }
        return out;
    }

    /**
     * Yeni blog kaydı: form DTO'sundan kalıcı {@link Blog} (içerik dahil).
     */
    public static Blog toNewBlogEntity(BlogDTO dto, Kategori kategori, Kullanici yazar,
                                       LocalDateTime olusturulmaTarihi, DurumTip durum) {
        if (dto == null) {
            throw new IllegalArgumentException("dto");
        }
        if (kategori == null || yazar == null) {
            throw new IllegalArgumentException("kategori/yazar");
        }
        Blog b = new Blog();
        b.setBaslik(dto.getBaslik() != null ? dto.getBaslik().trim() : null);
        b.setOzet(dto.getOzet() != null ? dto.getOzet().trim() : null);
        b.setIcerik(dto.getIcerik());
        b.setTahminiOkumaSuresi(dto.getTahminiOkumaSuresi());
        b.setKapakGorseliUrl(dto.getKapakGorseliUrl() != null && !dto.getKapakGorseliUrl().isBlank()
                ? dto.getKapakGorseliUrl().trim() : null);
        b.setEtiketler(dto.getEtiketler() != null && !dto.getEtiketler().isBlank() ? dto.getEtiketler().trim() : null);
        b.setOlusturulmaTarihi(olusturulmaTarihi);
        b.setDurum(durum);
        b.setYazar(yazar);
        b.setKategori(kategori);
        return b;
    }
}

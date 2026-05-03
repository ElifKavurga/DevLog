package dto;

import java.io.Serializable;

public class KategoriDTO implements Serializable {

    private Long id;
    private String isim;
    private String slug;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getKategoriAdi() {
        return isim;
    }
}

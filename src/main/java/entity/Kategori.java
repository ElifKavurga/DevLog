package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
public class Kategori implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kategoriadi", nullable = false, length = 200)
    private String isim;

    @Column(unique = true, length = 200)
    private String slug;

    @OneToMany(mappedBy = "kategori", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Blog> bloglar = new ArrayList<>();

    public Kategori() {}

    @PrePersist
    @PreUpdate
    private void slugYoksaIsimdenUret() {
        if (isim != null && !isim.isBlank() && (slug == null || slug.isBlank())) {
            slug = slugUret(isim);
        }
    }

    private static String slugUret(String ad) {
        String s = ad.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        s = s.replaceAll("^-+", "").replaceAll("-+$", "");
        return s.isEmpty() ? "kategori" : s;
    }

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

    /**
     * Eski kodla uyumluluk: kategori görünen adı {@link #isim} ile aynıdır.
     */
    public String getKategoriAdi() {
        return isim;
    }

    public void setKategoriAdi(String kategoriAdi) {
        this.isim = kategoriAdi;
    }

    public List<Blog> getBloglar() {
        return bloglar;
    }

    public void setBloglar(List<Blog> bloglar) {
        this.bloglar = bloglar;
    }
}

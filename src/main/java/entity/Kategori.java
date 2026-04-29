package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
public class Kategori implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String kategoriAdi;

    @OneToMany(mappedBy = "kategori", cascade = CascadeType.ALL)
    private List<Blog> bloglar;

    public Kategori() {}

    public List<Blog> getBloglar() {
        return bloglar;
    }

    public void setBloglar(List<Blog> bloglar) {
        this.bloglar = bloglar;
    }

    public String getKategoriAdi() {
        return kategoriAdi;
    }

    public void setKategoriAdi(String kategoriAdi) {
        this.kategoriAdi = kategoriAdi;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

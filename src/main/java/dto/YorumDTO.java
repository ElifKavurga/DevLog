package dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class YorumDTO implements Serializable {

    private Long id;
    private String metin;
    private LocalDateTime olusturulmaTarihi;
    private KullaniciDTO kullanici;
    private Long blogId;
    private String blogBaslik;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMetin() {
        return metin;
    }

    public void setMetin(String metin) {
        this.metin = metin;
    }

    public LocalDateTime getOlusturulmaTarihi() {
        return olusturulmaTarihi;
    }

    public void setOlusturulmaTarihi(LocalDateTime olusturulmaTarihi) {
        this.olusturulmaTarihi = olusturulmaTarihi;
    }

    public KullaniciDTO getKullanici() {
        return kullanici;
    }

    public void setKullanici(KullaniciDTO kullanici) {
        this.kullanici = kullanici;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public String getBlogBaslik() {
        return blogBaslik;
    }

    public void setBlogBaslik(String blogBaslik) {
        this.blogBaslik = blogBaslik;
    }
}

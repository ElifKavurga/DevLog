# DevLog

Jakarta EE 10 tabanlı bir **WAR** projesi: JPA (EclipseLink) ile PostgreSQL üzerinde blog, kullanıcı, kategori ve değerlendirme verilerini yönetmek için iskelet kod içerir.

## Klasör ve dosyalar

| Konum | Ne işe yarar |
|--------|----------------|
| `pom.xml` | Maven: `jakarta.jakartaee-api`, EclipseLink ve PostgreSQL **provided** (sunucunun kendi kütüphaneleri + `glassfish/lib` sürücüsü). |
| `src/main/java/entity/` | **JPA varlıkları** (veritabanı tablolarının nesne karşılığı). `Blog`, `Kullanici`, `Kategori`, `Degerlendirme` birbirine `@ManyToOne` / `@OneToMany` ile bağlıdır. |
| `src/main/java/enums/` | **`DurumTip`**: blog kaydının yaşam döngüsü (ör. taslak / yayında). **`RolTip`**: kullanıcı rolü. |
| `src/main/java/facade/` | **`@Stateless` EJB** + `@PersistenceContext`: GlassFish JTA havuzu üzerinden `EntityManager`; her public metotta konteyner transaction. |
| `src/main/java/controller/` | **Sunum / JSF tarafı**: `@Named` ve `@RequestScoped` bean’ler (ör. `KesfetController`). Arayüzden veya başka bean’lerden `@Inject` ile facade enjekte edilir. |
| `src/main/resources/META-INF/persistence.xml` | **JTA** birimi `default`: `jta-data-source` → **`java:app/jdbc/DevLogDS`**. |
| `src/main/java/config/DevLogDataSource.java` | **`@DataSourceDefinition`** + `@Singleton` `@Startup`: PostgreSQL JNDI kaynağını WAR içinde tanımlar (GlassFish’te `glassfish-resources.xml` işlenmese bile). |
| `src/main/resources/META-INF/beans.xml` | **CDI** etkinliği; `@Inject` ve `@ApplicationScoped` taraması için. |
| `src/main/webapp/WEB-INF/web.xml` | Servlet 6.0, **FacesServlet** (`*.xhtml`), varsayılan karşılama `index.xhtml`. |
| `src/main/webapp/WEB-INF/faces-config.xml` | JSF 4 yüz yapılandırması. |
| `src/main/webapp/index.xhtml` | **Ana sayfa** (Bootstrap + şablon): yayınlanan blog listesi, `KesfetController` ile veri. |
| `src/main/webapp/blog-detay.xhtml` | **Blog detayı** (`?id=`): `BlogDetayController` + `BlogFacade.bul`. |
| `src/main/webapp/WEB-INF/templates/mainTemplate.xhtml` | Ortak üst/alt bilgi ve Bootstrap/CSS iskeleti. |
| `src/main/webapp/resources/css/devlog.css` | Koyu IDE teması (referans tasarıma yakın renkler). |
| `src/main/webapp/resources/img/Logo.png` | Üst menüde kullanılan logo. |

## Facade sınıfları (kısa)

- **`BlogFacade`**: Blog ekleme, güncelleme, silme, tam listeleme, **duruma göre** listeleme, `bul(id)` ile detay, `ortalamaPuan(blogId)` ile yıldız ortalaması.
- **`KullaniciFacade`**: Kullanıcı CRUD ve `girisYap(eposta, sifre)` ile basit giriş sorgusu.
- **`KategoriFacade`**: Kategori CRUD.
- **`DegerlendirmeFacade`**: Değerlendirme (puan + kullanıcı + blog ilişkisi) CRUD.

## Controller örneği

- **`KesfetController`**: Ana sayfada `DurumTip.YAYINLANDI` blogları; kapak görseli indeksi, yazar/kategori/sürüm/okuma süresi/yıldız metni yardımcıları.
- **`BlogDetayController`**: `blog-detay.xhtml?id=…` ile tek yazı yükleme (`@ViewScoped`).

## Derleme ve yerel çalıştırma

```text
mvn clean package
```

### GlassFish ve veri kaynağı

JPA birimi **`java:app/jdbc/DevLogDS`** JNDI adını kullanır; kaynak `config.DevLogDataSource` içindeki **`@DataSourceDefinition`** ile oluşturulur. PostgreSQL sürücüsü WAR içindeki **`WEB-INF/lib`**’e (`postgresql` bağımlılığı, `runtime`) paketlenir; ayrıca `glassfish/lib`’e kopyalamanız gerekmez.

Bağlantı bilgisini değiştirmek için `DevLogDataSource.java` içindeki alanları güncelleyin. Uygulama (context `DevLog` ise): **http://localhost:8080/DevLog/** veya **…/DevLog/index.xhtml**.

### TomEE / diğer

`persistence.xml` şu an **GlassFish JTA + `jdbc/DevLogDS`** içindir. TomEE veya başka sunucuda çalıştırmak için aynı JNDI adında bir veri kaynağı tanımlamanız veya `persistence.xml` / havuz yapılandırmasını o sunucuya göre uyarlamanız gerekir.

```text
mvn clean package tomee:run
```

Üretilen WAR (`target/DevLog.war`) Payara / WildFly gibi sunuculara da taşınabilir; her ortamda JNDI veya JDBC ayarlarını eşlemeniz gerekir. Üretimde şifreyi `glassfish-resources.xml` içinde tutmamak daha güvenlidir (ör. şifre alias / ortam değişkeni).

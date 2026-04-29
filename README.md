# DevLog

**Jakarta EE 10** uyumlu bir **WAR** uygulaması. Ders / proje gereği uygulama **GlassFish 7.1.0** üzerinde çalıştırılır (Jakarta EE tam profil, JSF, EJB, JPA).

> **Not:** Projeyi derlemek için **Apache Maven** kullanılır; bu, uygulama sunucusu değildir. Çalışma zamanı sunucusu **GlassFish 7.1.0**’dır (ör. `C:\glassfish7\...`, IntelliJ’de yapılandırılmış **GlassFish 7.1.0** artifact’ı).

Veri katmanı: **JPA (EclipseLink)** + **PostgreSQL** (`@DataSourceDefinition` ile `java:app/jdbc/DevLogDS`).

## Klasör ve dosyalar

| Konum | Ne işe yarar |
|--------|----------------|
| `pom.xml` | Maven: `jakarta.jakartaee-api` ve EclipseLink **provided** (GlassFish sağlar); PostgreSQL sürücüsü **runtime** → WAR içinde `WEB-INF/lib`. |
| `src/main/java/entity/` | **JPA varlıkları**. `Blog`, `Kullanici`, `Kategori`, `Degerlendirme`. |
| `src/main/java/enums/` | **`DurumTip`**, **`RolTip`**. |
| `src/main/java/facade/` | **`@Stateless` EJB** + `@PersistenceContext`: GlassFish **JTA** ile `EntityManager`. |
| `src/main/java/controller/` | **JSF / CDI**: `@Named`, `@RequestScoped` / `@ViewScoped`; `@Inject` ile facade. |
| `src/main/resources/META-INF/persistence.xml` | **JTA** PU `default` → **`java:app/jdbc/DevLogDS`**. |
| `src/main/java/config/DevLogDataSource.java` | **`@DataSourceDefinition`**: PostgreSQL JNDI (GlassFish 7.1.0 + exploded deploy için uygun). |
| `src/main/resources/META-INF/beans.xml` | **CDI**. |
| `src/main/webapp/WEB-INF/web.xml` | Servlet 6.0, **FacesServlet** (`*.xhtml`), karşılama `index.xhtml`. |
| `src/main/webapp/WEB-INF/faces-config.xml` | JSF 4. |
| `src/main/webapp/index.xhtml` | Ana sayfa (Bootstrap). |
| `src/main/webapp/blog-detay.xhtml` | Yazı detayı (`?id=`). |
| `src/main/webapp/WEB-INF/templates/` … | Şablon ve parçalar; `resources/css`, `resources/img/Logo.png`. |

## Facade ve controller

- **`BlogFacade`**, **`KullaniciFacade`**, **`KategoriFacade`**, **`DegerlendirmeFacade`**: CRUD ve sorgular.
- **`KesfetController`**, **`BlogDetayController`**: JSF sayfalarına veri.

## Derleme (Maven)

```text
mvn clean package
```

Çıktı: **`target/DevLog.war`**. Bu dosyayı **GlassFish 7.1.0** admin konsolundan veya IntelliJ **Run/Debug** ile domain’e deploy edin.

## GlassFish 7.1.0 — çalıştırma özeti

1. **GlassFish 7.1.0** kurulu ve `domain1` (veya dersinizin istediği domain) ayakta olsun (varsayılan HTTP **8080**, admin **4848**).
2. IntelliJ’de **Application Server**: GlassFish 7.1.0, artifact: **DevLog:war** veya **war exploded** — ders talimatına uygun.
3. PostgreSQL’de **`DevLog`** veritabanı oluşturulmuş olsun; bağlantı bilgisi **`config/DevLogDataSource.java`** içinde (gerekirse host/kullanıcı/şifre güncellenir).
4. Tarayıcı (context `DevLog` ise): **http://localhost:8080/DevLog/** veya **http://localhost:8080/DevLog/index.xhtml**

Şifreyi kaynak kodda tutmamak için üretimde şifre alias veya güvenli konfigürasyon kullanın.

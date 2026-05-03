# DevLog — Proje Rehberi (Sıfırdan Büyük Resim)

Bu belge, temel Java, HTML ve CSS bilgisi olan biri için **DevLog** uygulamasının mimarisini, klasörlerini, veri akışını ve kullanılan Jakarta EE kavramlarını **adım adım** anlatır. Kod parçaları burada öğretim amaçlı özetlenir; satır içi açıklama yazmak yerine her kavram metinde açıklanır.

---

## 1. Bu proje ne iş yapar?

**DevLog**, blog yazılarının listelendiği, kategorilendiği, kullanıcıların giriş yaptığı, yazar ve yönetici rolleriyle içerik ve onay süreçlerinin yönetildiği bir **web uygulamasıdır**. Tarayıcıdan **XHTML (Facelets)** sayfaları açılır; arka planda **Jakarta EE 10** bileşenleri (özellikle **JSF**, **CDI**, **JPA**, **EJB**) çalışır. Veri **PostgreSQL** veritabanında tutulur. Arayüzde **Bootstrap 5** ve proje özel **CSS** kullanılır.

---

## 2. İki farklı “motor”: Maven ile GlassFish karışmasın

| Kavram | Basit analoji | Bu projede rolü |
|--------|----------------|-----------------|
| **Maven** | Restoranın **malzeme listesi ve paketleme bandı** | `pom.xml` ile hangi kütüphanelerin projeye dahil edileceğini ve `mvn package` ile **WAR dosyası** üretimini yönetir. Maven kendi başına kullanıcıya sayfa göstermez. |
| **GlassFish (uygulama sunucusu)** | Restoranın **mutfağı ve salonu** | WAR dosyasını alır, içindeki servletleri, JSF’yi, EJB’leri, JPA’yı **çalışma zamanında** çalıştırır; HTTP isteğini karşılar. |

Özet: **Derleme** Maven ile; **çalıştırma** GlassFish (veya dersinizde başka bir Jakarta EE tam uyumlu sunucu) ile yapılır.

---

## 3. `pom.xml` — projenin “kimlik kartı ve tedarik listesi”

**Konum:** proje kökünde `pom.xml`.

**Ne işe yarar?**

- **Kimlik:** `groupId`, `artifactId`, `version` projeyi benzersiz tanımlar.
- **Paket türü:** `<packaging>war</packaging>` → çıktı bir **Web ARchive** (`.war`) dosyasıdır; içinde derlenmiş sınıflar, `WEB-INF`, web sayfaları ve bağımlı JAR’lar toplanır.
- **Java sürümü:** Bu projede kaynak hedefi **21** olarak ayarlanmıştır.
- **Bağımlılıklar (`<dependencies>`):**
  - **`jakarta.jakartaee-api`** (`scope` **provided**): Jakarta EE API’leri derleme için burada; sunucu çalışırken kendi kopyasını sağlar, WAR şişmez.
  - **`jakarta.security.enterprise-api`** (**provided**): Güvenlik API’si (ör. şifre hash) için.
  - **`org.eclipse.persistence.jpa`** (**provided**): JPA sağlayıcısı EclipseLink; GlassFish ile uyumlu kullanım için genelde sunucudan gelir, projede “provided” bırakılabilir.
  - **`postgresql`** (**runtime**): PostgreSQL sürücüsü WAR içine (`WEB-INF/lib`) konur; sunucunun “içinden” değil, uygulamanın yanında taşınır.

**`<build><finalName>DevLog</finalName>`:** Paket adı `DevLog.war` olur.

**Öğrenme sırası:** Yeni bir Jakarta EE WAR projesine başlarken çoğu zaman **ilk dokunulan dosya `pom.xml`** olur; çünkü dil seviyesi, API ve paketleme burada sabitlenir.

---

## 4. WAR içi klasör yapısı (Maven standartları)

| Konum | İçerik |
|--------|--------|
| `src/main/java/` | Tüm **Java** kaynakları: `entity`, `dto`, `mapper`, `facade`, `facadeLocal`, `controller`, `config`, `enums` paketleri. |
| `src/main/resources/META-INF/` | `persistence.xml` (JPA), `beans.xml` (CDI). |
| `src/main/webapp/` | **Web kökü:** XHTML sayfaları, `WEB-INF` (web.xml, faces-config, şablonlar), `resources` (CSS, görseller). |

`mvn clean package` sonrası **`target/DevLog.war`** üretilir; deploy edilen şey budur.

---

## 5. Katman sırası: Entity → DTO → Mapper → Facade → Controller — neden bu sıra?

**Restoran analojisi:**

1. **Entity:** Depodaki **gerçek stok kartı** — veritabanındaki satırlarla eşleşen kalıcı model (JPA).
2. **DTO:** Müşteriye çıkan **tabakta görünen porsiyon** — ekrana veya dış dünyaya taşınan, alanları bilinçli seçilmiş veri taşıyıcısı.
3. **Mapper:** Depo kartından tabağa **aktarımı yapan mutfak personeli** — entity ile DTO arasında dönüşüm tek yerde toplanır.
4. **Facade:** **Mutfak şefi** — birden fazla tablo, kural, transaction ihtiyacını tek kapıdan yönetir; controller doğrudan `EntityManager` ile uğraşmaz.
5. **Controller (JSF bean):** **Garson** — kullanıcı eylemini alır, facade’e iletir, sonucu sayfaya bağlanabilir özelliklere koyar.

**Neden öğrenirken bu sıra?** Önce verinin **veritabanı karşılığını** (entity), sonra **dışarı ne çıkacağını** (DTO), sonra **dönüşümü** (mapper), sonra **iş kuralı ve kalıcılık** (facade), en son **ekran ve navigasyon** (controller) netleşir; tersine gidersen sayfa yazsan bile veri modeli dağınık kalır.

---

## 6. Veritabanı bağlantısı: JNDI, `persistence.xml` ve GlassFish kaynakları

### 6.1. `persistence.xml` — JPA’ya “hangi veri kaynağı?”

**Konum:** `src/main/resources/META-INF/persistence.xml`.

- **`persistence-unit` adı:** `default` — kodda `@PersistenceContext(unitName = "default")` ile eşleşir.
- **`transaction-type="JTA"`:** İşlemler uygulama sunucusunun **transaction yöneticisi** ile yönetilir; EJB katmanıyla doğal uyum.
- **`jta-data-source`:** `java:app/jdbc/DevLogDS` — bu isim bir **JNDI adresi**dir; “veritabanına bağlan” demek yerine “sunucunun kayıtlı havuzuna bağlan” dersin.
- **`provider`:** EclipseLink.
- **`<class>` listesi:** JPA’nın bilmesi gereken entity sınıfları açıkça listelenmiştir.
- **Özellikler:** Örneğin `eclipselink.ddl-generation` ile geliştirme ortamında şema/tabloların oluşturulması veya genişletilmesi EclipseLink’e bırakılabilir (üretimde strateji ayrı düşünülmelidir).

### 6.2. `glassfish-resources.xml` — havuz ve JNDI isminin tanımı

**Konum:** `src/main/webapp/WEB-INF/glassfish-resources.xml`.

GlassFish, deploy sırasında buradaki **`jdbc-connection-pool`** ve **`jdbc-resource`** tanımlarını işleyebilir; böylece **`java:app/jdbc/DevLogDS`** adı gerçek bir **DataSource**’a bağlanır (PostgreSQL sunucu, port, veritabanı adı, kullanıcı burada tanımlanır).

**Önemli pedagojik not:** Şifre ve bağlantı bilgisi bu dosyada düz metin olarak duruyorsa bu **sadece geliştirme** içindir. Üretimde güvenli konfigürasyon, sırların sunucu tarafında yönetimi ve erişim kontrolü hedeflenmelidir.

### 6.3. YAML veya `.properties` nerede?

Bu projede veritabanı bağlantısı **GlassFish kaynak XML’i** üzerinden kurgulanmıştır. Başka projelerde **MicroProfile Config** veya ortam değişkenleri ile `.properties` / YAML okuma da yaygındır; mantık aynıdır: **uygulama kodundan bağımsız** ortam ayarı.

---

## 7. JPA Entity katmanı (`entity/`)

Entity sınıfları (`Blog`, `Kullanici`, `Kategori`, `Yorum`, `Degerlendirme`, `Bildirim`, `SistemLog` vb.) veritabanı tablolarını temsil eder.

**Sık görülen anotasyonlar (ne işe yarar?):**

| Anotasyon / kavram | Kısa açıklama |
|--------------------|----------------|
| `@Entity` | Sınıfın JPA tarafından yönetilen bir varlık olduğunu bildirir. |
| `@Table(name = "...")` | Tablo adı; sınıf adından farklı olabilir. |
| `@Id`, `@GeneratedValue` | Birincil anahtar ve üretim stratejisi. |
| `@Column` | Sütun adı, uzunluk, `TEXT` gibi özel tanımlar. |
| `@ManyToOne`, `@OneToMany`, `@JoinColumn` | Tablolar arası ilişki ve yabancı anahtar. |
| `@Enumerated(EnumType.STRING)` | Enum değerinin veritabanında sayı yerine metin saklanması. |
| **`@PrePersist` / `@PreUpdate`** (projede kullanıldıysa) | Kayıt öncesi veya güncelleme öncesi otomatik alan doldurma (ör. zaman damgası) için yaşam döngüsü kancaları. |

**Persistence context:** `EntityManager` üzerinden yönetilen entity örnekleri kümesi; facade içinde `@PersistenceContext` ile enjekte edilir.

---

## 8. EJB Facade ve yerel arayüz (`facade/` + `facadeLocal/`)

**Desen:** Her iş alanı için **`XxxFacadeLocal`** adında bir **arayüz** (`@Local`) ve **`XxxFacade`** adında **`@Stateless`** bir **oturumsuz (stateless) EJB** sınıfı bulunur.

**Neden arayüz?** Controller veya başka bir EJB, somut sınıf yerine **arayüz** üzerinden (`@EJB` veya uygun enjeksiyon) bağlanır; test ve gevşek bağlılık kolaylaşır.

**Facade içinde tipik öğeler:**

- `@PersistenceContext(unitName = "default")` **EntityManager** — CRUD ve sorgular.
- `@Stateless` — Her çağrıda havuzdan bean örneği; transaction sınırları EJB kurallarına uygun.
- İş kuralları: örneğin kullanıcı oluştururken düz metin şifreyi **`Pbkdf2PasswordHash`** ile hash’leme (`KullaniciFacade`).

**Özet:** Veritabanına doğrudan JSF bean’inden gitmek yerine **facade** kullanılır; bu projenin “mutfak şefi” katmanıdır.

---

## 9. DTO ve Mapper (`dto/`, `mapper/`)

**DTO (Data Transfer Object):** `BlogDTO`, `KullaniciDTO` gibi sınıflar, ekrana veya katmanlar arası taşımaya **özel** seçilmiş alanları taşır. Entity’nin tüm ilişki ağını veya iç alanları her yerde dolaştırmak zorunda kalmazsın.

**Mapper:** Statik veya yardımcı metotlarla `entity ↔ dto` dönüşümü tek yerde toplanır (`BlogMapper`, `KullaniciMapper` vb.). Örneğin `OturumController` içinde aktif kullanıcı entity olarak tutulurken arayüzde `KullaniciMapper.toDto(...)` ile **DTO** sunulabilir.

---

## 10. Controller katmanı (`controller/`) — JSF ve CDI

Bu paket altında alt paketlerle düzen vardır: **`publicweb`**, **`panel`**, **`cms`**, **`admin`**, kökte **`GirisController`**, **`KayitController`**, **`OturumController`**, **`PanelYolController`** vb.

**Sık anotasyonlar:**

| Anotasyon | Rolü |
|-----------|------|
| `@Named("beanAdi")` | JSF ifadelerinde `#{beanAdi.ozellik}` ile erişilen CDI isimli bean. |
| `@RequestScoped` | İstek boyunca yaşar; form gönderimi gibi tek round-trip senaryolarına uyar. |
| `@ViewScoped` | Aynı JSF **view** (sayfa) yaşarken bean yaşar; AJAX veya çok adımlı formlar için uygundur; sınıf **`Serializable`** olmalıdır. |
| `@SessionScoped` | Tarayıcı oturumu boyunca; örneğin giriş yapmış kullanıcı bilgisi (`OturumController`). Yine **Serializable** gerekir. |
| `@Inject` | CDI ile başka bir bean veya bağımlılık enjeksiyonu. |
| `@EJB` | EJB arayüzüne (ör. `KullaniciFacadeLocal`) enjeksiyon. |

**`@PostConstruct`:** Bean oluşturulduktan sonra bir kez çalışan hazırlık metodu için kullanılır (örnek: sayfa açılışında listeyi yükleme).

---

## 11. `config/` — uygulama açılışı ve örnek veri

### 11.1. `DevLogSeedListener`

**`@WebListener`** ile `ServletContextListener` uygulanır: uygulama **context başlarken** (`contextInitialized`) çalışır.

**Görevi (yüksek seviye):** Veritabanı şema/uyumluluk kontrolleri için facade metotlarını tetiklemek, hata durumunda log yazıp uygulamanın tamamen çökmesini engellemeye çalışmak, ardından **`DataInitializer`** ile varsayılan kullanıcı ve kategori gibi **tohum veriyi** oluşturmak.

### 11.2. `DataInitializer`

**`@Singleton`** EJB: uygulama genelinde **tek** mantıksal örnek (container yönetimli). İçinde **`@EJB`** ile facade’ler kullanılır; `seedDefaults()` admin ve demo kullanıcılar ile boşsa varsayılan kategori ekler.

**Analoji:** Restoran sabah açılırken “varsayılan menü ve deneme hesapları hazır mı?” kontrolü.

---

## 12. Web katmanı: `web.xml`, JSF, Facelets, Bootstrap

### 12.1. `WEB-INF/web.xml`

- **Faces Servlet:** `*.xhtml` isteklerini JSF’ye yönlendirir.
- **`welcome-file`:** Açılışta `public/index.xhtml` hedeflenir.
- **Hata sayfaları:** 500 ve genel `Throwable` için `public/error.xhtml` yönlendirmesi.
- **Context parametreleri:** Örneğin `PROJECT_STAGE` Development; `FACELETS_SKIP_COMMENTS` ile Facelets çıktısında yorumların atlanması gibi JSF davranışları.

### 12.2. `WEB-INF/faces-config.xml`

JSF 4 uyumlu boş veya minimal yapılandırma dosyası; navigasyon veya bean tanımı ileride buraya veya anotasyonlara taşınabilir.

### 12.3. Sayfa yerleşimi (`webapp/`)

| Alan | Örnek yollar | Kimler |
|------|----------------|--------|
| **Herkese açık** | `public/index.xhtml`, `public/blog-detay.xhtml`, `public/kategoriler.xhtml` | Ziyaretçi |
| **Kimlik** | `auth/giris.xhtml`, `auth/kayit.xhtml` | Giriş / kayıt |
| **Kullanıcı paneli** | `panel/profil.xhtml`, `panel/bloglarim.xhtml`, … | Giriş yapmış kullanıcı |
| **Yazar CMS** | `panel/yeni-blog.xhtml` ve ilgili controller’lar | Yazma yetkisi |
| **Yönetim** | `admin/kategoriler.xhtml`, `admin/sistem-loglari.xhtml`, … | Admin / onay süreçleri |

### 12.4. Şablonlar (`WEB-INF/templates/`, `WEB-INF/fragments/`)

**Facelets** ile `ui:composition`, `ui:define`, `ui:insert`, `ui:include` kullanılır. Örneğin `publicTemplate.xhtml` ortak `h:head`, Bootstrap CSS CDN linki, `h:outputStylesheet` ile `devlog.css`, gövde düzeni ve parça include’ları tanımlar; alt sayfalar sadece `content` ve `title` gibi bölgeleri doldurur.

**Bootstrap:** CDN üzerinden CSS ve JS dahil edilir; grid, bileşen ve yardımcı sınıflar HTML tarafında kullanılır. **`h:`** ve **`ui:`** etiketleri JSF tarafından sunucuda işlenir; tarayıcıya çoğunlukla düz HTML gider.

### 12.5. Backend’den Bootstrap’li HTML’e veri akışı (kargo analojisi)

1. Tarayıcı bir **URL** ister (ör. `/DevLog/public/index.xhtml`).
2. GlassFish isteği **Faces Servlet**’e verir; JSF **lifecycle** başlar.
3. Sayfadaki `#{kesfetController.bloglar}` gibi ifadeler ilgili **`@Named`** bean’in özelliklerine bağlanır.
4. Bean, gerekirse **`@Inject` / `@EJB`** ile **facade**’i çağırır; facade **EntityManager** ile veritabanından **entity** listesi alır.
5. Liste **DTO**’ya map edilir veya doğrudan view’de kullanılacak şekilde bean’de tutulur.
6. JSF **render** aşamasında **HTML** üretilir; Bootstrap sınıfları görünümü düzenler.

**Kargo analojisi:** İstek **sipariş**; controller **sipariş masası**; facade **dağıtım merkezi**; veritabanı **depo**; HTML cevap **kapıya gelen paket**; Bootstrap **paketin düzenli sunumu**.

---

## 13. `beans.xml` — CDI’yi açıkça etkinleştirme

**Konum:** `src/main/resources/META-INF/beans.xml`.

CDI 4.x şeması ile boş bir `beans` kökü, sınıf yolunda CDI taramasının ve `@Inject` kullanımının beklendiği ortamlarda netlik sağlar.

---

## 14. Öğrenme yol haritası (projeyi sıfırdan anlamak için önerilen sıra)

1. **`pom.xml`** — bağımlılıklar ve WAR çıktısı.
2. **`web.xml`** — JSF nasıl devreye giriyor, karşılama dosyası nerede.
3. **`persistence.xml` + `glassfish-resources.xml`** — JNDI ismi ile veritabanı bağlantı zinciri.
4. **Bir entity** (ör. `Blog`) — tablo ve ilişkiler.
5. **Bir facade çifti** (ör. `BlogFacadeLocal` + `BlogFacade`) — `EntityManager` kullanımı.
6. **Bir DTO + Mapper** — entity ile ekran verisinin ayrılması.
7. **Bir public controller + bir XHTML** — `#{...}` bağları ve liste/detay.
8. **`OturumController`** — oturum kapsamı ve rol bayrakları.
9. **`DevLogSeedListener` + `DataInitializer`** — açılış ve örnek veri.
10. **Admin ve panel** sayfaları — aynı desenin farklı rollerde tekrarı.

---

## 15. Derleme ve çalıştırma özeti

1. **PostgreSQL** üzerinde projenin kullandığı veritabanı adıyla veritabanının var olduğundan emin olun (`glassfish-resources.xml` ile uyumlu).
2. Bağlantı bilgilerini kendi ortamınıza göre güncelleyin.
3. Proje kökünde: `mvn clean package`
4. **`target/DevLog.war`** dosyasını **GlassFish 7.x** (Jakarta EE 10 uyumlu) domain’e deploy edin veya IDE üzerinden artifact olarak çalıştırın.
5. Tarayıcıda context path genelde **`/DevLog`** olur; tam adres IDE veya domain ayarına bağlıdır (örnek: `http://localhost:8080/DevLog/`).

---

## 16. Bu belge ile kod içi yorum ilişkisi

Projede öğretim açıklamaları **bu README üzerinden** verilir; kaynak kodda açıklama satırı eklememe tercihin varsa yeni kodda da aynı disiplin korunabilir. Mevcut XML dosyalarında tarihsel açıklama satırları varsa bunlar aşamalı olarak temizlenebilir; davranışı değiştirmezler.

---

## 17. Hızlı referans: paket → sorumluluk

| Paket / konum | Sorumluluk |
|-----------------|------------|
| `entity` | Veritabanı eşlemesi, JPA anotasyonları |
| `enums` | Sabit liste tipleri (`RolTip`, `DurumTip`) |
| `dto` | Taşınan veri şekilleri |
| `mapper` | Entity ↔ DTO dönüşümü |
| `facadeLocal` | EJB yerel arayüzler |
| `facade` | Stateless EJB uygulamaları, JPA erişimi |
| `controller` | JSF bean’leri, navigasyon, form eylemleri |
| `config` | Açılış dinleyicisi, singleton tohum veri |
| `webapp/public`, `auth`, `panel`, `admin` | Facelets sayfaları |
| `WEB-INF/templates`, `fragments` | Ortak düzen ve parçalar |

Bu tablo, projede “dosyayı açınca ne bekleyeceğini” hızlı hatırlatır.

---

*Son güncelleme: README, mevcut DevLog kaynak ağacına göre yazılmıştır; bağımlılık sürümleri veya sunucu sürümü `pom.xml` ve dağıtım ortamınızla birlikte doğrulanmalıdır.*

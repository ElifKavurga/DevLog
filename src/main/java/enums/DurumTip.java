package enums;

/** Blog yazısının yayın durumu. */
public enum DurumTip {
    YAYINLANDI,
    /** Eski kayıtlarda kullanılan değer; yeni kayıtlar için {@link #ONAY_BEKLIYOR} tercih edilir. */
    BEKLIYOR,
    ONAY_BEKLIYOR,
    REDDEDILDI,
    TASLAK
}

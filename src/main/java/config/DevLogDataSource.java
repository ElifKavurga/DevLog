package config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

/**
 * Hedef uygulama sunucusu: GlassFish 7.1.0 (Jakarta EE 10).
 * JNDI veri kaynağı WAR içinden tanımlanır; PostgreSQL sürücüsü {@code WEB-INF/lib}
 * üzerinden gelir (pom'da {@code postgresql} runtime).
 */
@Startup
@Singleton
@DataSourceDefinition(
        name = "java:app/jdbc/DevLogDS",
        className = "org.postgresql.ds.PGSimpleDataSource",
        serverName = "localhost",
        portNumber = 5432,
        databaseName = "DevLog",
        user = "postgres",
        password = "29041983"
)
public class DevLogDataSource {
}

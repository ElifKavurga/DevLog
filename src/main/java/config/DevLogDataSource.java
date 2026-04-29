package config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

/**
 * GlassFish / IntelliJ exploded deploy sırasında {@code glassfish-resources.xml}
 * her zaman işlenmeyebilir; bu sınıf JNDI veri kaynağını WAR içinden tanımlar.
 * PostgreSQL sürücüsü {@code WEB-INF/lib} içinde olmalıdır (pom’da runtime).
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

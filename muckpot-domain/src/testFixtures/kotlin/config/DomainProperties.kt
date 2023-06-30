package config

object DomainProperties {
    init {
        System.setProperty("spring.jpa.hibernate.ddl-auto", "update")
        System.setProperty("spring.jpa.generate-ddl", "true")
        System.setProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.spatial.dialect.mariadb.MariaDB103SpatialDialect")
        System.setProperty("spring.jpa.properties.hibernate.format_sql", "true")
        System.setProperty("spring.jpa.show_sql", "true")
        System.setProperty("logging.level.org.hibernate.type", "trace")
    }
}

package config

object InfraProperties {
    init {
        System.setProperty("aws.ses.access-key", "test")
        System.setProperty("aws.ses.secret-key", "test")
        System.setProperty("aws.ses.region", "test")
    }
}

package config

object InfraProperties {
    init {
        System.setProperty("spring.mail.host", "smtp.gmail.com")
        System.setProperty("spring.mail.port", "8080")
        System.setProperty("spring.mail.username", "test")
        System.setProperty("spring.mail.password", "test")
    }
}

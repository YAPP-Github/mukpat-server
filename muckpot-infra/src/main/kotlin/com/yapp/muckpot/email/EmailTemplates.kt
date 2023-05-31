package com.yapp.muckpot.email

object EmailTemplates {

    const val AUTH_EMAIL_SUBJECT = "먹팟 이메일 인증 요청입니다."
    val AUTH_EMAIL_TEXT = EmailTemplate(
        "<span style=\"font-family:Arial,sans-serif\">\n" +
            "  <span style=\"font-weight: bold;font-size: 48px;\">먹팟 for <span style=\"color:#007bd9\">Samsung</span>\n" +
            "  </span>\n" +
            "  <br>\n" +
            "  <br>\n" +
            "  <span style=\"font-size:18px;\">안녕하세요, 먹팟 계정 생성을 환영합니다! <br>요청하신 인증코드는 아래와 같습니다 <br>\n" +
            "    <span style=\"font-size: 30px;color:#007bd9;font-weight: bold\">%s</span>\n" +
            "    <br>\n" +
            "    <br>\n" +
            "    <hr>\n" +
            "    <br>\n" +
            "    <span style=\"font-size:12px;\">해당 이메일은 발신 전용입니다. 기타 문의사항은 하기의 카카오톡 채널을 통해 연락주시길 바랍니다. <br>\n" +
            "      <a href=\"http://pf.kakao.com/_NVcQxj\">http://pf.kakao.com/_NVcQxj </a>\n" +
            "      <br>\n" +
            "      <br>감사합니다. <br> 먹팟 코리아 </span>\n" +
            "  </span>\n" +
            "</span>"
    )

    class EmailTemplate(private val text: String) {
        fun formatText(vararg args: Any): String {
            return text.format(*args)
        }
    }
}

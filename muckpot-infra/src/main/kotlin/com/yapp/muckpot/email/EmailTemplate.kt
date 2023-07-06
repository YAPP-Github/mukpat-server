package com.yapp.muckpot.email

enum class EmailTemplate(
    val subject: String,
    private val body: String
) {
    // 인증 번호
    AUTH_EMAIL(
        "먹팟 이메일 인증 요청입니다.",
        MAIL_BASIC_FORMAT.format(
            "  <br>\n" +
                "  <br>\n" +
                "  <span style=\"font-size:18px;\">안녕하세요, 먹팟 계정 생성을 환영합니다! <br>요청하신 인증코드는 아래와 같습니다 <br>\n" +
                "  <span style=\"font-size: 30px;color:#007bd9;font-weight: bold\">%s</span>\n" +
                "  <br>\n" +
                "  <br>\n"
        )
    ),

    // 먹팟 기존 글 제목, 변경 내용
    BOARD_UPDATE_EMAIL(
        "신청하신 \"%s\"을 작성자가 수정했습니다.",
        MAIL_BASIC_FORMAT.format(
            "  <br>\n" +
                "  <br>\n" +
                "  <span style=\"font-size:18px;\">신청하신 \"%s\"을 작성자가 수정했습니다. </span>\n" +
                "  %s" +
                "  <br>\n"
        )
    );

    fun formatSubject(vararg args: Any): String {
        return subject.format(*args)
    }

    fun formatBody(vararg args: Any): String {
        return body.format(*args)
    }

    companion object {
        fun createBoardUpdateRow(changeField: String, before: Any?, after: Any?): String {
            return BOARD_UPDATE_ROW.format(changeField, before.toString(), after.toString())
        }
    }
}

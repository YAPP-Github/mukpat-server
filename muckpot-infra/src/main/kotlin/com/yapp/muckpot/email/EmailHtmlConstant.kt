package com.yapp.muckpot.email

private const val HEADER_TITLE = "<span style=\"font-weight: bold;font-size: 48px;\">" +
    "먹팟 for <span style=\"color:#007bd9\">Samsung</span>\n" +
    "</span>\n"

private const val TAIL = "<hr>\n" +
    "<br>\n" +
    "<span style=\"font-size:12px;\">해당 이메일은 발신 전용입니다. 기타 문의사항은 하기의 카카오톡 채널을 통해 연락주시길 바랍니다. <br>\n" +
    "      <a href=\"http://pf.kakao.com/_NVcQxj\">http://pf.kakao.com/_NVcQxj </a>\n" +
    "      <br>\n" +
    "      <br>감사합니다. <br> 먹팟 코리아 " +
    "</span>\n"

const val MAIL_BASIC_FORMAT = "<span style=\"font-family:Arial,sans-serif\">\n" +
    HEADER_TITLE +
    "%s" +
    TAIL +
    "</span>"

const val BOARD_UPDATE_ROW = "<br>\n" +
    "  <br>\n" +
    "  <span style=\"font-size:14px;\">\n" +
    "  %s 변경되었습니다.\n" +
    "  <br>\n" +
    "  변경 전 : %s\n" +
    "  <br>\n" +
    "  변경 후 : %s\n" +
    "  </span>\n"

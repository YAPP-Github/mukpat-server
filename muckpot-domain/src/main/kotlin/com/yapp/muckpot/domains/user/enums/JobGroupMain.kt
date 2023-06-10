package com.yapp.muckpot.domains.user.enums

import java.lang.IllegalArgumentException

enum class JobGroupMain(val korName: String) {
    DEVELOPMENT("개발"),
    EDUCATION("교육"),
    FINANCE("금융/재무"),
    PLANNING("기획/경영"),
    DATA("데이터"),
    DESIGN("디자인"),
    MARKETING("마케팅/시장조사"),
    MEDIA("미디어/홍보"),
    LAW("법률/법무"),
    PRODUCTION("생산/제조"),
    PRODUCTION_MANAGEMENT("생산관리/품질관리"),
    SERVICE("서비스/고객지원"),
    ENGINEERING("엔지니어링"),
    R_D("연구개발"),
    SALES("영업/제휴"),
    DISTRIBUTION("유통/무역"),
    MEDICINE("의약"),
    HUMAN_AFFAIRS("인사/총무"),
    PROFESSIONAL("전문직"),
    PUBLIC("특수계층/공공");

    companion object {
        fun findByKorName(korName: String): JobGroupMain {
            return values().find { it.korName == korName }
                ?: throw IllegalArgumentException("직군 대분류가 존재하지 않습니다.")
        }
    }
}

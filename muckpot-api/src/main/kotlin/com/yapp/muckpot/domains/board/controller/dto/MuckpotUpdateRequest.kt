package com.yapp.muckpot.domains.board.controller.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.yapp.muckpot.common.Location
import com.yapp.muckpot.common.constants.AGE_MAX
import com.yapp.muckpot.common.constants.AGE_MIN
import com.yapp.muckpot.common.constants.CHAT_LINK_MAX
import com.yapp.muckpot.common.constants.CONTENT_MAX
import com.yapp.muckpot.common.constants.CONTENT_MAX_INVALID
import com.yapp.muckpot.common.constants.HHmm
import com.yapp.muckpot.common.constants.LINK_MAX_INVALID
import com.yapp.muckpot.common.constants.MAX_APPLY_MIN_INVALID
import com.yapp.muckpot.common.constants.NOT_BLANK_COMMON
import com.yapp.muckpot.common.constants.TITLE_MAX
import com.yapp.muckpot.common.constants.TITLE_MAX_INVALID
import com.yapp.muckpot.common.constants.YYYYMMDD
import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.board.entity.Province
import com.yapp.muckpot.domains.board.exception.BoardErrorCode
import com.yapp.muckpot.email.EmailTemplate
import com.yapp.muckpot.exception.MuckPotException
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.validator.constraints.Length
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

@ApiModel(value = "먹팟수정 요청")
data class MuckpotUpdateRequest(
    @field:ApiModelProperty(notes = "만날 날짜", required = true, example = "2023-12-21")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = YYYYMMDD)
    val meetingDate: LocalDate,
    @field:ApiModelProperty(notes = "만날 시간", required = true, example = "13:00")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = HHmm)
    val meetingTime: LocalTime,
    @field:ApiModelProperty(notes = "최대 참여 인원", required = true, example = "5")
    @field:Min(2, message = MAX_APPLY_MIN_INVALID)
    val maxApply: Int = 2,
    @field:ApiModelProperty(notes = "최소 나이", required = false, example = "20")
    val minAge: Int? = null,
    @field:ApiModelProperty(notes = "최대 나이", required = false, example = "100")
    val maxAge: Int? = null,
    @field:ApiModelProperty(notes = "주소", required = true, example = "서울 성북구 안암동5가 104-30 캐치카페 안암")
    var locationName: String,
    @field:ApiModelProperty(notes = "주소 상세", required = false, example = "6층")
    var locationDetail: String? = null,
    @field:ApiModelProperty(notes = "x 좌표", required = true, example = "127.02970799701643")
    val x: Double,
    @field:ApiModelProperty(notes = "y 좌표", required = true, example = "37.58392327180857")
    val y: Double,
    @field:ApiModelProperty(notes = "제목", required = true, example = "같이 밥묵으실분")
    @field:Length(max = TITLE_MAX, message = TITLE_MAX_INVALID)
    @field:NotBlank(message = NOT_BLANK_COMMON)
    var title: String,
    @field:ApiModelProperty(notes = "내용", required = false, example = "내용 입니다.")
    @field:Length(max = CONTENT_MAX, message = CONTENT_MAX_INVALID)
    var content: String? = null,
    @field:ApiModelProperty(notes = "오픈채팅방 링크", required = true, example = "https://open.kakao.com/o/gSIkvvHc")
    @field:Length(max = CHAT_LINK_MAX, message = LINK_MAX_INVALID)
    @field:NotBlank(message = NOT_BLANK_COMMON)
    var chatLink: String,
    @field:ApiModelProperty(notes = "시/도", required = true, example = "경기도")
    @field:NotBlank(message = NOT_BLANK_COMMON)
    var region_1depth_name: String,
    @field:ApiModelProperty(notes = "구/군", required = true, example = "용인시 기흥구")
    @field:NotBlank(message = NOT_BLANK_COMMON)
    var region_2depth_name: String
) {
    init {
        title = title.trim()
        content = content?.trim()
        chatLink = chatLink.trim()
        locationDetail = locationDetail?.trim()
        region_1depth_name = region_1depth_name.trim()
        region_2depth_name = region_2depth_name.trim()
    }

    fun updateBoard(board: Board, province: Province) {
        if (this.maxApply < board.currentApply) {
            throw MuckPotException(BoardErrorCode.MAX_APPLY_UPDATE_FAIL)
        }
        board.title = this.title
        board.content = this.content
        board.location = Location(locationName, x, y)
        board.locationDetail = this.locationDetail
        board.meetingTime = LocalDateTime.of(meetingDate, meetingTime)
        board.minAge = minAge ?: AGE_MIN
        board.maxAge = maxAge ?: AGE_MAX
        board.maxApply = this.maxApply
        board.chatLink = this.chatLink
        board.province = province
    }

    fun createBoardUpdateMailBody(board: Board): String {
        val modifyBody = StringBuffer()
        if (this.title != board.title) {
            modifyBody.append(
                EmailTemplate.createBoardUpdateRow("제목이", board.title, this.title)
            )
        }
        val meetingLocalTime = board.meetingTime.toLocalTime()
        if (this.meetingTime != meetingLocalTime) {
            modifyBody.append(
                EmailTemplate.createBoardUpdateRow("시간이", meetingLocalTime, this.meetingTime)
            )
        }
        val meetingLocalDate = board.meetingTime.toLocalDate()
        if (this.meetingDate != meetingLocalDate) {
            modifyBody.append(
                EmailTemplate.createBoardUpdateRow("날짜가", meetingLocalDate, this.meetingDate)
            )
        }
        if (this.maxApply != board.maxApply) {
            modifyBody.append(
                EmailTemplate.createBoardUpdateRow("인원이", board.maxApply, this.maxApply)
            )
        }
        if (this.locationName != board.location.locationName) {
            modifyBody.append(
                EmailTemplate.createBoardUpdateRow("만날 위치가", board.location.locationName, this.locationName)
            )
        }
        if (this.locationDetail != board.locationDetail) {
            modifyBody.append(
                EmailTemplate.createBoardUpdateRow("상세 주소가", board.locationDetail, this.locationDetail)
            )
        }
        if (this.content != board.content) {
            modifyBody.append(
                EmailTemplate.createBoardUpdateRow("내용이", board.content, this.content)
            )
        }
        if (this.chatLink != board.chatLink) {
            modifyBody.append(
                EmailTemplate.createBoardUpdateRow("오픈 채팅방 링크가", board.chatLink, this.chatLink)
            )
        }
        if ((this.minAge != board.minAge) || (this.maxAge != board.maxAge)) {
            val ageLimitFormat = "%d ~ %d"
            modifyBody.append(
                EmailTemplate.createBoardUpdateRow(
                    "나이 제한이",
                    ageLimitFormat.format(board.minAge, board.maxAge),
                    ageLimitFormat.format(this.minAge, this.maxAge)
                )
            )
        }
        return modifyBody.toString()
    }
}

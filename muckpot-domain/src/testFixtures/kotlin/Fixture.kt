import com.yapp.muckpot.common.Location
import com.yapp.muckpot.common.constants.AGE_MAX
import com.yapp.muckpot.common.constants.AGE_MIN
import com.yapp.muckpot.common.enums.Gender
import com.yapp.muckpot.domains.board.entity.Board
import com.yapp.muckpot.domains.board.entity.City
import com.yapp.muckpot.domains.board.entity.Participant
import com.yapp.muckpot.domains.board.entity.Province
import com.yapp.muckpot.domains.user.entity.MuckPotUser
import com.yapp.muckpot.domains.user.enums.JobGroupMain
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import java.time.LocalDateTime
import java.util.*

object Fixture {
    fun createUser(
        id: Long? = null,
        email: String = UUID.randomUUID().toString().substring(0, 5) + "@naver.com",
        password: String = "abcd1234",
        nickName: String = UUID.randomUUID().toString(),
        gender: Gender = Gender.MEN,
        yearOfBirth: Int = 2000,
        mainCategory: JobGroupMain = JobGroupMain.DEVELOPMENT,
        subCategory: String? = "subCategory",
        imageUrl: String? = "image_url"
    ): MuckPotUser {
        return MuckPotUser(
            id,
            email,
            password,
            nickName,
            gender,
            yearOfBirth,
            mainCategory,
            subCategory,
            imageUrl
        )
    }

    fun createBoard(
        id: Long? = null,
        user: MuckPotUser = createUser(),
        title: String = "board_title",
        location: Location = Location("boardLocation", 40.7128, -74.0060),
        locationDetail: String? = null,
        meetingTime: LocalDateTime = LocalDateTime.now().plusMinutes(30),
        content: String? = "content",
        views: Int = 0,
        currentApply: Int = 0,
        maxApply: Int = 2,
        chatLink: String = "chat_link",
        status: MuckPotStatus = MuckPotStatus.IN_PROGRESS,
        minAge: Int = AGE_MIN,
        maxAge: Int = AGE_MAX,
        state: State = State.ACTIVE,
        province: Province = createProvince()
    ): Board {
        return Board(
            id = id,
            user = user,
            title = title,
            location = location,
            locationDetail = locationDetail,
            meetingTime = meetingTime,
            content = content,
            views = views,
            currentApply = currentApply,
            maxApply = maxApply,
            chatLink = chatLink,
            status = status,
            minAge = minAge,
            maxAge = maxAge,
            province = province
        )
    }

    fun createParticipant(
        user: MuckPotUser = createUser(),
        board: Board = createBoard()
    ): Participant {
        return Participant(user, board)
    }

    fun createProvince(
        name: String = "강남구",
        city: City = createCity()
    ): Province {
        return Province(
            name,
            city
        )
    }

    fun createCity(
        name: String = "서울특별시"
    ): City {
        return City(
            name
        )
    }
}

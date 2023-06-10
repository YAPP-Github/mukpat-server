package com.yapp.muckpot.swagger

const val TEST_SAMPLE = """
{
  "id": 1,
  "name": "test",
  "currentTime": "20230515"
}
"""

const val LOGIN_RESPONSE = """
{
    "status": 200,
    "result": {
        "userId": 1,
        "nickName": "nickName"
    }
}
"""

const val EMAIL_AUTH_REQ_RESPONSE = """
{
    "status": 201,
    "result": {
        "verificationCode": "123456"
    }
}
"""

const val EMAIL_AUTH_RESPONSE = """
{
    "status": 204
}
"""

const val MUCKPOT_SAVE_RESPONSE = """
{
  "status": 201,
  "result": {
    "boardId": 18
  }
}
"""

const val MUCKPOT_FIND_ALL = """
{
  "status": 200,
  "result": {
    "list": [
      {
        "boardId": 75,
        "title": "같이 밥묵으실분",
        "status": "모집마감",
        "todayOrTomorrow": "오늘",
        "elapsedTime": "0분 전",
        "meetingTime": "04월 01일 (토) 오후 01:00",
        "meetingPlace": "서울 성북구 안암동5가 104-30 캐치카페 안암",
        "maxApply": 5,
        "currentApply": 1,
        "participants": [
          {
            "userId": 48,
            "nickName": "nickname2"
          }
        ]
      },
    "lastId": 74
  }
}
"""

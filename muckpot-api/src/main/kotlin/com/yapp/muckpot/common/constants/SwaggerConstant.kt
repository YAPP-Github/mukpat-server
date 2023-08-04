package com.yapp.muckpot.common.constants

const val LOGIN_RESPONSE = """
{
    "status": 200,
    "result": {
        "userId": 1,
        "nickName": "nickName"
    }
}
"""

const val NO_BODY_RESPONSE = """
{
    "status": 204
}
"""

const val SIGN_UP_RESPONSE = """
{
    "status": 201,
    "result": {
        "userId": 1,
        "nickName": "nickName"
    }
}
"""

// Board
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

const val MUCKPOT_FIND_BY_ID = """
{
  "status": 200,
  "result": {
    "boardId": 31,
    "prevId": null,
    "nextId": 14,
    "title": "개성주악 먹어여",
    "content": "개성주악 맛있어요",
    "chatLink": "https://open.kakao.com/o/s0U23Rsf",
    "status": "모집마감",
    "meetingDate": "2023년 07월 14일 (금)",
    "meetingTime": "오후 12:30",
    "createDate": "2023년 07월 14일",
    "maxApply": 2,
    "currentApply": 1,
    "minAge": 21,
    "maxAge": 25,
    "locationName": "경기 파주시 탄현면 갈현리 797-1 연리희재",
    "x": 126.713351951771,
    "y": 37.7684106528357,
    "locationDetail": "",
    "views": 208,
    "userAge": null,
    "participants": [
      {
        "userId": 21,
        "nickName": "건빵",
        "jobGroupMain": "개발",
        "writer": true
      }
    ]
  }
}
"""

const val MUCKPOT_JOIN_RESPONSE = """
{
  "status": 201
}
"""

const val MUCKPOT_REGIONS = """
{
  "status": 200,
  "result": {
    "list": [
      {
        "cityId": 2,
        "cityName": "경기도",
        "sumByCity": 5,
        "provinces": [
          {
            "provinceId": 2,
            "provinceName": "용인시 기흥구",
            "sumByProvince": 5
          }
        ]
      },
      {
        "cityId": 3,
        "cityName": "서울특별시",
        "sumByCity": 2,
        "provinces": [
          {
            "provinceId": 3,
            "provinceName": "성북구",
            "sumByProvince": 2
          }
        ]
      }
    ]
  }
}
"""

const val MUCKPOT_FIND_BY_ID_FOR_UPDATE = """
{
  "status": 200,
  "result": {
    "boardId": 1,
    "title": "같이 밥묵으실분",
    "content": "내용 입니다.",
    "chatLink": "https://open.kakao.com/o/gSIkvvHc",
    "meetingDate": "2023-12-21",
    "meetingTime": "오후 01:00",
    "createDate": "2023년 07월 24일",
    "maxApply": 3,
    "minAge": null,
    "maxAge": null,
    "locationName": "서울 성북구 안암동5가 104-30 캐치카페 안암",
    "addressName": null,
    "region_1depth_name": "경기도",
    "region_2depth_name": "용인시 기흥구",
    "x": 127.02970799701643,
    "y": 37.58392327180857,
    "locationDetail": "6층",
    "userAge": null
  }
}
"""

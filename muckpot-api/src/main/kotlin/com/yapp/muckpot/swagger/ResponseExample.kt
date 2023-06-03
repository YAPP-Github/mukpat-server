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

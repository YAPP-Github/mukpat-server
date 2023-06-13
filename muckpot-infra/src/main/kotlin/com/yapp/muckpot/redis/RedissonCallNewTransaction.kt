package com.yapp.muckpot.redis

import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class RedissonCallNewTransaction {
    /**
     * 부모 트랜잭션의 유무와 관계없이 동시성에 대한 처리는 새로운 별도의 트랜잭션으로 동작 보장
     * 트랜잭션의 타임아웃을 락 획득 후 유지 시간인 leaseTime(10)보다 작게 설정
     * (락 leaseTimeOut 발생 전에 트랜잭션 rollback 시키기 위해)
     * ref: https://devnm.tistory.com/37
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 9)
    fun proceed(joinPoint: ProceedingJoinPoint): Any? {
        return joinPoint.proceed()
    }
}

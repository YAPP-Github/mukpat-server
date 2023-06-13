package com.yapp.muckpot.common.redisson

import com.yapp.muckpot.redis.RedissonCallNewTransaction
import mu.KLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import org.springframework.transaction.TransactionTimedOutException

@Aspect
@Component
class DistributedLockAop(
    private val redissonClient: RedissonClient,
    private val redissonCallNewTransaction: RedissonCallNewTransaction
) {

    private val log = KLogging().logger

    @Around("@annotation(com.yapp.muckpot.common.redisson.DistributedLock)")
    fun lock(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val distributedLock = method.getAnnotation(DistributedLock::class.java)
        val baseKey: String = distributedLock.lockName
        val dynamicKey = getDynamicKeyFromMethodArg(signature.parameterNames, joinPoint.args, distributedLock.identifier)
        val rLock = redissonClient.getLock("$baseKey:$dynamicKey")

        try {
            val isPossible = rLock.tryLock(distributedLock.waitTime, distributedLock.leaseTime, distributedLock.timeUnit)
            if (!isPossible) {
                throw Exception("래디슨 락 획득에 실패했습니다.")
            }

            return redissonCallNewTransaction.proceed(joinPoint)
        } catch (e: TransactionTimedOutException) {
            throw e
        } finally {
            try {
                rLock.unlock()
            } catch (e: IllegalMonitorStateException) {
                // 이미 unlock된 상태에서 다시 unlock 시도시 발생, 에러로그로 남김
                log.error(e) { "" }
            }
        }
    }

    fun getDynamicKeyFromMethodArg(
        methodParameterNames: Array<String>,
        args: Array<Any>,
        paramName: String
    ): String {
        for (i in methodParameterNames.indices) {
            if ((methodParameterNames[i] == paramName)) {
                return args[i].toString()
            }
        }
        throw Exception("잘못된 래디슨 키값입니다.")
    }
}

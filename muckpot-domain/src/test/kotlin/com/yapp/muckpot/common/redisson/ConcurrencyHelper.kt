package com.yapp.muckpot.common.redisson

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

class ConcurrencyHelper {
    companion object {
        private const val NUMBER_OF_THREADS = 500
        private const val NUMBER_OF_THREAD_POOL = 500

        fun execute(operation: () -> Any?, successCount: AtomicLong) {
            val executorService = Executors.newFixedThreadPool(NUMBER_OF_THREAD_POOL)
            val latch = CountDownLatch(NUMBER_OF_THREADS)
            for (i in 1..NUMBER_OF_THREADS) {
                executorService.submit {
                    try {
                        operation()
                        successCount.getAndIncrement()
                    } catch (e: Throwable) {
                        Exception(e)
                    } finally {
                        latch.countDown()
                    }
                }
            }
            latch.await()
        }
    }
}

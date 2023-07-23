package com.yapp.muckpot.domains.board.service

import com.yapp.muckpot.domains.board.repository.BoardQuerydslRepository
import com.yapp.muckpot.redis.constants.ALL_KEY
import com.yapp.muckpot.redis.constants.REGIONS_CACHE_NAME
import mu.KLogging
import org.springframework.cache.annotation.CacheEvict
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardScheduler(
    private val boardQuerydslRepository: BoardQuerydslRepository
) {
    private val log = KLogging().logger

    @CacheEvict(value = [REGIONS_CACHE_NAME], key = ALL_KEY)
    @Scheduled(cron = "0 $UPDATE_MINUTES $UPDATE_HOURS * * *")
    @Transactional
    fun updateDoneBoard() {
        boardQuerydslRepository.updateLessThanCurrentTime()
        log.debug { "참여시간 지나간 먹팟 상태 변경" }
    }

    companion object {
        private const val UPDATE_MINUTES = "0,15,30,45"
        private const val UPDATE_HOURS = "10-22,0"
    }
}

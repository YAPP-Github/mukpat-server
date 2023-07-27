package com.yapp.muckpot.domains.board.service

import com.yapp.muckpot.domains.board.entity.City
import com.yapp.muckpot.domains.board.entity.Province
import com.yapp.muckpot.domains.board.repository.CityRepository
import com.yapp.muckpot.domains.board.repository.ProvinceRepository
import com.yapp.muckpot.redis.constants.ALL_KEY
import com.yapp.muckpot.redis.constants.REGIONS_CACHE_NAME
import mu.KLogging
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProvinceService(
    private val cityRepository: CityRepository,
    private val provinceRepository: ProvinceRepository
) {
    private val log = KLogging().logger

    @Transactional
    fun saveProvinceIfNot(cityName: String, provinceName: String): Province {
        val city = cityRepository.findByName(cityName) ?: cityRepository.save(City(cityName))
        val provinces = provinceRepository.findByName(provinceName)
        provinces.forEach { province ->
            if (province.city.name == cityName) {
                return province
            }
        }
        return provinceRepository.save(Province(provinceName, city))
    }

    @CacheEvict(value = [REGIONS_CACHE_NAME], key = ALL_KEY)
    fun clearRegionsAll() {
        log.info { "지역정보 캐시 삭제" }
    }
}

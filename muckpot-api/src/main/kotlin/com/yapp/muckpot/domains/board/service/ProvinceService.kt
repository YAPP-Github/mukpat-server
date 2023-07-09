package com.yapp.muckpot.domains.board.service

import com.yapp.muckpot.domains.board.entity.City
import com.yapp.muckpot.domains.board.entity.Province
import com.yapp.muckpot.domains.board.repository.CityRepository
import com.yapp.muckpot.domains.board.repository.ProvinceRepository
import org.springframework.stereotype.Service

@Service
class ProvinceService(
    private val cityRepository: CityRepository,
    private val provinceRepository: ProvinceRepository
) {
    fun saveProvinceIfNot(cityName: String, provinceName: String): Province {
        val city = cityRepository.findByName(cityName) ?: cityRepository.save(City(cityName))
        return provinceRepository.findByName(provinceName) ?: provinceRepository.save(Province(provinceName, city))
    }
}

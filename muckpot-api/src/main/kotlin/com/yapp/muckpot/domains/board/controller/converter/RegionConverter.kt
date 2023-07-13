package com.yapp.muckpot.domains.board.controller.converter

import com.yapp.muckpot.domains.board.dto.RegionDto
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import com.yapp.muckpot.redis.dto.MuckpotCityResponse
import com.yapp.muckpot.redis.dto.MuckpotProvinceResponse

object RegionConverter {
    fun convertToCityResponse(city: RegionDto.CityDto, provinceDtos: List<RegionDto>): MuckpotCityResponse {
        var cityInProgressCnt = 0
        val provinces = provinceDtos.groupBy { it.province }
            .mapValues { (province, provinceList) ->
                val provinceInProgressCnt = provinceList.count { it.status == MuckPotStatus.IN_PROGRESS }
                cityInProgressCnt += provinceInProgressCnt
                MuckpotProvinceResponse(province.provinceId, province.provinceName, provinceInProgressCnt)
            }
            .values.toList()
        return MuckpotCityResponse(city.cityId, city.cityName, cityInProgressCnt, provinces)
    }
}

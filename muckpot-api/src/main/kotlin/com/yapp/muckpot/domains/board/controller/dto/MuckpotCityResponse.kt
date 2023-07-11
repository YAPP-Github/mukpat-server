package com.yapp.muckpot.domains.board.controller.dto

import com.yapp.muckpot.domains.board.dto.RegionDto
import com.yapp.muckpot.domains.user.enums.MuckPotStatus
import java.io.Serializable

data class MuckpotCityResponse(
    val cityId: Long = 0,
    val cityName: String = "",
    var sumByCity: Int = 0,
    val provinces: List<MuckpotProvinceResponse> = emptyList()
) : Serializable {
    companion object {
        fun of(city: RegionDto.CityDto, provinceDtos: List<RegionDto>): MuckpotCityResponse {
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
}

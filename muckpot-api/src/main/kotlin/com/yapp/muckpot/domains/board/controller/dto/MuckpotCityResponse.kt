package com.yapp.muckpot.domains.board.controller.dto

import com.yapp.muckpot.domains.board.dto.RegionDto
import java.io.Serializable

data class MuckpotCityResponse(
    val cityId: Long = 0,
    val cityName: String = "",
    var sumByCity: Int = 0,
    val provinces: List<MuckpotProvinceResponse> = emptyList()
) : Serializable {
    companion object {
        fun of(city: RegionDto.CityDto, provinceDtos: List<RegionDto>): MuckpotCityResponse {
            val provinces = provinceDtos.groupBy { it.province }
                .mapValues { (province, provinceList) ->
                    MuckpotProvinceResponse(province.provinceId, province.provinceName, provinceList.size)
                }
                .values.toList()
            return MuckpotCityResponse(city.cityId, city.cityName, provinceDtos.size, provinces)
        }
    }
}

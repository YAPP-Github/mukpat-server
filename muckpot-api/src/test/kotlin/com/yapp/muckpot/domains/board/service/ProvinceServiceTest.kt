package com.yapp.muckpot.domains.board.service

import Fixture
import com.yapp.muckpot.domains.board.entity.City
import com.yapp.muckpot.domains.board.entity.Province
import com.yapp.muckpot.domains.board.repository.CityRepository
import com.yapp.muckpot.domains.board.repository.ProvinceRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProvinceServiceTest @Autowired constructor(
    private val provinceService: ProvinceService,
    private val cityRepository: CityRepository,
    private val provinceRepository: ProvinceRepository
) : FunSpec({
    lateinit var province: Province
    lateinit var city: City

    beforeEach {
        city = cityRepository.save(Fixture.createCity())
        province = provinceRepository.save(Fixture.createProvince(city = city))
    }

    afterEach {
        provinceRepository.deleteAll()
        cityRepository.deleteAll()
    }

    test("도시이름이 동일한 province라면 새롭게 생성하지 않는다.") {
        // when
        val actual = provinceService.saveProvinceIfNot(cityName = city.name, provinceName = province.name)

        // then
        actual.name shouldBe province.name
        actual.city.name shouldBe city.name
    }

    test("군/구는 같은데 시/도는 다른경우 새롭게 생성한다.") {
        // given
        val provinceName = "중구"
        val seoul = cityRepository.save(Fixture.createCity(name = "서울"))
        val inCheon = cityRepository.save(Fixture.createCity(name = "인천"))
        province = provinceRepository.save(Fixture.createProvince(city = seoul, name = provinceName))

        // when
        val actual = provinceService.saveProvinceIfNot(cityName = inCheon.name, provinceName = provinceName)

        // then
        actual.name shouldBe provinceName
        actual.city.name shouldBe inCheon.name
    }
})

package com.yapp.muckpot.common

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class Location(
    @Column(name = "location_name")
    var locationName: String,

    // TODO FE 작업 완료 후 nullable 제거
    @Column(name = "address_name")
    var addressName: String? = null,

    @Column(name = "location_point", columnDefinition = "Point")
    val locationPoint: Point
) {
    constructor(
        locationName: String,
        addressName: String? = null,
        x: Double,
        y: Double
    ) : this(
        locationName = locationName,
        addressName = addressName,
        GeometryFactory().createPoint(Coordinate(x, y))
    )

    init {
        require(locationName.isNotBlank()) { "위치 명을 입력해주세요" }
    }
}

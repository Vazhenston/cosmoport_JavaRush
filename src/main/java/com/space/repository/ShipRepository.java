package com.space.repository;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface ShipRepository extends JpaRepository<Ship, Long> {
    @Query("select ship from Ship ship where (ship.name like CONCAT('%',:name,'%') or :name is null) and (ship.planet like CONCAT('%',:planet,'%') or :planet is null) and ((ship.speed>=:minSpeed or :minSpeed is null) and (ship.speed<=:maxSpeed or :maxSpeed is null)) and ((ship.crewSize>=:minCrewSize or :minCrewSize is null) and (ship.crewSize<=:maxCrewSize or :maxCrewSize is null)) and ((ship.rating>=:minRating or :minRating is null) and (ship.rating<=:maxRating or :maxRating is null) and (ship.prodDate>=:minProdDate or :minProdDate is null) and (ship.prodDate<=:maxProdDate or :maxProdDate is null) and (ship.isUsed=:isUsed or :isUsed is null) and (ship.shipType=:shipType or :shipType is null))")
    Page<Ship> findByParameters(@Param("name") String name, @Param("planet") String planet,
                                @Param("minSpeed") Double minSpeed, @Param("maxSpeed") Double maxSpeed,
                                @Param("minCrewSize") Integer minCrewSize, @Param("maxCrewSize") Integer maxCrewSize,
                                @Param("minRating") Double minRating, @Param("maxRating") Double maxRating,
                                @Param("minProdDate") Date minProdDate, @Param("maxProdDate") Date maxProdDate,
                                @Param("isUsed") Boolean isUsed, @Param("shipType")ShipType shipType, Pageable page);

}

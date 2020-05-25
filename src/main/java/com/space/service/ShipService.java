package com.space.service;

import com.space.dto.ShipDto;
import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShipService {
    Ship getById(Long id);
    public Ship create(ShipDto dto);
    Ship update(ShipDto dto, Long id);
    boolean delete(Long id);
    List<Ship> getAll();
    long getCount();
    List<Ship> getAllByParameters(String name, String planet, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                  Integer maxCrewSize, Double minRating, Double maxRating, Long minProdDate, Long maxProdDate,
                                  Boolean isUsed, ShipType shipType, Pageable page);
}

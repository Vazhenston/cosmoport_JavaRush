package com.space.service;

import com.space.dto.ShipDto;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ShipServiceImpl implements ShipService {

    private final ShipRepository shipRepository;

    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public Ship getById(Long id) {
        return shipRepository.findById(id).orElse(null);
    }

    @Override
    public Ship create(ShipDto dto) {
        if (dto.getUsed() == null) {
            dto.setUsed(false);
        }
        Date prodDate = null;
        if (dto.getProdDate() != null) {
            prodDate = new Date(dto.getProdDate());
        }
        Date after = new Date();
        after.setYear(2800 - 1900);
        Date before = new Date();
        before.setYear(3019 - 1900);
        if (StringUtils.isEmpty(dto.getName()) || dto.getPlanet() == null || dto.getShipType() == null || prodDate == null ||
                dto.getSpeed() == null || dto.getCrewSize() == null || dto.getName().length() > 50 || dto.getPlanet().length() > 50 ||
                dto.getSpeed() < 0.01 || dto.getSpeed() > 0.99 || dto.getCrewSize() < 1 || dto.getCrewSize() > 9999 ||
                prodDate.getTime() < 0 || !after.before(prodDate) || !before.after(prodDate)) {
            return null;
        }
        Ship ship = new Ship();
        ship.setName(dto.getName());
        ship.setPlanet(dto.getPlanet());
        ship.setSpeed(dto.getSpeed());
        ship.setCrewSize(dto.getCrewSize());
        ship.setUsed(dto.getUsed());
        ship.setProdDate(prodDate);
        ship.setShipType(dto.getShipType());
        ship.setRating(computeRating(ship));
        return shipRepository.saveAndFlush(ship);
    }

    private double computeRating(Ship ship) {
        return Math.rint((80 * ship.getSpeed() * (ship.getUsed() ? 0.5 : 1)) / (3019 - (ship.getProdDate().getYear() + 1900) + 1) * 100)/100;
    }

    @Override
    public Ship update(ShipDto dto, Long id) {
        Optional<Ship> optionalShip = shipRepository.findById(id);
        if (!optionalShip.isPresent()) {
            return null;
        }
        Ship existingShip = optionalShip.get();
        if (dto.getName() == null && dto.getCrewSize() == null && dto.getSpeed() == null && dto.getPlanet() == null &&
        dto.getShipType() == null && dto.getProdDate() == null && dto.getUsed() == null) {
            return existingShip;
        }
        if (dto.getName() != null) {
            existingShip.setName(dto.getName());
        }
        if (dto.getPlanet() != null) {
            existingShip.setPlanet(dto.getPlanet());
        }
        if (dto.getUsed() != null) {
            existingShip.setUsed(dto.getUsed());
        }
        if (dto.getProdDate() != null) {
            existingShip.setProdDate(new Date(dto.getProdDate()));
        }
        if (dto.getCrewSize() != null) {
            existingShip.setCrewSize(dto.getCrewSize());
        }
        if (dto.getShipType() != null) {
            existingShip.setShipType(dto.getShipType());
        }
        if (dto.getSpeed() != null) {
            existingShip.setSpeed(dto.getSpeed());
        }
        existingShip.setRating(computeRating(existingShip));
        return shipRepository.saveAndFlush(existingShip);
    }

    @Override
    public boolean delete(Long id) {
        try {
            shipRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public List<Ship> getAll() {
        return shipRepository.findAll();
    }

    @Override
    public long getCount() {
        return shipRepository.count();
    }

    @Override
    public List<Ship> getAllByParameters(String name, String planet, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                         Integer maxCrewSize, Double minRating, Double maxRating, Long minProdDate, Long maxProdDate,
                                         Boolean isUsed, ShipType shipType, Pageable page) {
        Date dateAfter = null;
        if (minProdDate != null) {
            dateAfter = new Date(minProdDate);
        }
        Date dateBefore = null;
        if (maxProdDate != null) {
            dateBefore = new Date(maxProdDate);
        }
        return shipRepository.findByParameters(name, planet, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating,
                maxRating, dateAfter, dateBefore, isUsed, shipType, page).getContent();
    }
}

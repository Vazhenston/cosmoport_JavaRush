package com.space.controller;

import com.space.dto.ShipDto;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import java.util.*;

@RestController
@RequestMapping("/rest/ships")
public class ShipController extends HttpServlet {
    private final ShipService shipService;

    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> getShip(@PathVariable("id") Long shipId) {
        if (shipId == null || shipId < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Ship ship = shipService.getById(shipId);
        if (ship == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> createShip(@RequestBody ShipDto dto) {
        Ship ship = shipService.create(dto);
        if (ship != null) {
            return new ResponseEntity<Ship>(ship, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> updateShip(@PathVariable("id") Long id, @RequestBody ShipDto dto) {
        Date prodDate = null;
        if (dto.getProdDate() != null) {
            prodDate = new Date(dto.getProdDate());
        }
        Date after = new Date();
        after.setYear(2800 - 1900);
        Date before = new Date();
        before.setYear(3019 - 1900);
        if (id < 1 || (dto.getName() != null && (dto.getName().isEmpty() || dto.getName().length() > 50)) || (dto.getPlanet() != null && dto.getPlanet().length() > 50) ||
                (dto.getSpeed() != null && (dto.getSpeed() < 0.01 || dto.getSpeed() > 0.99)) || (dto.getCrewSize() != null && (dto.getCrewSize() < 1 || dto.getCrewSize() > 9999)) ||
                (prodDate != null && (prodDate.getTime() < 0 || !after.before(prodDate) || !before.after(prodDate)))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Ship updatedShip = shipService.update(dto, id);
        if (updatedShip == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedShip, HttpStatus.OK);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    private ResponseEntity<Ship> deleteShip(@PathVariable("id") Long id) {
        if (id < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (shipService.delete(id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    private ResponseEntity<List<Ship>> getAllShips(@RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "planet", required = false) String planet,
                                                   @RequestParam(value = "shipType", required = false) ShipType shipType,
                                                   @RequestParam(value = "after", required = false) Long after,
                                                   @RequestParam(value = "before", required = false) Long before,
                                                   @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                                   @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                                   @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                                   @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                                   @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                                   @RequestParam(value = "minRating", required = false) Double minRating,
                                                   @RequestParam(value = "maxRating", required = false) Double maxRating,
                                                   @RequestParam(value = "order", required = false) ShipOrder shipOrder,
                                                   @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                                   @RequestParam(value = "pageSize", defaultValue = "3", required = false) Integer pageSize) {
        if (shipOrder == null) {
            shipOrder = ShipOrder.ID;
        }
        Pageable page = PageRequest.of(pageNumber, pageSize, Sort.by(shipOrder.getFieldName()));
        List<Ship> ships = shipService.getAllByParameters(name, planet, minSpeed, maxSpeed, minCrewSize, maxCrewSize,
                minRating, maxRating, after, before, isUsed, shipType, page);
        return new ResponseEntity<>(ships, HttpStatus.OK);
    }

    @RequestMapping(value = "count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    private ResponseEntity<Integer> getShipsCount(@RequestParam(value = "name", required = false) String name,
                                                  @RequestParam(value = "planet", required = false) String planet,
                                                  @RequestParam(value = "shipType", required = false) ShipType shipType,
                                                  @RequestParam(value = "after", required = false) Long after,
                                                  @RequestParam(value = "before", required = false) Long before,
                                                  @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                                  @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                                  @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                                  @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                                  @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                                  @RequestParam(value = "minRating", required = false) Double minRating,
                                                  @RequestParam(value = "maxRating", required = false) Double maxRating) {
        return new ResponseEntity<>(shipService.getAllByParameters(name, planet, minSpeed, maxSpeed, minCrewSize, maxCrewSize,
                minRating, maxRating, after, before, isUsed, shipType, null).size(), HttpStatus.OK);
    }
}

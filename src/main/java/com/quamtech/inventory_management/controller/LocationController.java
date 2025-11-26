package com.quamtech.inventory_management.controller;

import com.quamtech.inventory_management.entite.Location;
import com.quamtech.inventory_management.payload.request.LocationRequest;
import com.quamtech.inventory_management.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LocationController {
    private final LocationService locationService;

    @PostMapping("/createLocation")
    public ResponseEntity<Location> createLocation(@Valid @RequestBody LocationRequest location) {
        return new ResponseEntity<>(locationService.createEmplacement(location), HttpStatus.CREATED);
    }

    @PutMapping("/updateLocation/{id}")
    public ResponseEntity<Location> updateLocation(@PathVariable String id, @Valid @RequestBody LocationRequest location) {
        return ResponseEntity.ok(locationService.updateLocation(id, location));
    }

    @GetMapping("/getLocationById/{id}")
    public ResponseEntity<Location> getLocation(@PathVariable String id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @GetMapping("/getAllLocations")
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/getLocationsByWarehouse/{warehouseId}")
    public ResponseEntity<List<Location>> getLocationsByWarehouse(@PathVariable String warehouseId) {
        return ResponseEntity.ok(locationService.getLocationsByWarehouse(warehouseId));
    }

    @DeleteMapping("/deleteLocation/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable String id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}

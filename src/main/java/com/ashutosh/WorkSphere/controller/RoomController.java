package com.ashutosh.WorkSphere.controller;

import com.ashutosh.WorkSphere.dto.RoomCreateRequest;
import com.ashutosh.WorkSphere.dto.RoomResponse;
import com.ashutosh.WorkSphere.dto.RoomUpdateRequest;
import com.ashutosh.WorkSphere.enums.RoomStatus;
import com.ashutosh.WorkSphere.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(request));
    }

    @GetMapping("/{roomCode}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<RoomResponse> getRoomByCode(@PathVariable String roomCode){
        return ResponseEntity.ok(roomService.getRoomByCode(roomCode));
    }

    @GetMapping("/building/{buildingId}/floor/{floorNumber}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<RoomResponse>> getRoomsByFloor(@PathVariable Long buildingId, @PathVariable Integer floorNumber) {

        return ResponseEntity.ok(roomService.getRoomsByFloor(buildingId, floorNumber));
    }

    @PatchMapping("/{roomCode}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<RoomResponse> updateRoomDetails(@PathVariable String roomCode, @Valid @RequestBody RoomUpdateRequest request) {
        return ResponseEntity.ok(roomService.updateRoomDetails(roomCode, request));
    }

    @PatchMapping("/{roomCode}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<RoomResponse> updateRoomStatus(@PathVariable String roomCode, @RequestParam RoomStatus status) {
        return ResponseEntity.ok(roomService.updateRoomStatus(roomCode, status));
    }


}

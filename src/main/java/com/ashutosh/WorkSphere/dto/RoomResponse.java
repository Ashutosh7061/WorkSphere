package com.ashutosh.WorkSphere.dto;

import com.ashutosh.WorkSphere.enums.RoomStatus;
import com.ashutosh.WorkSphere.enums.RoomType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {

    private Long id;
    private String roomCode;
    private String roomName;
    private RoomType roomType;
    private Integer capacity;
    private RoomStatus status;
    private Integer floorNumber;
    private String buildingName;
    private String buildingCode;
    private String campusName;
    private String campusCode;
}
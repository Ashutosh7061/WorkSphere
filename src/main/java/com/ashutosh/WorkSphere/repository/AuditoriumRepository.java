package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.Auditorium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditoriumRepository extends JpaRepository<Auditorium, Long> {

    Optional<Auditorium> findByAuditoriumCode(String auditoriumCode);

    boolean existsByAuditoriumCode(String auditoriumCode);

    List<Auditorium> findAllByFloorId(Long floorId);

    List<Auditorium> findAllByFloor_Building_CodeAndFloor_Building_Campus_Code(String buildingCode, String campusCode);

    boolean existsByAuditoriumNameIgnoreCaseAndFloor_Building_Id(String auditoriumName, Long buildingId);

    List<Auditorium> findAllByFloor_Building_Campus_Code(String campusCode);

    List<Auditorium> findAll();

}
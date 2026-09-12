package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.AssetAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetAssignmentRepository extends JpaRepository<AssetAssignment, Long> {
    Optional<AssetAssignment> findByAssetIdAndReturnedDateIsNull(Long assetId);
}

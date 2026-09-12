package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByAssetCode(String assetCode);

    Optional<Asset> findBySerialNumber(String serialNumber);

    boolean existsByAssetCode(String assetCode);
}

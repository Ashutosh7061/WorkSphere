package com.ashutosh.WorkSphere.entity;

import com.ashutosh.WorkSphere.enums.AssetStatus;
import com.ashutosh.WorkSphere.enums.AssetType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "assets",
        indexes = {
                @Index(name = "idx_asset_status", columnList = "status"),
                @Index(name = "idx_asset_type", columnList = "asset_type")

        })
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String assetCode;

    @Column(unique = true)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType assetType;

    private String brand;
    private String model;

    private LocalDate purchaseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}

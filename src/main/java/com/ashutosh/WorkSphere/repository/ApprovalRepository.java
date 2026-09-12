package com.ashutosh.WorkSphere.repository;

import com.ashutosh.WorkSphere.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    List<Approval> findByApproverId(Long approverId);
}
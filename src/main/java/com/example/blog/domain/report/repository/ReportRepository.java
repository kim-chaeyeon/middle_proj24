package com.example.blog.domain.report.repository;

import com.example.blog.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, Long> {

}
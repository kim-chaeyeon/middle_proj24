package com.example.blog.domain.report.service;

import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.report.entity.Report;
import com.example.blog.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    public void reportMember(Member reporter, Member reported, String reason) {
        Report report = new Report();
        report.setReporter(reporter);
        report.setReported(reported);
        report.setReason(reason);
        // set the nicknames
        report.setReporterNickname(reporter.getNickname());
        report.setReportedNickname(reported.getNickname());
        reportRepository.save(report);
    }
}

package com.innocito.testpilot.entity;

import com.innocito.testpilot.enums.ActiveStatus;
import com.innocito.testpilot.enums.ReportStatus;
import com.innocito.testpilot.model.TestSuiteUserReportFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class TestSuiteReportSpecification implements Specification<TestSuiteReport> {

    private TestSuiteUserReportFilter filter;

    public TestSuiteReportSpecification(TestSuiteUserReportFilter filter) {
        super();
        this.filter = filter;
    }

    public Predicate toPredicate(Root<TestSuiteReport> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

        Predicate p = cb.conjunction();

        p.getExpressions().add(cb.equal(root.get("testSuite").get("id"), filter.getTestSuiteId()));

        p.getExpressions().add(cb.equal(root.get("testSuite").get("activeStatus"), ActiveStatus.ACTIVE.getDisplayValue()));

        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            p.getExpressions().add(
                    cb.between(root.get("executionDate"), filter.getStartDate(), filter.getEndDate())
            );
        }

        if (StringUtils.isNotBlank(filter.getStatus()) && !("ALL".equalsIgnoreCase(filter.getStatus()))) {
            p.getExpressions().add(cb.equal(root.get("status"), ReportStatus.getByDisplayValue(filter.getStatus()).name()));
        }

        return p;
    }
}
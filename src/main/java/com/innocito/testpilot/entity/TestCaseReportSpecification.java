package com.innocito.testpilot.entity;

import com.innocito.testpilot.enums.ActiveStatus;
import com.innocito.testpilot.enums.ReportStatus;
import com.innocito.testpilot.model.TestCaseUserReportFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class TestCaseReportSpecification implements Specification<TestCaseReport> {

    private TestCaseUserReportFilter filter;

    public TestCaseReportSpecification(TestCaseUserReportFilter filter) {
        super();
        this.filter = filter;
    }

    public Predicate toPredicate(Root<TestCaseReport> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

        Predicate p = cb.conjunction();

        p.getExpressions().add(cb.equal(root.get("testCase").get("id"), filter.getTestCaseId()));

        p.getExpressions().add(cb.equal(root.get("testCase").get("activeStatus"), ActiveStatus.ACTIVE.getDisplayValue()));

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
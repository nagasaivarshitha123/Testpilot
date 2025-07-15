package com.innocito.testpilot.entity;

import com.innocito.testpilot.enums.ActiveStatus;
import com.innocito.testpilot.model.TestCaseFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class TestCaseSpecification implements Specification<TestCase> {

    private TestCaseFilter filter;

    public TestCaseSpecification(TestCaseFilter filter) {
        super();
        this.filter = filter;
    }

    public Predicate toPredicate(Root<TestCase> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

        Predicate p = cb.conjunction();

        p.getExpressions().add(cb.equal(root.get("projectId"), filter.getProjectId()));

        p.getExpressions().add(cb.equal(root.get("activeStatus"), ActiveStatus.ACTIVE.getDisplayValue()));

        if (StringUtils.isNotBlank(filter.getSearchText())) {
            p.getExpressions().add(cb.or(
                    cb.like(root.get("name"), "%" + filter.getSearchText() + "%"),
                    cb.like(root.get("description"), "%" + filter.getSearchText() + "%")
            ));
        }
        return p;
    }
}
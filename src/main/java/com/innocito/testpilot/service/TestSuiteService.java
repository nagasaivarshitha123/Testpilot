package com.innocito.testpilot.service;

import com.innocito.testpilot.entity.*;
import com.innocito.testpilot.enums.ActiveStatus;
import com.innocito.testpilot.enums.ReportStatus;
import com.innocito.testpilot.exception.EnumNotFoundException;
import com.innocito.testpilot.exception.ResourceNotFoundException;
import com.innocito.testpilot.exception.ValidationException;
import com.innocito.testpilot.model.*;
import com.innocito.testpilot.repository.TestSuiteReportRepository;
import com.innocito.testpilot.repository.TestSuiteRepository;
import com.innocito.testpilot.repository.TestSuiteTestCaseRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestSuiteService {
    private final Logger logger = LogManager.getLogger(TestSuiteService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestSuiteRepository testSuiteRepository;

    @Autowired
    private TestSuiteTestCaseRepository testSuiteTestCaseRepository;

    @Autowired
    private TestSuiteReportRepository testSuiteReportRepository;

    @Value("${page.size}")
    private int defaultPageSize;

    @Value("${report.default.period}")
    private int reportDefaultPeriod;

    @Transactional
    public TestSuiteResponseModel createTestSuite(TestSuiteCreationModel testSuiteCreationModel) {
        validateCreateTestSuite(testSuiteCreationModel);
        Project project = projectService.getProject(testSuiteCreationModel.getProjectId());
        TestSuite testSuite = new TestSuite();
        testSuite.setName(testSuiteCreationModel.getName());
        testSuite.setDescription(testSuiteCreationModel.getDescription());
        testSuite.setProjectId(project.getId());
        testSuite.setActiveStatus(ActiveStatus.ACTIVE.getDisplayValue());
        testSuite.setCreatedBy(userService.getLoggedInUser());
        testSuite.setCreationDate(new Date());
        testSuite = testSuiteRepository.save(testSuite);

        updateTestSuiteTestCases(testSuite, testSuiteCreationModel.getTestCases());

        return prepareTestSuiteResponseModel(testSuite);
    }

    private void validateCreateTestSuite(TestSuiteCreationModel testSuiteCreationModel) {
        Map<String, String> errors = new HashMap<>();
        Project project = projectService.getProject(testSuiteCreationModel.getProjectId());

        if (findTestSuite(testSuiteCreationModel.getProjectId(), testSuiteCreationModel.getName()).isPresent()) {
            errors.put("name", "TestSuite exist with same name under project : " + project.getName());
        }

        if (testSuiteCreationModel.getTestCases() != null && !testSuiteCreationModel.getTestCases().isEmpty()) {
            testSuiteCreationModel.getTestCases().forEach(testCase -> {
                try {
                    testCaseService.findTestCase(testCase.getTestCaseId(), ActiveStatus.ACTIVE.getDisplayValue());
                } catch (ResourceNotFoundException e) {
                    errors.put("testCaseId", e.getMessage());
                }
            });
        } else {
            errors.put("testCases", "provide one or more test cases in test suite");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void validateUpdateTestSuite(TestSuite testSuite, TestSuiteUpdationModel testSuiteUpdationModel) {
        Map<String, String> errors = new HashMap<>();

        if (!testSuite.getName().equalsIgnoreCase(testSuiteUpdationModel.getName())) {
            Project project = projectService.getProject(testSuite.getProjectId());
            if (findTestSuite(testSuite.getProjectId(), testSuiteUpdationModel.getName()).isPresent()) {
                errors.put("name", "TestSuite exist with same name under project : " + project.getName());
            }
        }

        if (testSuiteUpdationModel.getTestCases() != null && !testSuiteUpdationModel.getTestCases().isEmpty()) {
            testSuiteUpdationModel.getTestCases().forEach(testCase -> {
                try {
                    testCaseService.findTestCase(testCase.getTestCaseId(), ActiveStatus.ACTIVE.getDisplayValue());
                } catch (ResourceNotFoundException e) {
                    errors.put("testCaseId", e.getMessage());
                }
            });
        } else {
            errors.put("testCases", "provide one or more test cases in test suite");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public Optional<TestSuite> findTestSuite(long projectId, String name) {
        return testSuiteRepository.findByProjectIdAndName(projectId, name);
    }

    private TestSuiteResponseModel prepareTestSuiteResponseModel(TestSuite testSuite) {
        TestSuiteResponseModel testSuiteResponseModel = modelMapper.map(testSuite, TestSuiteResponseModel.class);
        testSuiteResponseModel.setTestCases(prepareTestSuiteTestCasesResponseModel(
                testSuiteTestCaseRepository.findAllByTestSuiteIdAndActiveStatusAndTestCaseActiveStatus(
                        testSuite.getId(), ActiveStatus.ACTIVE.getDisplayValue()
                        , ActiveStatus.ACTIVE.getDisplayValue())));
        testSuiteResponseModel.setTestCasesCount(
                (int) testSuiteResponseModel.getTestCases().stream().filter(TestSuiteTestCaseResponseModel::isEnabled).count());
        return testSuiteResponseModel;
    }

    private List<TestSuiteTestCaseResponseModel> prepareTestSuiteTestCasesResponseModel(List<TestSuiteTestCase> testCases) {
        List<TestSuiteTestCaseResponseModel> response = new ArrayList<>();
        if (testCases != null && !testCases.isEmpty()) {
            testCases.forEach(testCase -> {
                TestSuiteTestCaseResponseModel model = new TestSuiteTestCaseResponseModel();
                model.setTestCaseId(testCase.getTestCase().getId());
                model.setTestCaseName(testCase.getTestCase().getName());
                model.setTestCaseDescription(testCase.getTestCase().getDescription());
                model.setEnabled(testCase.getEnabled() == 1 ? true : false);
                response.add(model);
            });
        }
        return response;
    }

    public TestSuite findTestSuite(long id, int activeStatus) {
        return testSuiteRepository.findByIdAndActiveStatus(id, activeStatus).orElseThrow(() ->
                new ResourceNotFoundException("id", "TestSuite not found with id : " + id));
    }

    @Transactional
    public TestSuiteResponseModel updateTestSuite(long id, TestSuiteUpdationModel testSuiteUpdationModel) {
        TestSuite testSuite = findTestSuite(id, ActiveStatus.ACTIVE.getDisplayValue());

        validateUpdateTestSuite(testSuite, testSuiteUpdationModel);

        testSuite.setName(testSuiteUpdationModel.getName());
        testSuite.setDescription(testSuiteUpdationModel.getDescription());
        testSuite.setUpdatedBy(userService.getLoggedInUser());
        testSuite.setUpdationDate(new Date());
        testSuite = testSuiteRepository.save(testSuite);

        updateTestSuiteTestCases(testSuite, testSuiteUpdationModel.getTestCases());

        return prepareTestSuiteResponseModel(testSuite);
    }

    private List<TestSuiteTestCaseResponseModel> updateTestSuiteTestCases(TestSuite testSuite, List<TestSuiteTestCaseRequestModel> testCases) {
        List<TestSuiteTestCase> existingTestCases = testSuite.getTestCases();
        if (testCases == null || testCases.isEmpty()) {
            if (existingTestCases != null && !existingTestCases.isEmpty()) {
                existingTestCases.forEach(testCase -> {
                    testCase.setUpdatedBy(userService.getLoggedInUser());
                    testCase.setUpdationDate(new Date());
                    testCase.setActiveStatus(ActiveStatus.INACTIVE.getDisplayValue());
                });
                testSuiteTestCaseRepository.saveAll(existingTestCases);
            }
            return new ArrayList<>();
        } else {
            if (existingTestCases == null) {
                existingTestCases = new ArrayList<>();
            }
            List<Long> existingTestCaseIds = existingTestCases.stream().map(testCase -> testCase.getTestCase().getId())
                    .collect(Collectors.toList());

            List<TestSuiteTestCase> testSuiteTestCaseList = new ArrayList<>();

            List<Long> updatingTestCaseIds = new ArrayList<>();

            testCases.forEach(testCase -> {
                Optional<TestSuiteTestCase> testSuiteTestCaseOptional = findTestSuiteTestCase(
                        testSuite.getId(), testCase.getTestCaseId());
                if (testSuiteTestCaseOptional.isPresent()) {
                    TestSuiteTestCase testSuiteTestCase = testSuiteTestCaseOptional.get();
                    testSuiteTestCase.setEnabled(testCase.isEnabled() ? ActiveStatus.ACTIVE.getDisplayValue() : ActiveStatus.INACTIVE.getDisplayValue());
                    testSuiteTestCase.setActiveStatus(ActiveStatus.ACTIVE.getDisplayValue());
                    testSuiteTestCase.setUpdatedBy(userService.getLoggedInUser());
                    testSuiteTestCase.setUpdationDate(new Date());
                    testSuiteTestCaseList.add(testSuiteTestCase);
                    updatingTestCaseIds.add(testSuiteTestCase.getTestCase().getId());
                } else {
                    TestSuiteTestCase testSuiteTestCase = new TestSuiteTestCase();
                    testSuiteTestCase.setTestSuite(testSuite);
                    testSuiteTestCase.setTestCase(testCaseService.findTestCase(testCase.getTestCaseId(), ActiveStatus.ACTIVE.getDisplayValue()));
                    testSuiteTestCase.setEnabled(testCase.isEnabled() ? ActiveStatus.ACTIVE.getDisplayValue() : ActiveStatus.INACTIVE.getDisplayValue());
                    testSuiteTestCase.setActiveStatus(ActiveStatus.ACTIVE.getDisplayValue());
                    testSuiteTestCase.setCreatedBy(userService.getLoggedInUser());
                    testSuiteTestCase.setCreationDate(new Date());
                    testSuiteTestCaseList.add(testSuiteTestCase);
                }
            });

            testSuiteTestCaseRepository.saveAll(testSuiteTestCaseList);

            // it will give the ids of testcases which are deleted by user
            existingTestCaseIds.removeAll(updatingTestCaseIds);

            if (!existingTestCaseIds.isEmpty()) {
                List<TestSuiteTestCase> removeList = testSuiteTestCaseRepository.findAllByTestSuiteIdAndTestCaseIdIn(
                        testSuite.getId(),
                        existingTestCaseIds);
                removeList.forEach(testCase -> {
                    testCase.setUpdatedBy(userService.getLoggedInUser());
                    testCase.setUpdationDate(new Date());
                    testCase.setActiveStatus(ActiveStatus.INACTIVE.getDisplayValue());
                });
                testSuiteTestCaseRepository.saveAll(removeList);
            }

            List<TestSuiteTestCase> updatedTestSuiteTestCases = testSuiteTestCaseRepository.findAllByTestSuiteIdAndActiveStatusAndTestCaseActiveStatus(
                    testSuite.getId(), ActiveStatus.ACTIVE.getDisplayValue(), ActiveStatus.ACTIVE.getDisplayValue());

            return prepareTestSuiteTestCasesResponseModel(updatedTestSuiteTestCases);
        }
    }

    public Optional<TestSuiteTestCase> findTestSuiteTestCase(long testSuiteId, long testCaseId) {
        return testSuiteTestCaseRepository.findByTestSuiteIdAndTestCaseId(testSuiteId, testCaseId);
    }

    @Transactional
    public void deleteTestSuite(long id) {
        TestSuite testSuite = findTestSuite(id, ActiveStatus.ACTIVE.getDisplayValue());
        testSuite.setUpdatedBy(userService.getLoggedInUser());
        testSuite.setUpdationDate(new Date());
        testSuite.setActiveStatus(ActiveStatus.INACTIVE.getDisplayValue());
        testSuiteRepository.save(testSuite);
    }

    public TestSuiteResponseModel getTestSuite(long id) {
        TestSuite testSuite = findTestSuite(id, ActiveStatus.ACTIVE.getDisplayValue());
        return prepareTestSuiteResponseModel(testSuite);
    }

    public PageResponseModel<List<TestSuiteResponseModel>> getAllTestSuites(long projectId, String searchText,
                                                                            Integer pageNo, Integer pageSize, String sortBy) {
        Project project = projectService.getProject(projectId);

        if (pageNo == null || pageNo < 1) {
            pageNo = 1;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = defaultPageSize;
        }

        long totalElements = 0;
        int totalPages = 0;
        int currentPageSize = 0;

        TestSuiteFilter testSuiteFilter = new TestSuiteFilter();
        testSuiteFilter.setProjectId(projectId);
        testSuiteFilter.setSearchText(searchText);

        Specification<TestSuite> specification = new TestSuiteSpecification(testSuiteFilter);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(createSortOrder(sortBy)));
        Page<TestSuite> testSuites = testSuiteRepository.findAll(specification, pageable);
        totalElements = testSuites.getTotalElements();
        totalPages = testSuites.getTotalPages();
        currentPageSize = testSuites.getNumberOfElements();

        List<TestSuiteResponseModel> testSuiteResponseModels = testSuites.stream()
                .map(testSuite -> prepareTestSuiteResponseModel(testSuite))
                .collect(Collectors.toList());

        PageResponseModel<List<TestSuiteResponseModel>> response = new PageResponseModel<>();
        response.setPageNo(pageNo);
        response.setRequestedPageSize(pageSize);
        response.setTotalPages(totalPages);
        response.setTotalElements(totalElements);
        response.setCurrentPageSize(currentPageSize);
        response.setItems(testSuiteResponseModels);
        return response;
    }

    private List<Sort.Order> createSortOrder(String sortBy) {
        List<Sort.Order> sorts = new ArrayList<>();
        if (StringUtils.isNotBlank(sortBy)) {
            Map<String, String> map = sortPropertiesMap();
            String sortProperty;
            Sort.Direction direction;
            for (String sort : sortBy.split(",")) {
                sortProperty = map.get(sort.split(":")[0]);
                if (StringUtils.isNotBlank(sortProperty)) {
                    String sortDirection = sort.split(":")[1];
                    if (Sort.Direction.ASC.name().equalsIgnoreCase(sortDirection)) {
                        direction = Sort.Direction.ASC;
                    } else {
                        direction = Sort.Direction.DESC;
                    }
                    sorts.add(new Sort.Order(direction, sortProperty));
                }
            }
        }
        return sorts;
    }

    public Map<String, String> sortPropertiesMap() {
        Map<String, String> map = new HashMap<>();
        map.put("id", "id");
        map.put("name", "name");
        map.put("creationDate", "creationDate");
        return map;
    }

    private void validateRunTestSuiteTestCases(List<Long> testCaseIds) {
        Map<String, String> errors = new HashMap<>();

        if (testCaseIds != null && !testCaseIds.isEmpty()) {

            int total = testCaseIds.size();
            long validIdsCount = testCaseIds.stream().filter(testCaseId -> testCaseId != null).count();

            if (total != validIdsCount) {
                errors.put("testCaseId", "testCaseId should not be null");
            }

            testCaseIds.stream().filter(testCaseId -> testCaseId != null).forEach(testCaseId -> {
                try {
                    testCaseService.findTestCase(testCaseId, ActiveStatus.ACTIVE.getDisplayValue());
                } catch (ResourceNotFoundException e) {
                    errors.put("testCaseId", e.getMessage());
                }
            });
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    @Transactional
    public TestSuiteReportResponseModel runTestSuite(long testSuiteId, List<Long> testCaseIds) {
        TestSuite testSuite = findTestSuite(testSuiteId, ActiveStatus.ACTIVE.getDisplayValue());

        validateRunTestSuiteTestCases(testCaseIds);

        List<TestSuiteTestCase> testSuiteTestCaseList;

        if (testCaseIds != null && !testCaseIds.isEmpty()) {
            testSuiteTestCaseList = testSuiteTestCaseRepository.findAllByTestSuiteIdAndTestCaseIdIn(testSuite.getId(), testCaseIds);
        } else {
            testSuiteTestCaseList =
                    testSuiteTestCaseRepository.findAllByTestSuiteIdAndActiveStatusAndEnabledAndTestCaseActiveStatus(
                            testSuite.getId(),
                            ActiveStatus.ACTIVE.getDisplayValue(),
                            ActiveStatus.ACTIVE.getDisplayValue(),
                            ActiveStatus.ACTIVE.getDisplayValue());
        }

        TestSuiteReport testSuiteReport = saveTestSuiteReport(testSuite);

        List<TestCaseReportResponseModel> testCaseReportResponseModelList = new ArrayList<>();

        if (testSuiteTestCaseList.isEmpty()) {
            testSuiteReport.setStatus(ReportStatus.Passed.name());
            testSuiteReport.setTotalTestCases(0);
            testSuiteReport.setPassedTestCases(0);
            testSuiteReport.setFailedTestCases(0);
            testSuiteReport.setEndDate(new Date());
        } else {
            Long testSuiteReportId = testSuiteReport.getId();
            testSuiteTestCaseList.forEach(testSuiteTestCase -> {
                TestCaseReportResponseModel testCaseReportResponseModel =
                        testCaseService.runTestCase(testSuiteTestCase.getTestCase().getId(), testSuiteReportId);
                testCaseReportResponseModelList.add(testCaseReportResponseModel);
            });

            testSuiteReport.setTotalTestCases(testCaseReportResponseModelList.size());

            long passedTestCases = testCaseReportResponseModelList.stream()
                    .filter(item -> item.getStatus().equalsIgnoreCase(ReportStatus.Passed.name())).count();

            long failedTestCases = testCaseReportResponseModelList.stream()
                    .filter(item -> item.getStatus().equalsIgnoreCase(ReportStatus.Failed.name())).count();

            testSuiteReport.setPassedTestCases((int) passedTestCases);
            testSuiteReport.setFailedTestCases((int) failedTestCases);

            ReportStatus status =
                    testSuiteReport.getTotalTestCases() == testSuiteReport.getPassedTestCases() ? ReportStatus.Passed : ReportStatus.Failed;
            testSuiteReport.setStatus(status.name());
            testSuiteReport.setEndDate(new Date());
        }

        testSuiteReport = testSuiteReportRepository.save(testSuiteReport);

        return prepareTestSuiteReportResponseModel(testSuiteReport, testCaseReportResponseModelList);
    }

    public TestSuiteReport saveTestSuiteReport(TestSuite testSuite) {
        TestSuiteReport testSuiteReport = new TestSuiteReport();
        testSuiteReport.setProjectId(testSuite.getProjectId());
        testSuiteReport.setTestSuite(testSuite);
        testSuiteReport.setExecutionDate(new Date());

        testSuiteReport = testSuiteReportRepository.save(testSuiteReport);
        return testSuiteReport;
    }

    private TestSuiteReportResponseModel prepareTestSuiteReportResponseModel(TestSuiteReport testSuiteReport,
                                                                             List<TestCaseReportResponseModel> testCaseReportResponseModelList) {
        TestSuiteReportResponseModel response = new TestSuiteReportResponseModel();
        response.setTestSuiteId(testSuiteReport.getTestSuite().getId());
        response.setTestSuiteRunId(testSuiteReport.getId());
        response.setProjectId(testSuiteReport.getProjectId());
        response.setName(testSuiteReport.getTestSuite().getName());
        response.setDescription(testSuiteReport.getTestSuite().getDescription());
        response.setCreationDate(testSuiteReport.getTestSuite().getCreationDate());
        response.setExecutionDate(testSuiteReport.getExecutionDate());
        response.setEndDate(testSuiteReport.getEndDate());
        long elapsedTime = response.getEndDate().getTime() - response.getExecutionDate().getTime();
        response.setTime(elapsedTime + " ms");
        response.setTotalTestCases(testSuiteReport.getTotalTestCases());
        response.setPassedTestCases(testSuiteReport.getPassedTestCases());
        response.setFailedTestCases(testSuiteReport.getFailedTestCases());
        response.setStatus(testSuiteReport.getStatus());
        if (testCaseReportResponseModelList == null) {
            response.setTestCasesRunDetails(testCaseService.getTestCaseReportResponses(testSuiteReport));
        } else {
            response.setTestCasesRunDetails(testCaseReportResponseModelList);
        }
        return response;
    }

    public PageResponseModel<List<TestSuiteUserReport>> getTestSuiteUserReport(long projectId, String searchText,
                                                                               String startDate, String endDate,
                                                                               String status,
                                                                               Integer pageNo, Integer pageSize) {
        validateTestSuiteUserReportFilter(projectId, startDate, endDate, status);

        if (pageNo == null || pageNo < 1) {
            pageNo = 1;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = defaultPageSize;
        }

        long totalElements = 0;
        int totalPages = 0;
        int currentPageSize = 0;

        Date startDateTime;
        Date endDateTime;

        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            endDateTime = com.innocito.testpilot.util.DateUtils.atEndOfDay(new Date());
            startDateTime = com.innocito.testpilot.util.DateUtils.atStartOfDay(com.innocito.testpilot.util.DateUtils.subtractDays(endDateTime, reportDefaultPeriod));
        } else {
            try {
                startDateTime = com.innocito.testpilot.util.DateUtils.atStartOfDay(DateUtils.parseDate(startDate, "yyyy-MM-dd"));
                endDateTime = com.innocito.testpilot.util.DateUtils.atEndOfDay(DateUtils.parseDate(endDate, "yyyy-MM-dd"));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(createSortOrder(null)));

        String searchTextValue = null;
        if (StringUtils.isNotBlank(searchText)) {
            searchTextValue = "%" + searchText + "%";
        }

        String statusValue = null;
        if (StringUtils.isNotBlank(status) && !("ALL".equalsIgnoreCase(status))) {
            statusValue = ReportStatus.getByDisplayValue(status).name();
        }

        Page<TestSuiteOverallReport> testSuiteOverallReports =
                testSuiteReportRepository.getTestSuiteOverallReport(projectId, searchTextValue, startDateTime, endDateTime,
                        statusValue, pageable);

        List<TestSuiteUserReport> reports = new ArrayList<>();
        if (testSuiteOverallReports.getContent() != null && !testSuiteOverallReports.getContent().isEmpty()) {
            testSuiteOverallReports.getContent().forEach(report -> {
                TestSuiteUserReport userReport = new TestSuiteUserReport();
                userReport.setTestSuiteId(report.getTestSuiteId());
                userReport.setTestSuiteName(report.getTestSuiteName());
                userReport.setLastExecutionDate(report.getLastExecutionDate());
                userReport.setCreationDate(report.getCreationDate());
                userReport.setTotal(report.getTotal());
                userReport.setPassed(report.getPassed());
                userReport.setFailed(report.getFailed());
                userReport.setStatus(ReportStatus.Failed.name());
                if (report.getTotal() == report.getPassed()) {
                    userReport.setStatus(ReportStatus.Passed.name());
                }
                reports.add(userReport);
            });
        }

        totalElements = testSuiteOverallReports.getTotalElements();
        totalPages = testSuiteOverallReports.getTotalPages();
        currentPageSize = testSuiteOverallReports.getNumberOfElements();

        PageResponseModel<List<TestSuiteUserReport>> response = new PageResponseModel<>();
        response.setPageNo(pageNo);
        response.setRequestedPageSize(pageSize);
        response.setTotalPages(totalPages);
        response.setTotalElements(totalElements);
        response.setCurrentPageSize(currentPageSize);
        response.setItems(reports);
        return response;
    }

    public PageResponseModel<List<TestSuiteReportResponseModel>> getTestSuiteUserReportDetails(long testSuiteId,
                                                                                               String startDate, String endDate,
                                                                                               String status,
                                                                                               Integer pageNo, Integer pageSize) {
        validateTestSuiteUserReportDetailsFilter(testSuiteId, startDate, endDate, status);

        if (pageNo == null || pageNo < 1) {
            pageNo = 1;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = defaultPageSize;
        }

        long totalElements = 0;
        int totalPages = 0;
        int currentPageSize = 0;

        Date startDateTime;
        Date endDateTime;

        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            endDateTime = com.innocito.testpilot.util.DateUtils.atEndOfDay(new Date());
            startDateTime = com.innocito.testpilot.util.DateUtils.atStartOfDay(com.innocito.testpilot.util.DateUtils.subtractDays(endDateTime, reportDefaultPeriod));
        } else {
            try {
                startDateTime = com.innocito.testpilot.util.DateUtils.atStartOfDay(DateUtils.parseDate(startDate, "yyyy-MM-dd"));
                endDateTime = com.innocito.testpilot.util.DateUtils.atEndOfDay(DateUtils.parseDate(endDate, "yyyy-MM-dd"));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        TestSuiteUserReportFilter filter = new TestSuiteUserReportFilter();
        filter.setTestSuiteId(testSuiteId);
        filter.setStartDate(startDateTime);
        filter.setEndDate(endDateTime);
        filter.setStatus(status);

        Specification<TestSuiteReport> specification = new TestSuiteReportSpecification(filter);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(createSortOrder(null)));
        Page<TestSuiteReport> testSuiteReports = testSuiteReportRepository.findAll(specification, pageable);
        totalElements = testSuiteReports.getTotalElements();
        totalPages = testSuiteReports.getTotalPages();
        currentPageSize = testSuiteReports.getNumberOfElements();

        List<TestSuiteReportResponseModel> testSuiteReportResponseModels = testSuiteReports.stream()
                .map(testSuiteReport -> prepareTestSuiteReportResponseModel(testSuiteReport, null))
                .collect(Collectors.toList());

        PageResponseModel<List<TestSuiteReportResponseModel>> response = new PageResponseModel<>();
        response.setPageNo(pageNo);
        response.setRequestedPageSize(pageSize);
        response.setTotalPages(totalPages);
        response.setTotalElements(totalElements);
        response.setCurrentPageSize(currentPageSize);
        response.setItems(testSuiteReportResponseModels);
        return response;
    }

    private void validateTestSuiteUserReportFilter(long projectId, String startDate, String endDate, String status) {
        Map<String, String> errors = new HashMap<>();
        Project project = projectService.getProject(projectId);

        Date startDateTime = null;
        if (StringUtils.isNotBlank(startDate)) {
            try {
                startDateTime = DateUtils.parseDate(startDate, "yyyy-MM-dd");
            } catch (ParseException e) {
                errors.put("startDate", "Expected date format is : yyyy-MM-dd");
            }
        }

        Date endDateTime = null;
        if (StringUtils.isNotBlank(endDate)) {
            try {
                endDateTime = DateUtils.parseDate(endDate, "yyyy-MM-dd");
            } catch (ParseException e) {
                errors.put("endDate", "Expected date format is : yyyy-MM-dd");
            }
        }

        if (startDateTime != null && endDateTime != null) {
            if (!startDateTime.before(endDateTime) && !startDateTime.equals(endDateTime)) {
                errors.put("startDate", "startDate should be earlier than endDate");
            }
        }

        if (StringUtils.isNotBlank(status) && !("ALL".equalsIgnoreCase(status))) {
            try {
                ReportStatus.getByDisplayValue(status);
            } catch (EnumNotFoundException e) {
                errors.put("status", e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void validateTestSuiteUserReportDetailsFilter(long testSuiteId, String startDate, String endDate, String status) {
        Map<String, String> errors = new HashMap<>();

        try {
            TestSuite testSuite = findTestSuite(testSuiteId, ActiveStatus.ACTIVE.getDisplayValue());
        } catch (ResourceNotFoundException e) {
            errors.put("testSuiteId", e.getMessage());
        }

        Date startDateTime = null;
        if (StringUtils.isNotBlank(startDate)) {
            try {
                startDateTime = DateUtils.parseDate(startDate, "yyyy-MM-dd");
            } catch (ParseException e) {
                errors.put("startDate", "Expected date format is : yyyy-MM-dd");
            }
        }

        Date endDateTime = null;
        if (StringUtils.isNotBlank(endDate)) {
            try {
                endDateTime = DateUtils.parseDate(endDate, "yyyy-MM-dd");
            } catch (ParseException e) {
                errors.put("endDate", "Expected date format is : yyyy-MM-dd");
            }
        }

        if (startDateTime != null && endDateTime != null) {
            if (!startDateTime.before(endDateTime) && !startDateTime.equals(endDateTime)) {
                errors.put("startDate", "startDate should be earlier than endDate");
            }
        }

        if (StringUtils.isNotBlank(status) && !("ALL".equalsIgnoreCase(status))) {
            try {
                ReportStatus.getByDisplayValue(status);
            } catch (EnumNotFoundException e) {
                errors.put("status", e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
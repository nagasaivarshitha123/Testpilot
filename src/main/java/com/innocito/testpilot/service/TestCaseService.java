package com.innocito.testpilot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innocito.testpilot.entity.*;
import com.innocito.testpilot.enums.*;
import com.innocito.testpilot.exception.EnumNotFoundException;
import com.innocito.testpilot.exception.ResourceNotFoundException;
import com.innocito.testpilot.exception.ValidationException;
import com.innocito.testpilot.model.*;
import com.innocito.testpilot.repository.*;
import com.jayway.jsonpath.JsonPath;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestCaseService {
    private final Logger logger = LogManager.getLogger(TestCaseService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private TestCaseAssertionRepository testCaseAssertionRepository;

    @Autowired
    private TestCaseAssertionResponseRepository testCaseAssertionResponseRepository;

    @Autowired
    private TestCaseReportRepository testCaseReportRepository;

    @Autowired
    private TestSuiteReportRepository testSuiteReportRepository;

    @Autowired
    private TestSuiteTestCaseRepository testSuiteTestCaseRepository;

    @Autowired
    private RequestService requestService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Validator validator;

    @Autowired
    private UserService userService;

    @Value("${page.size}")
    private int defaultPageSize;

    @Value("${report.default.period}")
    private int reportDefaultPeriod;

    @Transactional
    public TestCaseResponseModel createTestCase(TestCaseCreationModel testCaseCreationModel) {
        validateCreateTestCase(testCaseCreationModel);
        Project project = projectService.getProject(testCaseCreationModel.getProjectId());
        Request request = requestService.findRequest(testCaseCreationModel.getRequestId(), ActiveStatus.ACTIVE.getDisplayValue());
        TestCase testCase = new TestCase();
        testCase.setName(testCaseCreationModel.getName());
        testCase.setDescription(testCaseCreationModel.getDescription());
        testCase.setProjectId(project.getId());
        testCase.setRequest(request);
        testCase.setEndpointUrl(request.getEndpointUrl());
        testCase.setMethod(request.getMethod());
        testCase.setAuthorization(request.getAuthorization());
        testCase.setHeaders(request.getHeaders());
        testCase.setBody(request.getBody());
        testCase.setActiveStatus(ActiveStatus.ACTIVE.getDisplayValue());
        testCase.setCreatedBy(userService.getLoggedInUser());
        testCase.setCreationDate(new Date());
        testCase = testCaseRepository.save(testCase);

        return prepareTestCaseResponseModel(testCase);
    }

    private void validateCreateTestCase(TestCaseCreationModel testCaseCreationModel) {
        Map<String, String> errors = new HashMap<>();
        Project project = projectService.getProject(testCaseCreationModel.getProjectId());
        if (findTestCase(testCaseCreationModel.getProjectId(), testCaseCreationModel.getName()).isPresent()) {
            errors.put("name", "TestCase exist with same name under project : " + project.getName());
        }

        try {
            requestService.findRequest(testCaseCreationModel.getRequestId(), ActiveStatus.ACTIVE.getDisplayValue());
        } catch (ResourceNotFoundException e) {
            errors.put("requestId", e.getMessage());
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void validateUpdateTestCase(TestCase testCase, TestCaseUpdationModel testCaseUpdationModel) {
        Set<ConstraintViolation<TestCaseUpdationModel>> constraintViolation = validator.validate(testCaseUpdationModel);
        if (!constraintViolation.isEmpty()) {
            throw new ConstraintViolationException(constraintViolation);
        }

        Map<String, String> errors = new HashMap<>();

        if (!testCase.getName().equalsIgnoreCase(testCaseUpdationModel.getName())) {
            Project project = projectService.getProject(testCase.getProjectId());
            if (findTestCase(testCase.getProjectId(), testCaseUpdationModel.getName()).isPresent()) {
                errors.put("name", "TestCase exist with same name under project : " + project.getName());
            }
        }

        try {
            HttpMethod.getByDisplayValue(testCaseUpdationModel.getMethod());
        } catch (EnumNotFoundException e) {
            errors.put("method", e.getMessage());
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public Optional<TestCase> findTestCase(long projectId, String name) {
        return testCaseRepository.findByProjectIdAndName(projectId, name);
    }

    private TestCaseResponseModel prepareTestCaseResponseModel(TestCase testCase) {
        TestCaseResponseModel testCaseResponseModel = modelMapper.map(testCase, TestCaseResponseModel.class);
        testCaseResponseModel.setRequestId(testCase.getRequest().getId());
        testCaseResponseModel.setRequestName(testCase.getRequest().getName());
        testCaseResponseModel.setRepositoryName(testCase.getRequest().getApiRepository().getName());
        testCaseResponseModel.setAssertions(prepareTestCaseAssertionOutputModel(
                testCaseAssertionRepository.findAllByTestCaseIdAndActiveStatus(
                        testCase.getId(), ActiveStatus.ACTIVE.getDisplayValue()
                        , Sort.by(Sort.Direction.DESC, "id"))));
        testCaseResponseModel.setTrend(Arrays.asList("todo"));
        return testCaseResponseModel;
    }

    private List<TestCaseAssertionModel> prepareTestCaseAssertionModel(List<TestCaseAssertion> testCaseAssertions) {
        List<TestCaseAssertionModel> assertions;
        if (testCaseAssertions != null && !testCaseAssertions.isEmpty()) {
            assertions = Arrays.asList(modelMapper.map(testCaseAssertions, TestCaseAssertionModel[].class));
            assertions.forEach(assertion -> {
                assertion.setAssertionType(AssertionType.valueOf(assertion.getAssertionType()).getDisplayValue());
                assertion.setComparison(AssertionOperation.valueOf(assertion.getComparison()).getDisplayValue());
            });
        } else {
            assertions = new ArrayList<>();
        }
        return assertions;
    }

    private List<TestCaseAssertionOutputModel> prepareTestCaseAssertionOutputModel(List<TestCaseAssertion> testCaseAssertions) {
        List<TestCaseAssertionOutputModel> assertions;
        if (testCaseAssertions != null && !testCaseAssertions.isEmpty()) {
            assertions = Arrays.asList(modelMapper.map(testCaseAssertions, TestCaseAssertionOutputModel[].class));
            assertions.forEach(assertion -> {
                assertion.setAssertionType(AssertionType.valueOf(assertion.getAssertionType()).getDisplayValue());
                assertion.setComparison(AssertionOperation.valueOf(assertion.getComparison()).getDisplayValue());
            });
        } else {
            assertions = new ArrayList<>();
        }
        return assertions;
    }

    private List<TestCaseAssertionResponseModel> prepareTestCaseAssertionResponseModel(List<TestCaseAssertionResponse> testCaseAssertionResponses) {
        List<TestCaseAssertionResponseModel> assertions;
        if (testCaseAssertionResponses != null && !testCaseAssertionResponses.isEmpty()) {
            assertions = Arrays.asList(modelMapper.map(testCaseAssertionResponses, TestCaseAssertionResponseModel[].class));
            assertions.forEach(assertion -> {
                assertion.setAssertionType(AssertionType.valueOf(assertion.getAssertionType()).getDisplayValue());
                assertion.setComparison(AssertionOperation.valueOf(assertion.getComparison()).getDisplayValue());
            });
        } else {
            assertions = new ArrayList<>();
        }
        return assertions;
    }

    public TestCase findTestCase(long id, int activeStatus) {
        return testCaseRepository.findByIdAndActiveStatus(id, activeStatus).orElseThrow(() ->
                new ResourceNotFoundException("id", "TestCase not found with id : " + id));
    }

    @Transactional
    public TestCaseTestResponseModel updateAndRunTestCase(long id, TestCaseUpdationModel testCaseUpdationModel) {
        TestCaseResponseModel testCaseResponseModel = updateTestCase(id, testCaseUpdationModel);
        TestCaseReportResponseModel testCaseReportResponseModel = runTestCase(id, null);
        TestCaseTestResponseModel responseModel = modelMapper.map(testCaseReportResponseModel, TestCaseTestResponseModel.class);
        return responseModel;
    }

    @Transactional
    public TestCaseResponseModel updateTestCase(long id, TestCaseUpdationModel testCaseUpdationModel) {
        TestCase testCase = findTestCase(id, ActiveStatus.ACTIVE.getDisplayValue());

        validateUpdateTestCase(testCase, testCaseUpdationModel);

        testCase.setName(testCaseUpdationModel.getName());
        testCase.setDescription(testCaseUpdationModel.getDescription());
        testCase.setEndpointUrl(testCaseUpdationModel.getEndpointUrl());
        testCase.setMethod(testCaseUpdationModel.getMethod());
        testCase.setUpdatedBy(userService.getLoggedInUser());
        testCase.setUpdationDate(new Date());
        testCase = testCaseRepository.save(testCase);

        TestCaseResponseModel testCaseResponseModel = prepareTestCaseResponseModel(testCase);
        return testCaseResponseModel;
    }

    private List<TestCaseAssertionOutputModel> updateTestCaseAssertions(TestCase testCase, List<TestCaseAssertionModel> requestedAssertions) {
        List<TestCaseAssertion> existingAssertions = testCase.getAssertions();
        if (requestedAssertions == null || requestedAssertions.isEmpty()) {
            if (existingAssertions != null && !existingAssertions.isEmpty()) {
                existingAssertions.forEach(assertion -> {
                    assertion.setUpdatedBy(userService.getLoggedInUser());
                    assertion.setUpdationDate(new Date());
                    assertion.setActiveStatus(ActiveStatus.INACTIVE.getDisplayValue());
                });
                testCaseAssertionRepository.saveAll(existingAssertions);
            }
            return new ArrayList<>();
        } else {
            List<Long> existingAssertionIds = existingAssertions.stream().map(testCaseAssertion -> testCaseAssertion.getId())
                    .collect(Collectors.toList());

            List<TestCaseAssertion> assertionList = new ArrayList<>();

            List<Long> updatingAssertionIds = new ArrayList<>();

            requestedAssertions.forEach(testCaseAssertionModel -> {
                if (testCaseAssertionModel.getId() == null) {
                    TestCaseAssertion testCaseAssertion = modelMapper.map(testCaseAssertionModel, TestCaseAssertion.class);
                    testCaseAssertion.setTestCase(testCase);
                    testCaseAssertion.setAssertionType(AssertionType.getByDisplayValue(testCaseAssertionModel.getAssertionType()).name());
                    testCaseAssertion.setComparison(AssertionOperation.getByDisplayValue(testCaseAssertionModel.getComparison()).name());
                    testCaseAssertion.setActiveStatus(ActiveStatus.ACTIVE.getDisplayValue());
                    testCaseAssertion.setCreatedBy(userService.getLoggedInUser());
                    testCaseAssertion.setCreationDate(new Date());
                    assertionList.add(testCaseAssertion);
                } else {
                    TestCaseAssertion testCaseAssertion = findTestCaseAssertion(testCaseAssertionModel.getId(), ActiveStatus.ACTIVE.getDisplayValue());
                    testCaseAssertion.setAssertionType(AssertionType.getByDisplayValue(testCaseAssertionModel.getAssertionType()).name());
                    testCaseAssertion.setPath(testCaseAssertionModel.getPath());
                    testCaseAssertion.setComparison(AssertionOperation.getByDisplayValue(testCaseAssertionModel.getComparison()).name());
                    testCaseAssertion.setValue(testCaseAssertionModel.getValue());
                    testCaseAssertion.setUpdatedBy(userService.getLoggedInUser());
                    testCaseAssertion.setUpdationDate(new Date());
                    assertionList.add(testCaseAssertion);
                    updatingAssertionIds.add(testCaseAssertion.getId());
                }
            });

            testCaseAssertionRepository.saveAll(assertionList);

            // it will give the ids of assertions which are deleted by user
            existingAssertionIds.removeAll(updatingAssertionIds);

            if (!existingAssertionIds.isEmpty()) {
                List<TestCaseAssertion> removeList = testCaseAssertionRepository.findAllById(existingAssertionIds);
                removeList.forEach(assertion -> {
                    assertion.setUpdatedBy(userService.getLoggedInUser());
                    assertion.setUpdationDate(new Date());
                    assertion.setActiveStatus(ActiveStatus.INACTIVE.getDisplayValue());
                });
                testCaseAssertionRepository.saveAll(removeList);
            }

            List<TestCaseAssertion> updatedAssertions = testCaseAssertionRepository.findAllByTestCaseIdAndActiveStatus(
                    testCase.getId(), ActiveStatus.ACTIVE.getDisplayValue()
                    , Sort.by(Sort.Direction.DESC, "id"));

            return prepareTestCaseAssertionOutputModel(updatedAssertions);
        }
    }

    public TestCaseAssertion findTestCaseAssertion(long id, int activeStatus) {
        return testCaseAssertionRepository.findByIdAndActiveStatus(id, activeStatus).orElseThrow(() ->
                new ResourceNotFoundException("id", "TestCaseAssertion not found with id : " + id));
    }

    private Map<String, String> validateTestCaseAssertions(List<TestCaseAssertionModel> assertions) {
        Map<String, String> errors = new HashMap<>();

        if (assertions != null && !assertions.isEmpty()) {
            assertions.forEach(assertion -> {
                try {
                    AssertionType.getByDisplayValue(assertion.getAssertionType());
                } catch (EnumNotFoundException e) {
                    errors.put("assertionType", e.getMessage());
                }

                try {
                    AssertionOperation.getByDisplayValue(assertion.getComparison());
                } catch (EnumNotFoundException e) {
                    errors.put("comparison", e.getMessage());
                }

                if (assertion.getId() != null) {
                    try {
                        findTestCaseAssertion(assertion.getId(), ActiveStatus.ACTIVE.getDisplayValue());
                    } catch (ResourceNotFoundException e) {
                        errors.put("id", e.getMessage());
                    }
                }
            });
        }
        return errors;
    }

    @Transactional
    public void deleteTestCase(long id) {
        TestCase testCase = findTestCase(id, ActiveStatus.ACTIVE.getDisplayValue());

        boolean exists = testSuiteTestCaseRepository.existsByTestCaseIdAndActiveStatusAndTestSuiteActiveStatus(id, ActiveStatus.ACTIVE.getDisplayValue(), ActiveStatus.ACTIVE.getDisplayValue());

        if (exists) {
            Map<String, String> errors = new HashMap<>();
            errors.put("testSuite", "test case already exists in a test suite");
            throw new ValidationException(errors);
        }

        testCase.setUpdatedBy(userService.getLoggedInUser());
        testCase.setUpdationDate(new Date());
        testCase.setActiveStatus(ActiveStatus.INACTIVE.getDisplayValue());
        testCaseRepository.save(testCase);
    }

    public TestCaseResponseModel getTestCase(long id) {
        TestCase testCase = findTestCase(id, ActiveStatus.ACTIVE.getDisplayValue());
        return prepareTestCaseResponseModel(testCase);
    }

    public PageResponseModel<List<TestCaseResponseModel>> getAllTestCases(long projectId, String searchText,
                                                                          Integer pageNo, Integer pageSize,
                                                                          String sortBy, boolean fetchAll) {
        Project project = projectService.getProject(projectId);

        if (fetchAll) {
            TestCaseFilter testCaseFilter = new TestCaseFilter();
            testCaseFilter.setProjectId(projectId);

            Specification<TestCase> specification = new TestCaseSpecification(testCaseFilter);
            List<TestCase> testCases = testCaseRepository.findAll(specification);

            List<TestCaseResponseModel> testCaseResponseModels = testCases.stream().map(testCase -> prepareTestCaseResponseModel(testCase)).collect(Collectors.toList());

            PageResponseModel<List<TestCaseResponseModel>> response = new PageResponseModel<>();
            response.setTotalElements(testCases.size());
            response.setItems(testCaseResponseModels);
            return response;
        } else {
            if (pageNo == null || pageNo < 1) {
                pageNo = 1;
            }

            if (pageSize == null || pageSize < 1) {
                pageSize = defaultPageSize;
            }

            long totalElements = 0;
            int totalPages = 0;
            int currentPageSize = 0;

            TestCaseFilter testCaseFilter = new TestCaseFilter();
            testCaseFilter.setProjectId(projectId);
            testCaseFilter.setSearchText(searchText);

            Specification<TestCase> specification = new TestCaseSpecification(testCaseFilter);
            Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(createSortOrder(sortBy)));
            Page<TestCase> testCases = testCaseRepository.findAll(specification, pageable);
            totalElements = testCases.getTotalElements();
            totalPages = testCases.getTotalPages();
            currentPageSize = testCases.getNumberOfElements();

            List<TestCaseResponseModel> testCaseResponseModels = testCases.stream().map(testCase -> prepareTestCaseResponseModel(testCase)).collect(Collectors.toList());

            PageResponseModel<List<TestCaseResponseModel>> response = new PageResponseModel<>();
            response.setPageNo(pageNo);
            response.setRequestedPageSize(pageSize);
            response.setTotalPages(totalPages);
            response.setTotalElements(totalElements);
            response.setCurrentPageSize(currentPageSize);
            response.setItems(testCaseResponseModels);
            return response;
        }
    }

    public TestCaseTestResponseModel runTestCase(TestCaseTestRequestModel testCaseTestRequestModel) {
        Map<String, String> errors = validateTestCaseAssertions(testCaseTestRequestModel.getRequestAssertions());
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        TestCaseTestResponseModel testCaseTestResponseModel = new TestCaseTestResponseModel();
        HttpHeaders headers = new HttpHeaders();
        if (testCaseTestRequestModel.getRequestHeaders() != null && !testCaseTestRequestModel.getRequestHeaders().isEmpty()) {
            testCaseTestRequestModel.getRequestHeaders().entrySet().forEach(item -> {
                headers.add(item.getKey(), item.getValue());
            });
        }

        HttpEntity<Object> entity = new HttpEntity<>(testCaseTestRequestModel.getRequestBody(), headers);

        boolean success;
        int status;
        long size = 0;

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(testCaseTestRequestModel.getEndpointUrl(),
                    org.springframework.http.HttpMethod.valueOf(
                            HttpMethod.getByDisplayValue(testCaseTestRequestModel.getMethod()).name()),
                    entity, String.class);

            success = true;
            status = responseEntity.getStatusCodeValue();
            if (responseEntity.getBody() != null) {
                size = responseEntity.getBody().getBytes().length;
            }

            testCaseTestResponseModel.setResponseHeaders(responseEntity.getHeaders().toSingleValueMap());
            testCaseTestResponseModel.setResponseBody(responseEntity.getBody());
        } catch (RestClientResponseException e) {
            success = false;
            status = e.getRawStatusCode();
            String errorMessage = StringUtils.isNotBlank(e.getResponseBodyAsString()) ? e.getResponseBodyAsString() : e.getMessage();
            size = errorMessage.getBytes().length;

            testCaseTestResponseModel.setErrorMessage(errorMessage);
            testCaseTestResponseModel.setResponseHeaders(e.getResponseHeaders().toSingleValueMap());
        } catch (RestClientException e) {
            success = false;
            status = 0;
            String errorMessage = e.getMessage();
            size = errorMessage.getBytes().length;
            testCaseTestResponseModel.setErrorMessage(errorMessage);
        }

        String contentType = testCaseTestResponseModel.getResponseHeaders() != null ?
                testCaseTestResponseModel.getResponseHeaders().get("Content-Type") : null;

        TestCaseAssertionsExecutionResponse testCaseAssertionsExecutionResponse =
                executeAssertions(testCaseTestRequestModel.getRequestAssertions(),
                        contentType, status, testCaseTestResponseModel.getResponseBody());

        testCaseTestResponseModel.setResponseAssertions(testCaseAssertionsExecutionResponse.getResponseAssertions());
        testCaseTestResponseModel.setTotalAssertions(testCaseAssertionsExecutionResponse.getTotalAssertions());
        testCaseTestResponseModel.setPassedAssertions(testCaseAssertionsExecutionResponse.getPassedAssertions());
        testCaseTestResponseModel.setFailedAssertions(testCaseAssertionsExecutionResponse.getFailedAssertions());
        testCaseTestResponseModel.setStatus(testCaseAssertionsExecutionResponse.getStatus());

        testCaseTestResponseModel.setSuccess(success);
        testCaseTestResponseModel.setEndpointUrl(testCaseTestRequestModel.getEndpointUrl());
        testCaseTestResponseModel.setMethod(testCaseTestRequestModel.getMethod());
        testCaseTestResponseModel.setRequestAuthorization(testCaseTestRequestModel.getRequestAuthorization());
        testCaseTestResponseModel.setRequestHeaders(testCaseTestRequestModel.getRequestHeaders());
        testCaseTestResponseModel.setRequestBody(testCaseTestRequestModel.getRequestBody());
        testCaseTestResponseModel.setRequestAssertions(testCaseTestRequestModel.getRequestAssertions());

        Map<String, String> responseMetadata = new HashMap<>();
        responseMetadata.put("time", "");
        responseMetadata.put("status", status + "");
        responseMetadata.put("size", size + " bytes");

        testCaseTestResponseModel.setResponseMetadata(responseMetadata);

        return testCaseTestResponseModel;
    }

    private TestCaseAssertionsExecutionResponse executeAssertions(List<TestCaseAssertionModel> assertions, String contentType,
                                                                  int statusCode, String body) {
        TestCaseAssertionsExecutionResponse result = new TestCaseAssertionsExecutionResponse();
        result.setTotalAssertions(assertions.size());
        List<TestCaseAssertionResponseModel> responseAssertions = new ArrayList<>();
        result.setResponseAssertions(responseAssertions);
        if (assertions != null && !assertions.isEmpty()) {
            assertions.forEach(assertion -> {
                TestCaseAssertionResponseModel testCaseAssertionResponseModel = modelMapper.map(assertion, TestCaseAssertionResponseModel.class);
                testCaseAssertionResponseModel.setStatus(ReportStatus.Failed.name());
                testCaseAssertionResponseModel.setActualValue("-");
                responseAssertions.add(testCaseAssertionResponseModel);
                try {
                    if (AssertionType.Status_Code.getDisplayValue().equalsIgnoreCase(assertion.getAssertionType())) {
                        testCaseAssertionResponseModel.setActualValue(statusCode + "");
                        if (AssertionOperation.Equals.getDisplayValue().equalsIgnoreCase(assertion.getComparison())) {
                            if (statusCode == Integer.parseInt(assertion.getValue())) {
                                testCaseAssertionResponseModel.setStatus(ReportStatus.Passed.name());
                            }
                        } else if (AssertionOperation.Not_Equals.getDisplayValue().equalsIgnoreCase(assertion.getComparison())) {
                            if (statusCode != Integer.parseInt(assertion.getValue())) {
                                testCaseAssertionResponseModel.setStatus(ReportStatus.Passed.name());
                            }
                        } else if (AssertionOperation.Less_than.getDisplayValue().equalsIgnoreCase(assertion.getComparison())) {
                            if (statusCode < Integer.parseInt(assertion.getValue())) {
                                testCaseAssertionResponseModel.setStatus(ReportStatus.Passed.name());
                            }
                        } else if (AssertionOperation.Greater_than.getDisplayValue().equalsIgnoreCase(assertion.getComparison())) {
                            if (statusCode > Integer.parseInt(assertion.getValue())) {
                                testCaseAssertionResponseModel.setStatus(ReportStatus.Passed.name());
                            }
                        }
                    } else if (AssertionType.JSON_Path.getDisplayValue().equalsIgnoreCase(assertion.getAssertionType())) {
                        if (StringUtils.isNotBlank(contentType)) {
                            if (contentType.contains(MimeTypeUtils.APPLICATION_JSON_VALUE)) {
                                String json = body;
                                String jsonPathExpression = assertion.getPath();
                                Object resultObj = JsonPath.parse(json).read(jsonPathExpression);
                                String resultString = resultObj == null ? "null" : resultObj.toString();
                                testCaseAssertionResponseModel.setActualValue(resultString);
                                if (AssertionOperation.Equals.getDisplayValue().equalsIgnoreCase(assertion.getComparison())) {
                                    String parsedValue = resultString;
                                    if (parsedValue.equalsIgnoreCase(assertion.getValue())) {
                                        testCaseAssertionResponseModel.setStatus(ReportStatus.Passed.name());
                                    }
                                } else if (AssertionOperation.Not_Equals.getDisplayValue().equalsIgnoreCase(assertion.getComparison())) {
                                    String parsedValue = resultString;
                                    if (!parsedValue.equalsIgnoreCase(assertion.getValue())) {
                                        testCaseAssertionResponseModel.setStatus(ReportStatus.Passed.name());
                                    }
                                } else if (AssertionOperation.Contains.getDisplayValue().equalsIgnoreCase(assertion.getComparison())) {
                                    String parsedValue = resultString;
                                    if (parsedValue.contains(assertion.getValue())) {
                                        testCaseAssertionResponseModel.setStatus(ReportStatus.Passed.name());
                                    }
                                } else if (AssertionOperation.Not_Contains.getDisplayValue().equalsIgnoreCase(assertion.getComparison())) {
                                    String parsedValue = resultString;
                                    if (!parsedValue.contains(assertion.getValue())) {
                                        testCaseAssertionResponseModel.setStatus(ReportStatus.Passed.name());
                                    }
                                } else if (AssertionOperation.Less_than.getDisplayValue().equalsIgnoreCase(assertion.getComparison())) {
                                    Double parsedValue = Double.parseDouble(resultString);
                                    if (parsedValue < Double.parseDouble(assertion.getValue())) {
                                        testCaseAssertionResponseModel.setStatus(ReportStatus.Passed.name());
                                    }
                                } else if (AssertionOperation.Greater_than.getDisplayValue().equalsIgnoreCase(assertion.getComparison())) {
                                    Double parsedValue = Double.parseDouble(resultString);
                                    if (parsedValue > Double.parseDouble(assertion.getValue())) {
                                        testCaseAssertionResponseModel.setStatus(ReportStatus.Passed.name());
                                    }
                                }
                            } else if (contentType.contains(MimeTypeUtils.APPLICATION_XML_VALUE)) {

                            } else if (contentType.contains(MimeTypeUtils.TEXT_HTML_VALUE)) {

                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("error in executeAssertions : {}", e);
                }
            });

            Long passedAssertions = responseAssertions.stream().filter(assertion -> assertion.getStatus().equals(ReportStatus.Passed.name())).count();
            result.setPassedAssertions(passedAssertions.intValue());
            result.setFailedAssertions(result.getTotalAssertions() - result.getPassedAssertions());
            ReportStatus status = result.getTotalAssertions() == result.getPassedAssertions() ? ReportStatus.Passed : ReportStatus.Failed;
            result.setStatus(status.name());
        } else {
            result.setStatus(ReportStatus.Passed.name());
        }
        return result;
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

    @Transactional
    public TestCaseReportResponseModel runTestCase(long testCaseId, Long testsuiteReportId) {
        TestCase testCase = findTestCase(testCaseId, ActiveStatus.ACTIVE.getDisplayValue());
        testCase.setAssertions(testCaseAssertionRepository.findAllByTestCaseIdAndActiveStatus(
                testCaseId, ActiveStatus.ACTIVE.getDisplayValue()
                , Sort.by(Sort.Direction.DESC, "id")));
        TestCaseTestRequestModel requestModel = new TestCaseTestRequestModel();
        requestModel.setEndpointUrl(testCase.getEndpointUrl());
        requestModel.setMethod(testCase.getMethod());
        requestModel.setRequestAuthorization(testCase.getAuthorization());
        requestModel.setRequestHeaders(testCase.getHeaders());
        requestModel.setRequestBody(testCase.getBody());
        requestModel.setRequestAssertions(prepareTestCaseAssertionModel(testCase.getAssertions()));
        TestCaseTestResponseModel responseModel = runTestCase(requestModel);

        TestCaseReport testCaseReport = saveTestCaseReport(testCase, responseModel, testsuiteReportId);
        return prepareTestCaseReportResponseModel(testCaseReport);
    }

    public TestCaseReport saveTestCaseReport(TestCase testCase, TestCaseTestResponseModel testCaseTestResponseModel, Long testsuiteReportId) {
        TestCaseReport testCaseReport = modelMapper.map(testCaseTestResponseModel, TestCaseReport.class);
        testCaseReport.setTestCase(testCase);
        testCaseReport.setProjectId(testCase.getProjectId());
        if (testsuiteReportId != null) {
            Optional<TestSuiteReport> testSuiteReportOptional = testSuiteReportRepository.findById(testsuiteReportId);
            if (testSuiteReportOptional.isPresent()) {
                testCaseReport.setTestSuiteReport(testSuiteReportOptional.get());
            }
        }
        testCaseReport.setExecutionDate(new Date());
        testCaseReport = testCaseReportRepository.save(testCaseReport);

        List<TestCaseAssertionResponse> assertions = Arrays.asList(modelMapper.map(testCaseTestResponseModel.getResponseAssertions(), TestCaseAssertionResponse[].class));
        for (TestCaseAssertionResponse assertion : assertions) {
            assertion.setTestCaseReport(testCaseReport);
            assertion.setAssertionType(AssertionType.getByDisplayValue(assertion.getAssertionType()).name());
            assertion.setComparison(AssertionOperation.getByDisplayValue(assertion.getComparison()).name());
        }
        assertions = testCaseAssertionResponseRepository.saveAll(assertions);

        testCaseReport.setAssertions(assertions);
        return testCaseReport;
    }

    private TestCaseReportResponseModel prepareTestCaseReportResponseModel(TestCaseReport testCaseReport) {
        TestCaseReportResponseModel reportResponseModel = modelMapper.map(testCaseReport, TestCaseReportResponseModel.class);
        reportResponseModel.setTestCaseRunId(testCaseReport.getId());
        reportResponseModel.setTestCaseId(testCaseReport.getTestCase().getId());
        reportResponseModel.setTestCaseName(testCaseReport.getTestCase().getName());
        reportResponseModel.setTestCaseDescription(testCaseReport.getTestCase().getDescription());
        if (testCaseReport.getTestSuiteReport() != null) {
            reportResponseModel.setTestsuiteReportId(testCaseReport.getTestSuiteReport().getId());
        }
        reportResponseModel.setResponseAssertions(prepareTestCaseAssertionResponseModel(testCaseReport.getAssertions()));
        return reportResponseModel;
    }

    public PageResponseModel<List<TestCaseUserReport>> getTestCaseUserReport(long projectId, String searchText,
                                                                             String startDate, String endDate,
                                                                             String status, String repositoryType,
                                                                             Integer pageNo, Integer pageSize) {
        validateTestCaseUserReportFilter(projectId, startDate, endDate, repositoryType, status);

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

        Page<TestCaseOverallReport> testCaseOverallReports =
                testCaseReportRepository.getTestCaseOverallReport(projectId, searchTextValue, startDateTime, endDateTime,
                        statusValue, repositoryType, pageable);

        List<TestCaseUserReport> reports = new ArrayList<>();
        if (testCaseOverallReports.getContent() != null && !testCaseOverallReports.getContent().isEmpty()) {
            testCaseOverallReports.getContent().forEach(report -> {
                TestCaseUserReport userReport = new TestCaseUserReport();
                userReport.setTestCaseId(report.getTestCaseId());
                userReport.setTestCaseName(report.getTestCaseName());
                userReport.setCreationDate(report.getCreationDate());
                userReport.setLastExecutionDate(report.getLastExecutionDate());
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

        totalElements = testCaseOverallReports.getTotalElements();
        totalPages = testCaseOverallReports.getTotalPages();
        currentPageSize = testCaseOverallReports.getNumberOfElements();

        PageResponseModel<List<TestCaseUserReport>> response = new PageResponseModel<>();
        response.setPageNo(pageNo);
        response.setRequestedPageSize(pageSize);
        response.setTotalPages(totalPages);
        response.setTotalElements(totalElements);
        response.setCurrentPageSize(currentPageSize);
        response.setItems(reports);
        return response;
    }

    public PageResponseModel<List<TestCaseReportResponseModel>> getTestCaseUserReportDetails(long testCaseId,
                                                                                             String startDate, String endDate,
                                                                                             String status,
                                                                                             Integer pageNo, Integer pageSize) {
        validateTestCaseUserReportDetailsFilter(testCaseId, startDate, endDate, status);

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

        TestCaseUserReportFilter filter = new TestCaseUserReportFilter();
        filter.setTestCaseId(testCaseId);
        filter.setStartDate(startDateTime);
        filter.setEndDate(endDateTime);
        filter.setStatus(status);

        Specification<TestCaseReport> specification = new TestCaseReportSpecification(filter);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(createSortOrder(null)));
        Page<TestCaseReport> testCaseReports = testCaseReportRepository.findAll(specification, pageable);
        totalElements = testCaseReports.getTotalElements();
        totalPages = testCaseReports.getTotalPages();
        currentPageSize = testCaseReports.getNumberOfElements();

        List<TestCaseReportResponseModel> testCaseReportResponseModels = testCaseReports.stream()
                .map(testCaseReport -> prepareTestCaseReportResponseModel(testCaseReport)).collect(Collectors.toList());

        PageResponseModel<List<TestCaseReportResponseModel>> response = new PageResponseModel<>();
        response.setPageNo(pageNo);
        response.setRequestedPageSize(pageSize);
        response.setTotalPages(totalPages);
        response.setTotalElements(totalElements);
        response.setCurrentPageSize(currentPageSize);
        response.setItems(testCaseReportResponseModels);
        return response;
    }

    private void validateTestCaseUserReportFilter(long projectId, String startDate, String endDate,
                                                  String repositoryType, String status) {
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


        if (StringUtils.isNotBlank(repositoryType)) {
            try {
                RepositoryType.getByDisplayValue(repositoryType);
            } catch (EnumNotFoundException e) {
                errors.put("repositoryType", e.getMessage());
            }
        } else {
            errors.put("repositoryType", "repositoryType is required");
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

    private void validateTestCaseUserReportDetailsFilter(long testCaseId, String startDate, String endDate, String status) {
        Map<String, String> errors = new HashMap<>();

        try {
            TestCase testCase = findTestCase(testCaseId, ActiveStatus.ACTIVE.getDisplayValue());
        } catch (ResourceNotFoundException e) {
            errors.put("testCaseId", e.getMessage());
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

    @Transactional
    public AuthorizationModel updateTestCaseAuthorization(long id, AuthorizationModel authorizationModel) {
        TestCase testCase = findTestCase(id, ActiveStatus.ACTIVE.getDisplayValue());

        validateTestCaseAuthorization(authorizationModel);

        testCase.setAuthorization(new ObjectMapper().convertValue(authorizationModel, new TypeReference<Map<String, String>>() {
        }));
        testCase.setUpdatedBy(userService.getLoggedInUser());
        testCase.setUpdationDate(new Date());
        testCase = testCaseRepository.save(testCase);

        return authorizationModel;
    }

    private void validateTestCaseAuthorization(AuthorizationModel authorizationModel) {

        Map<String, String> errors = new HashMap<>();

        AuthorizationType authorizationType = null;
        try {
            authorizationType = AuthorizationType.getByDisplayValue(authorizationModel.getType());
        } catch (EnumNotFoundException e) {
            errors.put("type", e.getMessage());
        }

        if (authorizationType != null && authorizationType.equals(AuthorizationType.Basic)) {
            if (StringUtils.isBlank(authorizationModel.getUserName())) {
                errors.put("userName", "userName is required");
            }
            if (StringUtils.isBlank(authorizationModel.getPassword())) {
                errors.put("password", "password is required");
            }
        }

        if (authorizationType != null && authorizationType.equals(AuthorizationType.Bearer_Token)) {
            if (StringUtils.isBlank(authorizationModel.getToken())) {
                errors.put("token", "token is required");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    @Transactional
    public Map<String, String> updateTestCaseHeaders(long id, Map<String, String> headers) {
        TestCase testCase = findTestCase(id, ActiveStatus.ACTIVE.getDisplayValue());

        testCase.setHeaders(headers);
        testCase.setUpdatedBy(userService.getLoggedInUser());
        testCase.setUpdationDate(new Date());
        testCase = testCaseRepository.save(testCase);

        return headers;
    }

    @Transactional
    public Map<String, Object> updateTestCaseBody(long id, Map<String, Object> body) {
        TestCase testCase = findTestCase(id, ActiveStatus.ACTIVE.getDisplayValue());

        testCase.setBody(body);
        testCase.setUpdatedBy(userService.getLoggedInUser());
        testCase.setUpdationDate(new Date());
        testCase = testCaseRepository.save(testCase);

        return body;
    }

    @Transactional
    public TestCaseAssertionsOutputModel updateTestCaseAssertions(long id, TestCaseAssertionsModel testCaseAssertionsModel) {
        TestCase testCase = findTestCase(id, ActiveStatus.ACTIVE.getDisplayValue());

        validateTestCaseAssertions(testCaseAssertionsModel.getAssertions());

        testCase.setUpdatedBy(userService.getLoggedInUser());
        testCase.setUpdationDate(new Date());
        testCase = testCaseRepository.save(testCase);

        List<TestCaseAssertionOutputModel> testCaseAssertionModels = updateTestCaseAssertions(testCase, testCaseAssertionsModel.getAssertions());

        TestCaseAssertionsOutputModel response = new TestCaseAssertionsOutputModel();
        response.setAssertions(testCaseAssertionModels);
        return response;
    }

    public List<TestCaseReportResponseModel> getTestCaseReportResponses(TestSuiteReport testSuiteReport) {
        List<TestCaseReportResponseModel> response = new ArrayList<>();
        if (testSuiteReport.getTestCaseReports() != null && !testSuiteReport.getTestCaseReports().isEmpty()) {
            testSuiteReport.getTestCaseReports().forEach(testCaseReport -> {
                response.add(prepareTestCaseReportResponseModel(testCaseReport));
            });
        }
        return response;
    }

    private void validateUpdateTestCase(TestCaseNameUpdateRequestModel testCaseNameUpdateRequestModel, long testId) {
        Map<String, String> errors = new HashMap<>();
        TestCase testCaseoldDataResponse = findTestCase(testId, ActiveStatus.ACTIVE.getDisplayValue());
        if (!testCaseoldDataResponse.getName().equalsIgnoreCase(testCaseNameUpdateRequestModel.getName())) {
            Project project = projectService.getProject(testCaseoldDataResponse.getProjectId());
            if (testCaseRepository.findByNameAndActiveStatusAndProjectId(testCaseNameUpdateRequestModel.getName(), ActiveStatus.ACTIVE.getDisplayValue(), testCaseoldDataResponse.getProjectId()).isPresent()) {
                errors.put("name", "TestCase exist with same name under project " + project.getName());
            } else {
                testCaseoldDataResponse.setName(testCaseNameUpdateRequestModel.getName());
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    @Transactional
    public TestCaseNameUpdateResposeModel updateTestCaseNameAndDescription(long id, TestCaseNameUpdateRequestModel testCaseNameUpdateRequestModel) {
        TestCase testCaseoldDataResponse = findTestCase(id, ActiveStatus.ACTIVE.getDisplayValue());
        validateUpdateTestCase(testCaseNameUpdateRequestModel, id);
        testCaseoldDataResponse.setUpdatedBy(userService.getLoggedInUser());
        testCaseoldDataResponse.setUpdationDate(new Date());
        testCaseoldDataResponse.setDescription(testCaseNameUpdateRequestModel.getDescription());
        testCaseoldDataResponse.setName(testCaseNameUpdateRequestModel.getName());
        TestCase testCasemodel = testCaseRepository.save(testCaseoldDataResponse);
        TestCaseNameUpdateResposeModel testmodel = new TestCaseNameUpdateResposeModel();
        testmodel.setName(testCasemodel.getName());
        testmodel.setDescription(testCasemodel.getDescription());
        testmodel.setId(id);
        return testmodel;
    }
}
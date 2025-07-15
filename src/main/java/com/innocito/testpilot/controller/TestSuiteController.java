package com.innocito.testpilot.controller;

import com.innocito.testpilot.model.*;
import com.innocito.testpilot.service.TestSuiteService;
import com.innocito.testpilot.util.BasicUtils;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/testsuite")
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class TestSuiteController {

    private final Logger logger = LogManager.getLogger(TestSuiteController.class);

    @Autowired
    private TestSuiteService testSuiteService;

    @Autowired
    private BasicUtils basicUtils;

    @PostMapping
    public ResponseEntity<ResponseModel<TestSuiteResponseModel>> createTestSuite(@Valid @RequestBody TestSuiteCreationModel testSuiteCreationModel) {
        TestSuiteResponseModel testSuiteResponseModel = testSuiteService.createTestSuite(testSuiteCreationModel);
        ResponseModel response = ResponseModel.<TestSuiteResponseModel>builder()
                .data(testSuiteResponseModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<TestSuiteResponseModel>> updateTestSuite(@PathVariable long id,
                                                                                 @Valid @RequestBody TestSuiteUpdationModel testSuiteUpdationModel) {
        TestSuiteResponseModel testSuiteResponseModel = testSuiteService.updateTestSuite(id, testSuiteUpdationModel);
        ResponseModel response = ResponseModel.<TestSuiteResponseModel>builder()
                .data(testSuiteResponseModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<TestSuiteResponseModel>> getTestSuite(@PathVariable long id) {
        TestSuiteResponseModel testSuiteResponseModel = testSuiteService.getTestSuite(id);
        ResponseModel response = ResponseModel.<TestSuiteResponseModel>builder()
                .data(testSuiteResponseModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageResponseModel<List<TestSuiteResponseModel>>> getAllTestSuites(@RequestParam long projectId,
                                                                                            @RequestParam(required = false) String searchText,
                                                                                            @RequestParam(required = false) Integer pageNo,
                                                                                            @RequestParam(required = false) Integer pageSize,
                                                                                            @RequestParam(required = false) String sortBy) {
        PageResponseModel<List<TestSuiteResponseModel>> response = testSuiteService.getAllTestSuites(projectId, searchText, pageNo, pageSize, sortBy);
        response.setMessage(basicUtils.getLocalizedMessage("successful", null));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteTestSuite(@PathVariable long id) {
        testSuiteService.deleteTestSuite(id);
        ResponseModel response = ResponseModel.<Void>builder()
                .data(null)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/sortParams")
    public ResponseEntity<ResponseModel<Set<String>>> getSortParams() {
        Set<String> sortParams = testSuiteService.sortPropertiesMap().keySet();
        ResponseModel response = ResponseModel.<Set<String>>builder()
                .data(sortParams)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/run")
    public ResponseEntity<ResponseModel<TestSuiteReportResponseModel>> runTestSuite(@PathVariable long id,
                                                                                    @RequestBody List<Long> testCaseIds) {
        TestSuiteReportResponseModel responseModel = testSuiteService.runTestSuite(id, testCaseIds);
        ResponseModel response = ResponseModel.<TestSuiteReportResponseModel>builder()
                .data(responseModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/report")
    public ResponseEntity<PageResponseModel<List<TestSuiteUserReport>>> getTestSuiteUserReport(@RequestParam long projectId,
                                                                                               @RequestParam(required = false) String searchText,
                                                                                               @RequestParam(required = false) String startDate,
                                                                                               @RequestParam(required = false) String endDate,
                                                                                               @RequestParam(required = false) String status,
                                                                                               @RequestParam(required = false) Integer pageNo,
                                                                                               @RequestParam(required = false) Integer pageSize) {
        PageResponseModel<List<TestSuiteUserReport>> response =
                testSuiteService.getTestSuiteUserReport(projectId, searchText, startDate, endDate, status, pageNo, pageSize);
        response.setMessage(basicUtils.getLocalizedMessage("successful", null));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/report/{testSuiteId}/details")
    public ResponseEntity<PageResponseModel<List<TestSuiteReportResponseModel>>> getTestSuiteUserReportDetails(@PathVariable long testSuiteId,
                                                                                                               @RequestParam(required = false) String startDate,
                                                                                                               @RequestParam(required = false) String endDate,
                                                                                                               @RequestParam(required = false) String status,
                                                                                                               @RequestParam(required = false) Integer pageNo,
                                                                                                               @RequestParam(required = false) Integer pageSize) {
        PageResponseModel<List<TestSuiteReportResponseModel>> response =
                testSuiteService.getTestSuiteUserReportDetails(testSuiteId, startDate, endDate, status, pageNo, pageSize);
        response.setMessage(basicUtils.getLocalizedMessage("successful", null));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
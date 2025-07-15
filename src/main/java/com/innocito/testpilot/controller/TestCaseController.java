package com.innocito.testpilot.controller;

import com.innocito.testpilot.model.*;
import com.innocito.testpilot.service.TestCaseService;
import com.innocito.testpilot.util.BasicUtils;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/testcase")
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class TestCaseController {

    private final Logger logger = LogManager.getLogger(TestCaseController.class);

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private BasicUtils basicUtils;

    @PostMapping
    public ResponseEntity<ResponseModel<TestCaseResponseModel>> createTestCase(@Valid @RequestBody TestCaseCreationModel testCaseCreationModel) {
        TestCaseResponseModel testCaseResponseModel = testCaseService.createTestCase(testCaseCreationModel);
        ResponseModel response = ResponseModel.<TestCaseResponseModel>builder()
                .data(testCaseResponseModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/run")
    public ResponseEntity<ResponseModel<TestCaseTestResponseModel>> updateAndRunTestCase(@PathVariable long id,
                                                                                         @RequestBody TestCaseUpdationModel testCaseUpdationModel) {
        long start = System.currentTimeMillis();
        TestCaseTestResponseModel responseModel = testCaseService.updateAndRunTestCase(id, testCaseUpdationModel);
        long end = System.currentTimeMillis();
        long elapsedTime = end - start;
        responseModel.getResponseMetadata().put("time", elapsedTime + " ms");
        ResponseModel response = ResponseModel.<TestCaseTestResponseModel>builder()
                .data(responseModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<TestCaseResponseModel>> getTestCase(@PathVariable long id) {
        TestCaseResponseModel testCaseResponseModel = testCaseService.getTestCase(id);
        ResponseModel response = ResponseModel.<TestCaseResponseModel>builder()
                .data(testCaseResponseModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageResponseModel<List<TestCaseResponseModel>>> getAllTestCases(@RequestParam long projectId,
                                                                                          @RequestParam(required = false) String searchText,
                                                                                          @RequestParam(required = false) Integer pageNo,
                                                                                          @RequestParam(required = false) Integer pageSize,
                                                                                          @RequestParam(required = false) String sortBy,
                                                                                          @RequestParam(required = false) boolean fetchAll) {
        PageResponseModel<List<TestCaseResponseModel>> response = testCaseService.getAllTestCases(
                projectId, searchText, pageNo, pageSize, sortBy, fetchAll);
        response.setMessage(basicUtils.getLocalizedMessage("successful", null));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteTestCase(@PathVariable long id) {
        testCaseService.deleteTestCase(id);
        ResponseModel response = ResponseModel.<Void>builder()
                .data(null)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/sortParams")
    public ResponseEntity<ResponseModel<Set<String>>> getSortParams() {
        Set<String> sortParams = testCaseService.sortPropertiesMap().keySet();
        ResponseModel response = ResponseModel.<Set<String>>builder()
                .data(sortParams)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/report")
    public ResponseEntity<PageResponseModel<List<TestCaseUserReport>>> getTestCaseUserReport(@RequestParam long projectId,
                                                                                             @RequestParam(required = false) String searchText,
                                                                                             @RequestParam(required = false) String startDate,
                                                                                             @RequestParam(required = false) String endDate,
                                                                                             @RequestParam String repositoryType,
                                                                                             @RequestParam(required = false) String status,
                                                                                             @RequestParam(required = false) Integer pageNo,
                                                                                             @RequestParam(required = false) Integer pageSize) {
        PageResponseModel<List<TestCaseUserReport>> response =
                testCaseService.getTestCaseUserReport(projectId, searchText, startDate, endDate, status, repositoryType, pageNo, pageSize);
        response.setMessage(basicUtils.getLocalizedMessage("successful", null));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/report/{testCaseId}/details")
    public ResponseEntity<PageResponseModel<List<TestCaseReportResponseModel>>> getTestCaseUserReportDetails(@PathVariable long testCaseId,
                                                                                                             @RequestParam(required = false) String startDate,
                                                                                                             @RequestParam(required = false) String endDate,
                                                                                                             @RequestParam(required = false) String status,
                                                                                                             @RequestParam(required = false) Integer pageNo,
                                                                                                             @RequestParam(required = false) Integer pageSize) {
        PageResponseModel<List<TestCaseReportResponseModel>> response =
                testCaseService.getTestCaseUserReportDetails(testCaseId, startDate, endDate, status, pageNo, pageSize);
        response.setMessage(basicUtils.getLocalizedMessage("successful", null));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/authorization")
    public ResponseEntity<ResponseModel<AuthorizationModel>> updateTestCaseAuthorization(@PathVariable long id,
                                                                                         @Valid @RequestBody AuthorizationModel authorizationModel) {
        AuthorizationModel responseModel = testCaseService.updateTestCaseAuthorization(id, authorizationModel);
        ResponseModel response = ResponseModel.<AuthorizationModel>builder()
                .data(responseModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/headers")
    public ResponseEntity<ResponseModel<Map<String, String>>> updateTestCaseHeaders(@PathVariable long id,
                                                                                    @RequestBody Map<String, String> headers) {
        Map<String, String> responseModel = testCaseService.updateTestCaseHeaders(id, headers);
        ResponseModel response = ResponseModel.<Map<String, String>>builder()
                .data(responseModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/body")
    public ResponseEntity<ResponseModel<Map<String, Object>>> updateTestCaseBody(@PathVariable long id,
                                                                                 @RequestBody Map<String, Object> body) {
        Map<String, Object> responseModel = testCaseService.updateTestCaseBody(id, body);
        ResponseModel response = ResponseModel.<Map<String, Object>>builder()
                .data(responseModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/assertions")
    public ResponseEntity<ResponseModel<TestCaseAssertionsOutputModel>> updateTestCaseAssertions(@PathVariable long id,
                                                                                                 @Valid @RequestBody TestCaseAssertionsModel testCaseAssertionsModel) {
        TestCaseAssertionsOutputModel responseModel = testCaseService.updateTestCaseAssertions(id, testCaseAssertionsModel);
        ResponseModel response = ResponseModel.<TestCaseAssertionsOutputModel>builder()
                .data(responseModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //for Update the testCase Name and Description
    @PutMapping("{id}")
    public ResponseEntity<ResponseModel<TestCaseNameUpdateResposeModel>> updateTestCaseNameandDescription(@PathVariable long id,
                                                                                                          @Valid @RequestBody TestCaseNameUpdateRequestModel testcase) {
        TestCaseNameUpdateResposeModel testCaseNameUpdateResposeModel = testCaseService.updateTestCaseNameAndDescription(id, testcase);
        ResponseModel responseModel = ResponseModel.<TestCaseNameUpdateResposeModel>builder()
                .data(testCaseNameUpdateResposeModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }
}
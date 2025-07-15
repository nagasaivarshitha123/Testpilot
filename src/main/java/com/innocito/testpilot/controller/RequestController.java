package com.innocito.testpilot.controller;

import com.innocito.testpilot.model.*;
import com.innocito.testpilot.service.RequestService;
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

@RestController
@RequestMapping("/api/request")
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class RequestController {

    private final Logger logger = LogManager.getLogger(RequestController.class);

    @Autowired
    private RequestService requestService;

    @Autowired
    private BasicUtils basicUtils;

    @PutMapping("/{id}/run")
    public ResponseEntity<ResponseModel<TestResponseModel>> updateAndRunRequest(@PathVariable long id,
                                                                                @RequestBody RequestUpdationModel requestUpdationModel) {
        long start = System.currentTimeMillis();
        TestResponseModel testResponseModel = requestService.updateAndRunRequest(id, requestUpdationModel);
        long end = System.currentTimeMillis();
        long elapsedTime = end - start;
        testResponseModel.getResponseMetadata().put("time", elapsedTime + " ms");

        ResponseModel response = ResponseModel.<TestResponseModel>builder()
                .data(testResponseModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<RequestOutput>> getRequest(@PathVariable long id) {
        RequestOutput requestOutput = requestService.getRequest(id);
        ResponseModel response = ResponseModel.<RequestOutput>builder()
                .data(requestOutput)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ResponseModel<List<RequestOutput>>> getRequests(@RequestParam long projectId) {
        List<RequestOutput> data = requestService.getRequests(projectId);
        ResponseModel response = ResponseModel.<List<RequestOutput>>builder()
                .data(data)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/authorization")
    public ResponseEntity<ResponseModel<AuthorizationModel>> updateRequestAuthorization(@PathVariable long id,
                                                                                        @Valid @RequestBody AuthorizationModel authorizationModel) {
        AuthorizationModel responseModel = requestService.updateRequestAuthorization(id, authorizationModel);
        ResponseModel response = ResponseModel.<AuthorizationModel>builder()
                .data(responseModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/headers")
    public ResponseEntity<ResponseModel<Map<String, String>>> updateRequestHeaders(@PathVariable long id,
                                                                                   @RequestBody Map<String, String> headers) {
        Map<String, String> responseModel = requestService.updateRequestHeaders(id, headers);
        ResponseModel response = ResponseModel.<Map<String, String>>builder()
                .data(responseModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/body")
    public ResponseEntity<ResponseModel<Map<String, Object>>> updateRequestBody(@PathVariable long id,
                                                                                @RequestBody Map<String, Object> body) {
        Map<String, Object> responseModel = requestService.updateRequestBody(id, body);
        ResponseModel response = ResponseModel.<Map<String, Object>>builder()
                .data(responseModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //For updating the Request Name and Description API
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<RequestResponseUpdateModel>> updateRequest(@PathVariable long id,
                                                                                   @Valid @RequestBody RequestUpdateModel request) {
        RequestResponseUpdateModel reuqestResponseModel = requestService.updateRequestNameAndDescription(id, request);
        ResponseModel response = ResponseModel.<RequestResponseUpdateModel>builder()
                .data(reuqestResponseModel)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Creation of new request under the existing repository    
    @PostMapping("/create")
    public ResponseEntity<ResponseModel<RequestOutput>> createRequest(@Valid @RequestBody RequestCreationalModel requestCreateRequest) {
        RequestOutput apiRepositoryResponse = requestService.createRequest(requestCreateRequest);
        ResponseModel response = ResponseModel.<RequestOutput>builder()
                .data(apiRepositoryResponse)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //for deleting the request
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteRequest(@PathVariable long id) {
        requestService.deleteRequest(id);
        ResponseModel response = ResponseModel.<Void>builder()
                .data(null)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
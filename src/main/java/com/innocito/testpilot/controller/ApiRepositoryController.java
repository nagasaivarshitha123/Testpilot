package com.innocito.testpilot.controller;

import com.innocito.testpilot.model.*;
import com.innocito.testpilot.service.ApiRepositoryService;
import com.innocito.testpilot.util.BasicUtils;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/repository")
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class ApiRepositoryController {

    private final Logger logger = LogManager.getLogger(ApiRepositoryController.class);

    @Autowired
    private ApiRepositoryService apiRepositoryService;

    @Autowired
    private BasicUtils basicUtils;

    @PostMapping
    public ResponseEntity<ResponseModel<ApiRepositoryResponse>> createRepository(@Valid @RequestBody ApiRepositoryCreateRequest apiRepositoryCreateRequest) {
        ApiRepositoryResponse apiRepositoryResponse = apiRepositoryService.createApiRepository(apiRepositoryCreateRequest);
        ResponseModel response = ResponseModel.<ApiRepositoryResponse>builder()
                .data(apiRepositoryResponse)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ApiRepositoryResponse>> updateRepository(@PathVariable long id, @Valid @RequestBody ApiRepositoryUpdateRequest apiRepositoryUpdateRequest) {
        ApiRepositoryResponse apiRepositoryResponse = apiRepositoryService.updateApiRepository(id, apiRepositoryUpdateRequest);
        ResponseModel response = ResponseModel.<ApiRepositoryResponse>builder()
                .data(apiRepositoryResponse)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
//
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ApiRepositoryResponse>> getRepository(@PathVariable long id) {
        ApiRepositoryResponse apiRepositoryResponse = apiRepositoryService.getApiRepository(id);
        ResponseModel response = ResponseModel.<ApiRepositoryResponse>builder()
                .data(apiRepositoryResponse)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping// all is add by me  , we can remove 'all' if we want repo with id
    public ResponseEntity<ResponseModel<ApiRepositoriesResponse>> getRepositories(@RequestParam long projectId) {
        ApiRepositoriesResponse apiRepositoriesResponse = apiRepositoryService.getApiRepositories(projectId);
        ResponseModel response = ResponseModel.<ApiRepositoriesResponse>builder()
                .data(apiRepositoriesResponse)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

// getting all projects  withoutId
//    @GetMapping
//    public ResponseEntity<ResponseModel<ApiRepositoriesResponse>> getRepositories() {
//        ApiRepositoriesResponse apiRepositoriesResponse = apiRepositoryService.getAllApiRepositories();
//
//        ResponseModel<ApiRepositoriesResponse> response = ResponseModel.<ApiRepositoriesResponse>builder()
//                .data(apiRepositoriesResponse)
//                .message(basicUtils.getLocalizedMessage("successful", null))
//                .build();
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//}




    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<Void>> deleteRepository(@PathVariable long id) {
        apiRepositoryService.deleteApiRepository(id);
        ResponseModel response = ResponseModel.<Void>builder()
                .data(null)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
package com.innocito.testpilot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innocito.testpilot.entity.ApiRepository;
import com.innocito.testpilot.entity.Project;
import com.innocito.testpilot.entity.Request;
import com.innocito.testpilot.enums.ActiveStatus;
import com.innocito.testpilot.enums.AuthorizationType;
import com.innocito.testpilot.enums.HttpMethod;
import com.innocito.testpilot.exception.EnumNotFoundException;
import com.innocito.testpilot.exception.ResourceNotFoundException;
import com.innocito.testpilot.exception.UniqueConstraintViolationException;
import com.innocito.testpilot.exception.ValidationException;
import com.innocito.testpilot.model.*;
import com.innocito.testpilot.repository.ApiRepoRepository;
import com.innocito.testpilot.repository.RequestRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RequestService {
    private final Logger logger = LogManager.getLogger(RequestService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ApiRepoRepository apiRepoRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Validator validator;

    @Autowired
    private UserService userService;
    @Autowired
    private TenantData tenantData;

    @Transactional
    public RequestOutput createRequest(RequestCreationModel requestCreationModel) {
        Request request = modelMapper.map(requestCreationModel, Request.class);
        request.setMethod(HttpMethod.getByDisplayValue(requestCreationModel.getMethod()).name());
        request.setActiveStatus(ActiveStatus.ACTIVE.getDisplayValue());
        //request.setCreatedBy(userService.getLoggedInUser());
        request.setCreatedBy(tenantData.getLoggedInUserName());
        request.setCreationDate(new Date());
        Project project = projectService.getProject(requestCreationModel.getProjectId());
        request.setProjectId(project.getId());
        request.setApiRepository(apiRepoRepository.getById(requestCreationModel.getRepositoryId()));
        request = requestRepository.save(request);
        RequestOutput requestOutput = modelMapper.map(request, RequestOutput.class);
        requestOutput.setRepositoryId(requestCreationModel.getRepositoryId());
        return requestOutput;
    }

    public RequestCreationModel prepareRequestCreationModel(ApiRepositoryCreateRequest repositoryCreateRequest) {
        RequestCreationModel requestCreationModel = new RequestCreationModel();
        requestCreationModel.setName("Request 1");
        requestCreationModel.setDescription(repositoryCreateRequest.getDescription());
        requestCreationModel.setProjectId(repositoryCreateRequest.getProjectId());
        //requestCreationModel.setRepositoryId();
        requestCreationModel.setEndpointUrl(repositoryCreateRequest.getRepositoryUrl());
        requestCreationModel.setMethod(HttpMethod.GET.name());
        //requestCreationModel.setAuthorization();
        //requestCreationModel.setHeaders();
        //requestCreationModel.setBody();
        return requestCreationModel;
    }

    public List<Request> saveAll(List<Request> requests) {
        return requestRepository.saveAll(requests);
    }

    public Request findRequest(long id, int activeStatus) {
        return requestRepository.findByIdAndActiveStatus(id, activeStatus).orElseThrow(() ->
                new ResourceNotFoundException("id", "Request not found with id : " + id));
    }

    @Transactional
    public RequestOutput updateRequest(long id, RequestUpdationModel requestUpdationModel) {
        Set<ConstraintViolation<RequestUpdationModel>> constraintViolation = validator.validate(requestUpdationModel);
        if (!constraintViolation.isEmpty()) {
            throw new ConstraintViolationException(constraintViolation);
        }

        Request request = findRequest(id, ActiveStatus.ACTIVE.getDisplayValue());
        request.setEndpointUrl(requestUpdationModel.getEndpointUrl());
        request.setMethod(HttpMethod.getByDisplayValue(requestUpdationModel.getMethod()).name());
      //  request.setUpdatedBy(userService.getLoggedInUser());
        request.setUpdatedBy(tenantData.getLoggedInUserName());
        request.setUpdationDate(new Date());
        request = requestRepository.save(request);
        RequestOutput requestOutput = modelMapper.map(request, RequestOutput.class);
        requestOutput.setRepositoryId(request.getApiRepository().getId());
        return requestOutput;
    }

    public RequestOutput getRequest(long id) {
        Request request = findRequest(id, ActiveStatus.ACTIVE.getDisplayValue());
        RequestOutput requestOutput = modelMapper.map(request, RequestOutput.class);
        requestOutput.setRepositoryId(request.getApiRepository().getId());
        return requestOutput;
    }

    public TestResponseModel runRequest(TestRequestModel testRequestModel) {
        TestResponseModel testResponseModel = new TestResponseModel();
        HttpHeaders headers = new HttpHeaders();
        if (testRequestModel.getRequestHeaders() != null && !testRequestModel.getRequestHeaders().isEmpty()) {
            testRequestModel.getRequestHeaders().entrySet().forEach(item -> {
                headers.add(item.getKey(), item.getValue());
            });
        }

        HttpEntity<Object> entity = new HttpEntity<>(testRequestModel.getRequestBody(), headers);

        boolean success;
        int status;
        long size = 0;
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(testRequestModel.getEndpointUrl(),
                    org.springframework.http.HttpMethod.valueOf(
                            HttpMethod.getByDisplayValue(testRequestModel.getMethod()).name()),
                    entity, String.class);

            success = true;
            status = responseEntity.getStatusCodeValue();
            if (responseEntity.getBody() != null) {
                size = responseEntity.getBody().getBytes().length;
                testResponseModel.setResponseBody(responseEntity.getBody());
            }
            testResponseModel.setResponseHeaders(responseEntity.getHeaders().toSingleValueMap());
        } catch (RestClientResponseException e) {
            success = false;
            status = e.getRawStatusCode();
            String errorMessage = StringUtils.isNotBlank(e.getResponseBodyAsString()) ? e.getResponseBodyAsString() : e.getMessage();
            size = errorMessage.getBytes().length;

            testResponseModel.setErrorMessage(errorMessage);
            testResponseModel.setResponseHeaders(e.getResponseHeaders().toSingleValueMap());
        } catch (RestClientException e) {
            success = false;
            status = 0;
            String errorMessage = e.getMessage();
            size = errorMessage.getBytes().length;
            testResponseModel.setErrorMessage(errorMessage);
        }

        testResponseModel.setSuccess(success);
        testResponseModel.setEndpointUrl(testRequestModel.getEndpointUrl());
        testResponseModel.setMethod(testRequestModel.getMethod());
        testResponseModel.setRequestAuthorization(testRequestModel.getRequestAuthorization());
        testResponseModel.setRequestHeaders(testRequestModel.getRequestHeaders());
        testResponseModel.setRequestBody(testRequestModel.getRequestBody());

        Map<String, String> responseMetadata = new HashMap<>();
        responseMetadata.put("time", "");
        responseMetadata.put("status", status + "");
        responseMetadata.put("size", size + " bytes");

        testResponseModel.setResponseMetadata(responseMetadata);

        return testResponseModel;
    }

    public TestResponseModel updateAndRunRequest(long id, RequestUpdationModel requestUpdationModel) {
        RequestOutput requestOutput = updateRequest(id, requestUpdationModel);

        TestRequestModel testRequestModel = new TestRequestModel();
        testRequestModel.setEndpointUrl(requestOutput.getEndpointUrl());
        testRequestModel.setMethod(requestOutput.getMethod());
        testRequestModel.setRequestAuthorization(requestOutput.getAuthorization());
        testRequestModel.setRequestHeaders(requestOutput.getHeaders());
        testRequestModel.setRequestBody(requestOutput.getBody());
        TestResponseModel testResponseModel = runRequest(testRequestModel);
        return testResponseModel;
    }

    public List<RequestOutput> getRequests(long projectId) {
        Project project = projectService.getProject(projectId);
        List<Request> requests = requestRepository.findByProjectIdAndActiveStatus(projectId, ActiveStatus.ACTIVE.getDisplayValue(), org.springframework.data.domain.Sort.by(Arrays.asList(new Sort.Order(org.springframework.data.domain.Sort.Direction.DESC, "id"))));
        List<RequestOutput> requestOutputList = requests.stream().map(request -> {
            RequestOutput requestOutput = modelMapper.map(request, RequestOutput.class);
            requestOutput.setRepositoryId(request.getApiRepository().getId());
            return requestOutput;
        }).collect(Collectors.toList());
        return requestOutputList;
    }

    @Transactional
    public AuthorizationModel updateRequestAuthorization(long id, AuthorizationModel authorizationModel) {
        Request request = findRequest(id, ActiveStatus.ACTIVE.getDisplayValue());

        validateRequestAuthorization(authorizationModel);

        request.setAuthorization(new ObjectMapper().convertValue(authorizationModel, new TypeReference<Map<String, String>>() {
        }));
        request.setUpdatedBy(userService.getLoggedInUser());
        request.setUpdationDate(new Date());
        request = requestRepository.save(request);

        return authorizationModel;
    }

    private void validateRequestAuthorization(AuthorizationModel authorizationModel) {

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
    public Map<String, String> updateRequestHeaders(long id, Map<String, String> headers) {
        Request request = findRequest(id, ActiveStatus.ACTIVE.getDisplayValue());

        request.setHeaders(headers);
        request.setUpdatedBy(userService.getLoggedInUser());
        request.setUpdationDate(new Date());
        request = requestRepository.save(request);

        return headers;
    }

    @Transactional
    public Map<String, Object> updateRequestBody(long id, Map<String, Object> body) {
        Request request = findRequest(id, ActiveStatus.ACTIVE.getDisplayValue());

        request.setBody(body);
       // request.setUpdatedBy(userService.getLoggedInUser());
        request.setUpdatedBy(tenantData.getLoggedInUserName());
        request.setUpdationDate(new Date());
        request = requestRepository.save(request);

        return body;
    }

    @Transactional
    public RequestResponseUpdateModel updateRequestNameAndDescription(long id, RequestUpdateModel request) {
        Request requestOld = findRequest(id, ActiveStatus.ACTIVE.getDisplayValue());
        ApiRepository apiRepositoesponse = requestOld.getApiRepository();
        String currentUser = tenantData.getLoggedInUserName();

        if (apiRepositoesponse .getCreatedBy() == null || !apiRepositoesponse .getCreatedBy().equals(currentUser)) {
            throw new SecurityException("You are not authorized to update this repository");
        }
        if (!requestOld.getName().equalsIgnoreCase(request.getName())) {
            if (requestRepository.findByApiRepositoryIdAndName(apiRepositoesponse.getId(), request.getName()).isPresent()) {
                throw new UniqueConstraintViolationException("name", "Request name already existing in this repository : " + apiRepositoesponse.getName());
            } else {
                request.setName(request.getName());
            }
        }
        requestOld.setName(request.getName());
        requestOld.setDescription(request.getDescription());
        //requestOld.setUpdatedBy(userService.getLoggedInUser());
        requestOld.setUpdatedBy(tenantData.getLoggedInUserName());
        requestOld.setUpdationDate(new Date());
        Request updateReq = requestRepository.save(requestOld);
        RequestResponseUpdateModel responseModel = new RequestResponseUpdateModel();
        responseModel.setName(updateReq.getName());
        responseModel.setDescription(updateReq.getDescription());
        responseModel.setRepositoryId(apiRepositoesponse.getId());
        return responseModel;
    }

    public ApiRepository findApiRepository(long id, int activeStatus) {
        return apiRepoRepository.findByIdAndActiveStatus(id, activeStatus).orElseThrow(() ->
                new ResourceNotFoundException("id", "ApiRepository not found with id : " + id));
    }

    private void validateCreateRequest(RequestCreationalModel requestCreationModel) {
        Map<String, String> errors = new HashMap<>();
        if (requestRepository.findByApiRepositoryIdAndName(requestCreationModel.getRepositoryId(), requestCreationModel.getName()).isPresent()) {
            errors.put("name", "Request name is already existing in this repository : " + requestCreationModel.getName());
        }

        try {
            HttpMethod.getByDisplayValue(requestCreationModel.getMethod());
        } catch (EnumNotFoundException e) {
            errors.put("Method", e.getMessage());
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    //creating a request
    @Transactional
    public RequestOutput createRequest(RequestCreationalModel requestCreationalModel) {
        ApiRepository apiRepository = findApiRepository(requestCreationalModel.getRepositoryId(), ActiveStatus.ACTIVE.getDisplayValue());
        validateCreateRequest(requestCreationalModel);
        RequestCreationalModel requestCreationModel = prepareCreateRequestModel(requestCreationalModel);
        requestCreationModel.setRepositoryId(apiRepository.getId());
        requestCreationalModel.setProjectId(requestCreationModel.getProjectId());
        RequestOutput requestOutput = createNewRequest(requestCreationModel);
        return requestOutput;
    }

    public RequestOutput createNewRequest(RequestCreationalModel requestCreationModel) {
        Request request = modelMapper.map(requestCreationModel, Request.class);
        request.setMethod(HttpMethod.getByDisplayValue(requestCreationModel.getMethod()).name());
        request.setActiveStatus(ActiveStatus.ACTIVE.getDisplayValue());
        request.setCreatedBy(userService.getLoggedInUser());
        request.setCreationDate(new Date());
        request.setApiRepository(apiRepoRepository.getById(requestCreationModel.getRepositoryId()));
        request.setEndpointUrl(requestCreationModel.getUrl());
        request.setProjectId(requestCreationModel.getProjectId());
        request.setAuthorization(null);
        request.setHeaders(null);
        request.setBody(null);
        request = requestRepository.save(request);
        RequestOutput requestOutput = modelMapper.map(request, RequestOutput.class);
        requestOutput.setRepositoryId(requestCreationModel.getRepositoryId());
        return requestOutput;
    }

    public RequestCreationalModel prepareCreateRequestModel(RequestCreationalModel repositoryCreateRequest) {
        RequestCreationalModel requestCreationModel = new RequestCreationalModel();
        requestCreationModel.setName(repositoryCreateRequest.getName());
        requestCreationModel.setDescription(repositoryCreateRequest.getDescription());
        requestCreationModel.setProjectId(repositoryCreateRequest.getProjectId());
        requestCreationModel.setUrl(repositoryCreateRequest.getUrl());
        requestCreationModel.setMethod(repositoryCreateRequest.getMethod());
        return requestCreationModel;
    }

    @Transactional
    public void deleteRequest(long id) {
        Request request = findRequest(id, ActiveStatus.ACTIVE.getDisplayValue());
        String currentUser = tenantData.getLoggedInUserName();    //added if also

        if (request.getCreatedBy() == null || !request.getCreatedBy().equals(currentUser)) {
            throw new SecurityException("You are not authorized to update this repository");
        }
        request.setActiveStatus(ActiveStatus.INACTIVE.getDisplayValue());
        //request.setUpdatedBy(userService.getLoggedInUser());
        request.setUpdatedBy(tenantData.getLoggedInUserName());
        request.setUpdationDate(new Date());
        request = requestRepository.save(request);
    }
}
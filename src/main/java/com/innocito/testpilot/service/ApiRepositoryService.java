package com.innocito.testpilot.service;

import com.innocito.testpilot.entity.ApiRepository;
import com.innocito.testpilot.entity.Project;
import com.innocito.testpilot.enums.ActiveStatus;
import com.innocito.testpilot.enums.RepositoryType;
import com.innocito.testpilot.exception.EnumNotFoundException;
import com.innocito.testpilot.exception.ResourceNotFoundException;
import com.innocito.testpilot.exception.UniqueConstraintViolationException;
import com.innocito.testpilot.exception.ValidationException;
import com.innocito.testpilot.model.*;
import com.innocito.testpilot.repository.ApiRepoRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApiRepositoryService {
    private final Logger logger = LogManager.getLogger(ApiRepositoryService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ApiRepoRepository apiRepoRepository;

    @Autowired
    private RequestService requestService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;
    @Autowired
    private TenantData tenantData;



    @Transactional
    public ApiRepositoryResponse createApiRepository(ApiRepositoryCreateRequest repositoryCreateRequest) {
        validateCreateRepository(repositoryCreateRequest);
        ApiRepository apiRepository = modelMapper.map(repositoryCreateRequest, ApiRepository.class);
//        Project project = projectService.getProject(repositoryCreateRequest.getProjectId());
//        apiRepository.setProjectId(project.getId());
        Project project = projectService.getProject(repositoryCreateRequest.getProjectId());
        apiRepository.setProject(project);


        apiRepository.setRepositoryType(RepositoryType.getByDisplayValue(repositoryCreateRequest.getRepositoryType()).name());
        apiRepository.setActiveStatus(ActiveStatus.ACTIVE.getDisplayValue());
        apiRepository.setCreatedBy(userService.getLoggedInUser());
        apiRepository.setCreationDate(new Date());
        apiRepository = apiRepoRepository.save(apiRepository);

        RequestCreationModel requestCreationModel = requestService.prepareRequestCreationModel(repositoryCreateRequest);
        requestCreationModel.setRepositoryId(apiRepository.getId());
        RequestOutput requestOutput = requestService.createRequest(requestCreationModel);

        ApiRepositoryResponse apiRepositoryResponse = modelMapper.map(apiRepository, ApiRepositoryResponse.class);

        List<RequestOutput> requests = new ArrayList<>();
        requests.add(requestOutput);
        apiRepositoryResponse.setRequests(requests);

        return apiRepositoryResponse;
    }

    private void validateCreateRepository(ApiRepositoryCreateRequest repositoryCreateRequest) {
        Map<String, String> errors = new HashMap<>();
        Project project = projectService.getProject(repositoryCreateRequest.getProjectId());
        if (findApiRepository(repositoryCreateRequest.getProjectId(), repositoryCreateRequest.getName()).isPresent()) {
            errors.put("name", "Repository exist with same name under project : " + project.getName());
        }

        try {
            RepositoryType.getByDisplayValue(repositoryCreateRequest.getRepositoryType());
        } catch (EnumNotFoundException e) {
            errors.put("repositoryType", e.getMessage());
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public Optional<ApiRepository> findApiRepository(long projectId, String name) {
        return apiRepoRepository.findByProjectIdAndName(projectId, name);
    }

    public ApiRepository findApiRepository(long id, int activeStatus) {
        return apiRepoRepository.findByIdAndActiveStatus(id, activeStatus).orElseThrow(() ->
                new ResourceNotFoundException("id", "ApiRepository not found with id : " + id));
    }

    public List<ApiRepository> findApiRepositories(long projectId, int activeStatus) {
        return apiRepoRepository.findByProjectIdAndActiveStatus(projectId, activeStatus, Sort.by(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "id"))));
    }

    @Transactional
    public ApiRepositoryResponse updateApiRepository(long id, ApiRepositoryUpdateRequest repositoryUpdateRequest) {
        ApiRepository apiRepository = findApiRepository(id, ActiveStatus.ACTIVE.getDisplayValue());
        String currentUser = tenantData.getLoggedInUserName();

        if (apiRepository.getCreatedBy() == null || !apiRepository.getCreatedBy().equals(currentUser)) {
            throw new SecurityException("You are not authorized to update this repository");
        }


        if (!apiRepository.getName().equalsIgnoreCase(repositoryUpdateRequest.getName())) {
//            Project project = projectService.getProject(apiRepository.getProjectId());
          Project project = projectService.getProject(apiRepository.getProject().getId());// added me

            if (findApiRepository(apiRepository.getProject().getId(), repositoryUpdateRequest.getName()).isPresent()) {
                throw new UniqueConstraintViolationException("name", "Repository exist with same name under project : " + project.getName());
            } else {
                apiRepository.setName(repositoryUpdateRequest.getName());
            }
        }

        apiRepository.setDescription(repositoryUpdateRequest.getDescription());
        apiRepository.setUpdatedBy(userService.getLoggedInUser());
        apiRepository.setUpdationDate(new Date());
        apiRepository = apiRepoRepository.save(apiRepository);

        ApiRepositoryResponse apiRepositoryResponse = modelMapper.map(apiRepository, ApiRepositoryResponse.class);

        List<RequestOutput> requests = Arrays.asList(modelMapper.map(apiRepository.getRequests(), RequestOutput[].class));
        apiRepositoryResponse.setRequests(requests);

        return apiRepositoryResponse;
    }

    @Transactional
    public void deleteApiRepository(long id) {
        ApiRepository apiRepository = findApiRepository(id, ActiveStatus.ACTIVE.getDisplayValue());
        String currentUser = tenantData.getLoggedInUserName();    //added if also

        if (apiRepository.getCreatedBy() == null || !apiRepository.getCreatedBy().equals(currentUser)) {
            throw new SecurityException("You are not authorized to update this repository");
        }
        apiRepository.setActiveStatus(ActiveStatus.INACTIVE.getDisplayValue());
        apiRepository.setUpdatedBy(userService.getLoggedInUser());
        apiRepository.setUpdationDate(new Date());
        apiRepository = apiRepoRepository.save(apiRepository);

        apiRepository.getRequests().forEach(request -> {
            request.setActiveStatus(ActiveStatus.INACTIVE.getDisplayValue());
            request.setUpdatedBy(userService.getLoggedInUser());
            request.setUpdationDate(new Date());
        });
        requestService.saveAll(apiRepository.getRequests());
    }

    public ApiRepositoryResponse getApiRepository(long id) {
        ApiRepository apiRepository = findApiRepository(id, ActiveStatus.ACTIVE.getDisplayValue());
        ApiRepositoryResponse apiRepositoryResponse = modelMapper.map(apiRepository, ApiRepositoryResponse.class);

        List<RequestOutput> requests = Arrays.asList(modelMapper.map(apiRepository.getRequests(), RequestOutput[].class));
        apiRepositoryResponse.setRequests(requests);

        return apiRepositoryResponse;
    }

    public ApiRepositoriesResponse getApiRepositories(long projectId) {
        Project project = projectService.getProject(projectId);
        //List<ApiRepository> apiRepositoryListSOAP = apiRepoRepository.findByProjectIdAndActiveStatusAndRepositoryType(projectId, ActiveStatus.ACTIVE.getDisplayValue(), RepositoryType.SOAP.name(), Sort.by(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "id"))));
        List<ApiRepository> apiRepositoryListREST = apiRepoRepository.findByProjectIdAndActiveStatusAndRepositoryType(projectId, ActiveStatus.ACTIVE.getDisplayValue(), RepositoryType.REST.name(), Sort.by(Arrays.asList(new Sort.Order(Sort.Direction.DESC, "id"))));

        List<ApiRepositoryResponse> apiRepositoryResponseListSOAP = new ArrayList<>();
        List<ApiRepositoryResponse> apiRepositoryResponseListREST = new ArrayList<>();

//        apiRepositoryListSOAP.forEach(apiRepository -> {
//            ApiRepositoryResponse apiRepositoryResponse = modelMapper.map(apiRepository, ApiRepositoryResponse.class);
//            List<RequestOutput> requests = Arrays.asList(modelMapper.map(apiRepository.getRequests(), RequestOutput[].class));
//            apiRepositoryResponse.setRequests(requests);
//            apiRepositoryResponseListSOAP.add(apiRepositoryResponse);
//        });

        apiRepositoryListREST.forEach(apiRepository -> {
            ApiRepositoryResponse apiRepositoryResponse = modelMapper.map(apiRepository, ApiRepositoryResponse.class);
            if (apiRepositoryResponse.getRequests() != null && !apiRepositoryResponse.getRequests().isEmpty()) {
                List<RequestOutput> requests = Arrays.asList(modelMapper.map(apiRepositoryResponse.getRequests().stream().filter(req -> req.getActiveStatus() == ActiveStatus.ACTIVE.getDisplayValue()).collect(Collectors.toList()),
                        RequestOutput[].class));
                apiRepositoryResponse.setRequests(requests);
                apiRepositoryResponseListREST.add(apiRepositoryResponse);
            }
        });

        ApiRepositoriesResponse response = new ApiRepositoriesResponse();
//        response.setSOAP(apiRepositoryResponseListSOAP);
      response.setREST(apiRepositoryResponseListREST);
        return response;
    }
//
//    public ApiRepositoriesResponse getAllApiRepositories() {
//
//
//        List<ApiRepository> restRepositories = apiRepoRepository.findByActiveStatusAndRepositoryType(
//                ActiveStatus.ACTIVE.getDisplayValue(), RepositoryType.REST.name(), Sort.by(Sort.Order.desc("id"))
//        );
//
//
//
//        List<ApiRepositoryResponse> restResponses = restRepositories.stream().map(apiRepository -> {
//            ApiRepositoryResponse response = modelMapper.map(apiRepository, ApiRepositoryResponse.class);
//            List<RequestOutput> activeRequests = apiRepository.getRequests().stream()
//                    .filter(req -> req.getActiveStatus() == ActiveStatus.ACTIVE.getDisplayValue())
//                    .map(req -> modelMapper.map(req, RequestOutput.class))
//                    .collect(Collectors.toList());
//            response.setRequests(activeRequests);
//            return response;
//        }).collect(Collectors.toList());
//
//        ApiRepositoriesResponse response = new ApiRepositoriesResponse();
//
//        //response.setREST(restResponses);
//        return response;
//    }

//    public ApiRepositoriesResponse getAllApiRepositories() {
//        List<ApiRepository> allRepositories = apiRepoRepository.findByActiveStatus(
//                ActiveStatus.ACTIVE.getDisplayValue(), Sort.by(Sort.Order.desc("id")));
//
//        List<ApiRepositoryResponse> apiRepositoryResponseList = allRepositories.stream().map(apiRepository -> {
//            ApiRepositoryResponse response = modelMapper.map(apiRepository, ApiRepositoryResponse.class);
//
//            List<RequestOutput> activeRequests = apiRepository.getRequests().stream()
//                    .filter(req -> req.getActiveStatus() == ActiveStatus.ACTIVE.getDisplayValue())
//                    .map(req -> modelMapper.map(req, RequestOutput.class))
//                    .collect(Collectors.toList());
//
//            response.setRequests(activeRequests);
//            return response;
//        }).collect(Collectors.toList());
//
//        ApiRepositoriesResponse response = new ApiRepositoriesResponse();
//        response.setRepositories(apiRepositoryResponseList);
//        return response;
//    }

    public ApiRepositoriesResponse getAllApiRepositories() {
        List<ApiRepository> allRepositories = apiRepoRepository.findByActiveStatus(
                ActiveStatus.ACTIVE.getDisplayValue(), Sort.by(Sort.Order.desc("id")));

        List<ApiRepositoryResponse> apiRepositoryResponseList = allRepositories.stream()
                .map(apiRepository -> {
                    ApiRepositoryResponse response = modelMapper.map(apiRepository, ApiRepositoryResponse.class);
                    // Remove requests from response so they won't appear in JSON
                    response.setRequests(null);
                    return response;
                })
                .collect(Collectors.toList());

        ApiRepositoriesResponse response = new ApiRepositoriesResponse();
      //  response.setRepositories(apiRepositoryResponseList);
        return response;
    }





}



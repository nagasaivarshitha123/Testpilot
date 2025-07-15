package com.innocito.testpilot.service;

import com.innocito.testpilot.entity.ApiRepository;
import com.innocito.testpilot.entity.Project;
import com.innocito.testpilot.entity.Request;
import com.innocito.testpilot.enums.ActiveStatus;
import com.innocito.testpilot.enums.ProjectType;
import com.innocito.testpilot.exception.ResourceNotFoundException;
import com.innocito.testpilot.exception.ValidationException;
import com.innocito.testpilot.model.*;
import com.innocito.testpilot.repository.ApiRepoRepository;
import com.innocito.testpilot.repository.ProjectRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.time.DateUtils.isSameDay;

@Service
public class ProjectService {
    private final Logger logger = LogManager.getLogger(ProjectService.class);

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private TenantData tenantData;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    ApiRepoRepository apiRepoRepository;

    @Value("${page.size}")
    private int defaultPageSize;


    public ProjectResponse createProject(ProjectRequest projectRequest) {

        Project project = modelMapper.map(projectRequest, Project.class);


        project.setCreatedBy(tenantData.getLoggedInUserName());
        project.setCreationDate(new Date());
        project.setActiveStatus(ActiveStatus.ACTIVE.getDisplayValue());

        Project savedProject = projectRepo.save(project);

        // Map saved entity to response DTO
        ProjectResponse projectResponse = modelMapper.map(savedProject, ProjectResponse.class);

        return projectResponse;
    }

    // they given only this code intially
    public Project getProject(long id) {
        Project project = new Project();
        project.setId(id);
        project.setName("Test");
        project.setDescription("Test");
        project.setProjectType(ProjectType.API_Or_Web_Service.name());
        return project;
    }


    public ProjectResponse updateProject(Long id, ProjectRequest projectRequest) {
        Project existingProject = projectRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("id", "Project not found with id: " + id));

        String currentUser = tenantData.getLoggedInUserName();

        if (!existingProject.getCreatedBy().equals(currentUser)) {
            throw new SecurityException("You are not authorized to update this project");
        }


        modelMapper.map(projectRequest, existingProject);

        existingProject.setUpdatedBy(currentUser);
        existingProject.setUpdationDate(new Date());
        existingProject.setActiveStatus(ActiveStatus.ACTIVE.getDisplayValue());


        Project savedProject = projectRepo.save(existingProject);

        return modelMapper.map(savedProject, ProjectResponse.class);
    }


    public Project findProject(long id, int activeStatus) {
        return projectRepo.findByIdAndActiveStatus(id, activeStatus).orElseThrow(() ->
                new ResourceNotFoundException("id", "Project not found with id : " + id));
    }


    @Transactional
    public void deleteProject(long id) {
        Project project = findProject(id, ActiveStatus.ACTIVE.getDisplayValue());

        String currentUser = tenantData.getLoggedInUserName();

        if (!project.getCreatedBy().equals(currentUser)) {
            throw new SecurityException("You are not authorized to delete this project");
        }

        project.setActiveStatus(ActiveStatus.INACTIVE.getDisplayValue());
        project.setUpdatedBy(currentUser);
        project.setUpdationDate(new Date());

        projectRepo.save(project);
    }


    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepo.findByIdAndActiveStatus(id, ActiveStatus.ACTIVE.getDisplayValue())
                .orElseThrow(() -> new ResourceNotFoundException("id", "Project not found with id: " + id));

        return modelMapper.map(project, ProjectResponse.class);
    }


    //get all projects with repos and requests.
    //correct code

    public ProjectResponseDetails getProjectAndRepoById(Long id) {
        Project project = projectRepo.findByIdAndActiveStatus(id, ActiveStatus.ACTIVE.getDisplayValue())
                .orElseThrow(() -> new ResourceNotFoundException("message", "Project not found with id: " + id));

        ProjectResponseDetails projectResponse = modelMapper.map(project, ProjectResponseDetails.class);

        List<ApiRepositoryResponse> apiRepoResponses = project.getApiRepositories().stream()
                .filter(repo -> repo.getActiveStatus() == ActiveStatus.ACTIVE.getDisplayValue())
                .map(repo -> {
                    ApiRepositoryResponse repoResp = modelMapper.map(repo, ApiRepositoryResponse.class);


                    List<RequestOutput> activeRequests = repo.getRequests().stream()
                            .filter(request -> request.getActiveStatus() == ActiveStatus.ACTIVE.getDisplayValue())
                            .map(request -> modelMapper.map(request, RequestOutput.class))
                            .collect(Collectors.toList());

                    repoResp.setRequests(activeRequests);
                    return repoResp;
                })
                .collect(Collectors.toList());

        projectResponse.setApiRepositories(apiRepoResponses);

        return projectResponse;
    }


//// will get all the projects display withput passing anything,   GET ALL CREATED PROJECTS.
//
//    public List<ProjectResponse> getAllActiveProjects() {
//        List<Project> projects = projectRepo.findAllByActiveStatus(ActiveStatus.ACTIVE.getDisplayValue());
//
//        return projects.stream()
//                .map(project -> modelMapper.map(project, ProjectResponse.class))
//                .collect(Collectors.toList());
//    }


//   GET PROJECTS OF LOGGED IN USER ONLY.
public List<ProjectResponse> getAllActiveProjects() {
    // Get the username of the logged-in user
    String username = tenantData.getLoggedInUserName();

    // Fetch projects by active status and createdBy
    List<Project> projects = projectRepo.findAllByActiveStatusAndCreatedBy(
            ActiveStatus.ACTIVE.getDisplayValue(), username
    );
    System.out.println("Logged-in user: " + username);
    return projects.stream()
            .map(project -> modelMapper.map(project, ProjectResponse.class))
            .collect(Collectors.toList());
}



    //repo and requests search

    private boolean matchesKeyword(ApiRepository repo, String keyword) {
        if (containsIgnoreCase(repo.getName(), keyword) ||
                containsIgnoreCase(repo.getDescription(), keyword) ||
                containsIgnoreCase(repo.getCreatedBy(), keyword) ||
                containsIgnoreCase(dateToString(repo.getCreationDate()), keyword) ||
                containsIgnoreCase(dateToString(repo.getUpdationDate()), keyword)) {
            return true;
        }

        if (repo.getRequests() != null) {
            for (Request req : repo.getRequests()) {
                if (req.getActiveStatus() == ActiveStatus.ACTIVE.getDisplayValue() &&
                        matchesKeyword(req, keyword)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matchesKeyword(Request req, String keyword) {
        return containsIgnoreCase(req.getName(), keyword) ||
                containsIgnoreCase(req.getDescription(), keyword) ||
                containsIgnoreCase(req.getMethod(), keyword) ||
                containsIgnoreCase(req.getEndpointUrl(), keyword) ||
                containsIgnoreCase(req.getCreatedBy(), keyword) ||
                containsIgnoreCase(req.getUpdatedBy(), keyword) ||
                containsIgnoreCase(dateToString(req.getCreationDate()), keyword) ||
                containsIgnoreCase(dateToString(req.getUpdationDate()), keyword);
    }

    private boolean containsIgnoreCase(String field, String keyword) {
        return field != null && field.toLowerCase().contains(keyword);
    }

    private String dateToString(Object date) {
        return date != null ? date.toString().toLowerCase() : "";
    }


    public PageResponseModel<List<ApiRepositoryResponse>> getProjectAndRepoById(
            Long id, Integer pageNo, Integer pageSize, String searchText) {

        Project project = projectRepo.findByIdAndActiveStatus(id, ActiveStatus.ACTIVE.getDisplayValue())
                .orElseThrow(() -> new ResourceNotFoundException("id", "Project not found with id: " + id));

        if (pageNo == null || pageNo < 1) pageNo = 1;
        if (pageSize == null || pageSize < 1) pageSize = defaultPageSize;

        String keyword = StringUtils.isBlank(searchText) ? null : searchText.toLowerCase().trim();

        List<ApiRepository> allActiveRepos = project.getApiRepositories().stream()
                .filter(repo -> repo.getActiveStatus() == ActiveStatus.ACTIVE.getDisplayValue())
                .collect(Collectors.toList());

        // Global search across repository and request fields
        List<ApiRepository> matchedRepos = allActiveRepos.stream()
                .filter(repo -> keyword == null || matchesKeyword(repo, keyword))
                .collect(Collectors.toList());


        int totalElements = matchedRepos.size();
        int totalPages = (totalElements + pageSize - 1) / pageSize;

        //  Add validation for out-of-bound page number
        if (pageNo > totalPages && totalPages > 0) {
            throw new ResourceNotFoundException("message", "The page number requested is beyond the total number of available pages ");
        }
        final int MAX_PAGE_SIZE = 50;
        if (pageSize > MAX_PAGE_SIZE) {
            throw new ResourceNotFoundException("message", "Page size requested exceeds maximum allowed limit of " + MAX_PAGE_SIZE);
        }


        int start = Math.min((pageNo - 1) * pageSize, matchedRepos.size());
        int end = Math.min(start + pageSize, matchedRepos.size());
        List<ApiRepository> pageRepos = matchedRepos.subList(start, end);

        List<ApiRepositoryResponse> repoResponses = pageRepos.stream()
                .map(repo -> {
                    ApiRepositoryResponse dto = modelMapper.map(repo, ApiRepositoryResponse.class);

                    List<RequestOutput> activeRequests = repo.getRequests().stream()
                            .filter(req -> req.getActiveStatus() == ActiveStatus.ACTIVE.getDisplayValue())
                            .filter(req -> keyword == null || matchesKeyword(req, keyword))
                            .filter(req -> req.getProjectId().equals(project.getId()))

                            .map(req -> modelMapper.map(req, RequestOutput.class))
                            .collect(Collectors.toList());

                    dto.setRequests(activeRequests);
                    return dto;
                })
                .collect(Collectors.toList());

        PageResponseModel<List<ApiRepositoryResponse>> pageModel = new PageResponseModel<>();
        pageModel.setPageNo(pageNo);
        pageModel.setRequestedPageSize(pageSize);
        pageModel.setTotalElements((long) matchedRepos.size());
        pageModel.setTotalPages((matchedRepos.size() + pageSize - 1) / pageSize);
        pageModel.setCurrentPageSize(repoResponses.size());
        pageModel.setItems(repoResponses);
        pageModel.setMessage("Request completed successfully");

        return pageModel;
    }


    //project search implementation
    private boolean containsignoreCase(String field, String keyword) {
        return field != null && keyword != null && field.toLowerCase().contains(keyword.toLowerCase());
    }

    private boolean matchesKeyword(Project project, String keyword) {
        return containsignoreCase(project.getName(), keyword) ||
                containsignoreCase(project.getProjectType(), keyword) ||
                containsignoreCase(project.getDescription(), keyword) ||
                containsignoreCase(project.getCreatedBy(), keyword) ||
                containsignoreCase(dateToString(project.getCreationDate()), keyword) ||
                containsignoreCase(dateToString(project.getUpdationDate()), keyword);
    }


private Date tryParseDate(String text) {
    if (text == null || text.isBlank()) return null;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    sdf.setLenient(false);  // strict parsing
    try {
        return sdf.parse(text);
    } catch (ParseException e) {
        return null;
    }
}


    public PageResponseModel<List<ProjectResponse>> searchProjects(
            String searchText, Integer pageNo, Integer pageSize) {

        if (pageNo == null || pageNo < 1) pageNo = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        final int MAX_PAGE_SIZE = 20;
        if (pageSize > MAX_PAGE_SIZE) {
            throw new ResourceNotFoundException("message", "Page size requested exceeds maximum allowed limit of " + MAX_PAGE_SIZE);
        }

        String keyword = StringUtils.isBlank(searchText) ? null : searchText.toLowerCase().trim();

        //  Get all active projects
        List<Project> allProjects = projectRepo.findAllByActiveStatus(ActiveStatus.ACTIVE.getDisplayValue());

        Date searchDate = tryParseDate(searchText);

        if (searchDate != null) {
            Date now = new Date();

            // Get earliest creation date from projects, filtering out nulls
            Optional<Date> earliestCreationDateOpt = allProjects.stream()
                    .map(Project::getCreationDate)
                    .filter(Objects::nonNull)
                    .min(Date::compareTo);

            if (earliestCreationDateOpt.isPresent()) {
                Date earliestCreationDate = earliestCreationDateOpt.get();
                if (searchDate.before(earliestCreationDate)) {
                    throw new ValidationException(
                            Collections.singletonMap("date", "Entered date is before any project creation date"));
                }
            }

            if (searchDate.after(now)) {
                throw new ValidationException(
                        Collections.singletonMap("date", "Entered date is in the future, please enter a valid date"));
            }
        }


        //  Filter by keyword if provided
        List<Project> matchedProjects = allProjects.stream()
                .filter(project -> keyword == null || matchesKeyword(project, keyword))
                .collect(Collectors.toList());

        int totalElements = matchedProjects.size();


        if (totalElements == 0) {
            PageResponseModel<List<ProjectResponse>> emptyPage = new PageResponseModel<>();
            emptyPage.setPageNo(pageNo);
            emptyPage.setRequestedPageSize(pageSize);
            emptyPage.setTotalElements(0L);
            emptyPage.setTotalPages(0);
            emptyPage.setCurrentPageSize(0);
            emptyPage.setItems(Collections.emptyList());
            emptyPage.setMessage("Input entered is not matched with the data");
            return emptyPage;
        }

        // Step 3: Pagination logic
        int totalPages = (totalElements + pageSize - 1) / pageSize;

        if (pageNo > totalPages && totalPages > 0) {
            throw new ResourceNotFoundException("message", "The page number requested is beyond the total number of available pages");
        }

        int start = Math.min((pageNo - 1) * pageSize, matchedProjects.size());
        int end = Math.min(start + pageSize, matchedProjects.size());
        List<Project> pageProjects = matchedProjects.subList(start, end);

        // Step 4: Map to response DTO
        List<ProjectResponse> projectResponses = pageProjects.stream()
                .map(project -> modelMapper.map(project, ProjectResponse.class))
                .collect(Collectors.toList());


        PageResponseModel<List<ProjectResponse>> pageModel = new PageResponseModel<>();
        pageModel.setPageNo(pageNo);
        pageModel.setRequestedPageSize(pageSize);
        pageModel.setTotalElements((long) totalElements);
        pageModel.setTotalPages(totalPages);
        pageModel.setCurrentPageSize(projectResponses.size());
        pageModel.setItems(projectResponses);
        pageModel.setMessage("Request completed successfully");

        return pageModel;
    }

}






































//
//    // pagination and search  also gives the
//
//public ProjectResponseDetails getProjectAndRepoById(
//        Long id, Integer pageNo, Integer pageSize, String searchText) {
//
//    Project project = projectRepo.findByIdAndActiveStatus(id, ActiveStatus.ACTIVE.getDisplayValue())
//            .orElseThrow(() -> new ResourceNotFoundException("id", "Project not found with id: " + id));
//
//    ProjectResponseDetails projectResponse = modelMapper.map(project, ProjectResponseDetails.class);
//
//    if (pageNo == null || pageNo < 1) pageNo = 1;
//    if (pageSize == null || pageSize < 1) pageSize = defaultPageSize;
//
//    String keyword = StringUtils.isBlank(searchText) ? null : searchText.toLowerCase().trim();
//
//    // 1. Filter active repositories and perform unified search
//    List<ApiRepository> allActiveRepos = project.getApiRepositories().stream()
//            .filter(repo -> repo.getActiveStatus() == 1)
//            .collect(Collectors.toList());
//
//    List<ApiRepository> matchedRepos = allActiveRepos.stream()
//            .filter(repo -> {
//                if (keyword == null) return true;
//
//                boolean repoMatches = (repo.getName() != null && repo.getName().toLowerCase().contains(keyword))
//                        || (repo.getDescription() != null && repo.getDescription().toLowerCase().contains(keyword));
//
//                boolean anyRequestMatches = repo.getRequests().stream()
//                        .anyMatch(req -> req.getActiveStatus() == 1
//                                && req.getName() != null
//                                && req.getName().toLowerCase().contains(keyword));
//
//                return repoMatches || anyRequestMatches;
//            })
//            .collect(Collectors.toList());
//
//    // 2. Pagination
//    int start = Math.min((pageNo - 1) * pageSize, matchedRepos.size());
//    int end = Math.min(start + pageSize, matchedRepos.size());
//    List<ApiRepository> pageRepos = matchedRepos.subList(start, end);
//
//    // 3. Map to DTOs with filtered active requests
//    List<ApiRepositoryResponse> repoResponses = pageRepos.stream()
//            .map(repo -> {
//                ApiRepositoryResponse dto = modelMapper.map(repo, ApiRepositoryResponse.class);
//                List<RequestOutput> activeReqs = repo.getRequests().stream()
//                        .filter(req -> req.getActiveStatus() == 1
//                                && (keyword == null || req.getName().toLowerCase().contains(keyword)))
//                        .map(req -> modelMapper.map(req, RequestOutput.class))
//                        .collect(Collectors.toList());
//
//                dto.setRequests(activeReqs);
//                return dto;
//            })
//            .collect(Collectors.toList());
//
//    // 4. Build pagination metadata
//    PageResponseModel<List<ApiRepositoryResponse>> pageModel = new PageResponseModel<>();
//    pageModel.setPageNo(pageNo);
//    pageModel.setRequestedPageSize(pageSize);
//    pageModel.setTotalElements((long) matchedRepos.size());
//    pageModel.setTotalPages((matchedRepos.size() + pageSize - 1) / pageSize);
//    pageModel.setCurrentPageSize(repoResponses.size());
//    pageModel.setItems(repoResponses);
//
//   // projectResponse.setPaginatedApiRepositories(pageModel);
//    projectResponse.setApiRepositories(repoResponses);  // Optionally mirror items here
//
//    return projectResponse;
//}
//private boolean matchesKeyword(ApiRepository repo, String keyword) {
//    if (containsIgnoreCase(repo.getName(), keyword) ||
//            containsIgnoreCase(repo.getDescription(), keyword) ||
//            containsIgnoreCase(repo.getCreatedBy(), keyword)
//    ) {
//        return true;
//    }
//
//    if (repo.getRequests() != null) {
//        for (Request req : repo.getRequests()) {
//            if (req.getActiveStatus() == ActiveStatus.ACTIVE.getDisplayValue() &&
//                    matchesKeyword(req, keyword)) {
//                return true;
//            }
//        }
//    }
//
//    return false;
//}
//
//    private boolean matchesKeyword(Request req, String keyword) {
//        return containsIgnoreCase(req.getName(), keyword) ||
//                containsIgnoreCase(req.getDescription(), keyword) ||
//                containsIgnoreCase(req.getMethod(), keyword) ||
//                containsIgnoreCase(req.getEndpointUrl(), keyword) ||
//
//                containsIgnoreCase(req.getCreatedBy(), keyword) ||
//                containsIgnoreCase(req.getUpdatedBy(), keyword);
//    }
//
//    private boolean containsIgnoreCase(String field, String keyword) {
//        return field != null && field.toLowerCase().contains(keyword);
//    }


//repo and reqts data for (get request).


/*public ProjectResponseDetails getProjectAndRepoById(Long id) {
    Project project = projectRepo.findByIdAndActiveStatus(id, ActiveStatus.ACTIVE.getDisplayValue())
            .orElseThrow(() -> new ResourceNotFoundException("id", "Project not found with id: " + id));

    ProjectResponseDetails projectResponse = modelMapper.map(project, ProjectResponseDetails.class);

    List<ApiRepositoryResponse> apiRepoResponses = project.getApiRepositories().stream()
            .filter(repo -> repo.getActiveStatus() == ActiveStatus.ACTIVE.getDisplayValue())
            .map(repo -> {
                ApiRepositoryResponse repoResp = modelMapper.map(repo, ApiRepositoryResponse.class);

                // Just filter existing requests — no type changes
                if (repoResp.getRequests() != null) {
                    repoResp.getRequests().removeIf(req -> !req.getProjectId().equals(project.getId()));
                }

                return repoResp;
            })

            .collect(Collectors.toList());

    projectResponse.setApiRepositories(apiRepoResponses);

    return projectResponse;
}
*/

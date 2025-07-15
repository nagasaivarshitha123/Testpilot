package com.innocito.testpilot.controller;

import com.innocito.testpilot.model.*;
import com.innocito.testpilot.service.ProjectService;
import com.innocito.testpilot.util.BasicUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final Logger logger = LogManager.getLogger(ProjectController.class);
    private final ProjectService projectService;
    @Autowired
    private BasicUtils basicUtils;


    @PostMapping("/create")
    public ResponseEntity<ResponseModel<ProjectResponse>> addProject(@Valid @RequestBody ProjectRequest projectRequest) {
        ProjectResponse projectResponse = projectService.createProject(projectRequest);

        ResponseModel<ProjectResponse> response = ResponseModel.<ProjectResponse>builder()
                .data(projectResponse)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ProjectResponse>> update(@PathVariable Long id, @Valid @RequestBody ProjectRequest projectRequest) {

        ProjectResponse projectResponse = projectService.updateProject(id, projectRequest);

        ResponseModel<ProjectResponse> response = ResponseModel.<ProjectResponse>builder()
                .data(projectResponse)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        ResponseModel<String> response = ResponseModel.<String>builder()
                .data(null)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ProjectResponse>> getProjectById(@PathVariable Long id) {
        ProjectResponse projectResponse = projectService.getProjectById(id);

        ResponseModel<ProjectResponse> response = ResponseModel.<ProjectResponse>builder()
                .data(projectResponse)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //projects with repo and requests it will get when we give project id
    @GetMapping("/repo/{id}")
    public ResponseEntity<ResponseModel<ProjectResponseDetails>> getProjectAndRepoById(@PathVariable Long id) {
        ProjectResponseDetails projectResponses = projectService.getProjectAndRepoById(id);

        ResponseModel<ProjectResponseDetails> response = ResponseModel.<ProjectResponseDetails>builder()
                .data(projectResponses)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ResponseModel<List<ProjectResponse>>> getAllActiveProjects() {
        List<ProjectResponse> projects = projectService.getAllActiveProjects();

        ResponseModel<List<ProjectResponse>> response = ResponseModel.<List<ProjectResponse>>builder()
                .data(projects)
                .message(basicUtils.getLocalizedMessage("successful", null))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //project search

    @GetMapping("/search")
    public ResponseEntity<PageResponseModel<List<ProjectResponse>>> searchProjects(
            @RequestParam(defaultValue = "", required = false) String searchText,
            @RequestParam(defaultValue = "1", required = false) Integer pageNo,
            @RequestParam(defaultValue = "20", required = false) Integer pageSize) {

        PageResponseModel<List<ProjectResponse>> response = projectService.searchProjects(searchText, pageNo, pageSize);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/reposearch/{id}")

//    // it only get the paginated values of repo and requests with search
    public ResponseEntity<PageResponseModel<List<ApiRepositoryResponse>>> getPaginatedRepositoriesByProjectId(
            @PathVariable Long id,
            @RequestParam(required = false,defaultValue = "1" ) Integer pageNo,
            @RequestParam(required = false,defaultValue = "20") Integer pageSize,
            @RequestParam(required = false, defaultValue = "") String searchText) {

        PageResponseModel<List<ApiRepositoryResponse>> response =
                projectService.getProjectAndRepoById(id, pageNo, pageSize, searchText);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // this will get the project with repos and below repo and req with pagination & search
//    public ResponseEntity<ResponseModel<ProjectResponseDetails>> getProjectAndRepoById(
//            @PathVariable Long id,
//            @RequestParam(required = false) Integer pageNo,
//            @RequestParam(required = false) Integer pageSize,
//            @RequestParam(required = false) String searchText) {
//
//
//        ProjectResponseDetails projectResponses = projectService.getProjectAndRepoById(id, pageNo, pageSize, searchText);
//
//        ResponseModel<ProjectResponseDetails> response = ResponseModel.<ProjectResponseDetails>builder()
//                .data(projectResponses)
//                .message(basicUtils.getLocalizedMessage("successful", null))
//                .build();
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

 



}

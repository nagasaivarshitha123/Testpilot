package com.innocito.testpilot.service;

import com.innocito.testpilot.enums.*;
import com.innocito.testpilot.model.MasterData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class MasterDataService {
    private final Logger logger = LogManager.getLogger(MasterDataService.class);

    public MasterData getMasterData() {
        MasterData masterData = new MasterData();
        masterData.setHttpMethods(HttpMethod.getDisplayValues());
        masterData.setAuthorizationTypes(AuthorizationType.getDisplayValues());
        masterData.setRepositoryTypes(RepositoryType.getDisplayValues());
        masterData.setProjectTypes(ProjectType.getDisplayValues());
        masterData.setContentTypes(ContentType.getDisplayValues());
        masterData.setAssertionTypes(AssertionType.getDisplayValues());
        masterData.setAssertionOperations(AssertionOperation.getDisplayValues());
        masterData.setReportStatus(ReportStatus.getDisplayValues());
        return masterData;
    }
}
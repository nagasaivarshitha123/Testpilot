package com.innocito.testpilot.controller;

import com.innocito.testpilot.model.MasterData;
import com.innocito.testpilot.model.ResponseModel;
import com.innocito.testpilot.service.MasterDataService;
import com.innocito.testpilot.util.BasicUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/masterData")
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class MasterDataController {

    private final Logger logger = LogManager.getLogger(MasterDataController.class);

    @Autowired
    private MasterDataService masterDataService;

    @Autowired
    private BasicUtils basicUtils;

    @GetMapping
    public ResponseEntity<ResponseModel<MasterData>> masterData() {
        MasterData masterData = masterDataService.getMasterData();
        ResponseModel response = ResponseModel.<MasterData>builder()
                .data(masterData)
                .message(basicUtils.getLocalizedMessage("master.data.fetch.successful", null))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material";
import { ApiRepositoryService } from "src/app/services/api-repository.service";
import { SharedService } from "src/app/services/shared.service";
import { TestCaseService } from "src/app/services/test-case.service";
import { CreateTestCaseType } from "typings";

@Component({
  selector: "app-add-new-test-case",
  templateUrl: "./add-new-test-case.component.html",
  styleUrls: ["./add-new-test-case.component.scss"],
})
export class AddNewTestCaseComponent implements OnInit {
  public repositoryData: any = [];

  // testcase edit
  public isTestCaseEdit: Boolean = false;
  public testCaseEditData: any = "";

  public apiType: Object[] = [
    { id: 1, content: "Rest" },
    { id: 2, content: "Soap" },
  ];
  public selectedApiType: string = "Rest";
  public selectedApiTypeData: any[] = [];
  public selectedTypeRequestData: any[] = [];
  public selectedRequestData: any[] = [];
  public selectedRequest: any[] = [];
  public selectedOption: string = "";

  public isProps: Boolean = false;
  public selectedRepoProps: any;
  public selectedRequestProps: any;

  CreateTestCase: CreateTestCaseType = {
    description: "",
    name: "",
    projectId: 1,
    requestId: "",
  };

  constructor(
    private caseService: TestCaseService,
    private apiRepoService: ApiRepositoryService,
    private dialog: MatDialog,
    private sharedService: SharedService
  ) {
    this.apiRepoService.getAllRequests(1).subscribe((data: any) => {
      // this.seasons = data.data;
    });
  }

  ngOnInit() {
    if (!this.isProps) {
      this.getAllRepos();
    } else {
      this.selectedApiTypeData = this.selectedRepoProps;
      this.setRequestId(this.selectedRequestProps);
      this.selectedOption = this.selectedRequestProps.name;
    }

    if (this.testCaseEditData) {
      this.CreateTestCase.name = this.testCaseEditData.name;
      this.CreateTestCase.description = this.testCaseEditData.description;
    }
  }

  getAllRepos() {
    this.apiRepoService.getAllRepositories(1).subscribe((data: any) => {
      this.repositoryData = data.data;
      this.selectedApiTypeData = this.repositoryData.rest;
    });
  }

  data(selectedRepoProps) {}

  saveTestCase() {
    const dialogRef = this.dialog;
    this.caseService
      .createTestCase(this.CreateTestCase)
      .subscribe((response) => {
        dialogRef.closeAll();
      });
  }

  updateTestCase() {
    const dialogRef = this.dialog;
    const body = {
      name: this.CreateTestCase.name,
      description: this.CreateTestCase.description,
    };
    this.caseService
      .editTestCase(body, this.testCaseEditData.id)
      .subscribe((data) => {
        this.sharedService.editTestcaseSubjectData.next(data);
        dialogRef.closeAll();
      });
  }

  setApiRepoType(type: string) {
    if (type === "Rest") {
      this.selectedApiTypeData = this.repositoryData.rest;
    } else if (type === "Soap") {
      this.selectedApiTypeData = this.repositoryData.soap;
    }
  }

  setRequestId(request: any) {
    const { id } = request;
    this.CreateTestCase.requestId = id;
  }

  ngOnDestroy() {
    this.isProps = false;
  }
}

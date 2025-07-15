import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material";
import { ApiRepositoryService } from "src/app/services/api-repository.service";
import { SharedService } from "src/app/services/shared.service";
import { TestCaseService } from "src/app/services/test-case.service";
import { assertionsType, createRepository, createRequest } from "typings";
import { EsDialogboxComponent } from "../es-dialogbox/es-dialogbox.component";

export interface Selector {
  viewValue: string;
}

@Component({
  selector: "app-customdialogbox",
  templateUrl: "./customdialogbox.component.html",
  styleUrls: ["./customdialogbox.component.scss"],
})
export class CustomdialogboxComponent implements OnInit {
  public createRepositoryType: createRepository = {
    name: "",
    description: "",
    repositoryType: "REST",
    repositoryUrl: "",
    projectId: 1,
  };

  public createRequestType: createRequest = {
    name: "",
    description: "",
    requestType: "REST",
    requestUrl: "",
    projectId: 1,
    requestId: 0,
    requestMethod: "GET",
  };

  public updateAssertions: assertionsType = {
    assertionType: "Status Code",
    path: "",
    comparison: "Equals",
    value: "",
  };

  public name: string = "";

  // assertions
  public isAssertions: Boolean = false;
  public isAssertionUpdate: Boolean = false;
  public assertionId: number | string = "";
  public assertionsList: any[] = [];
  public specificAssertions: any = {};

  // requests
  public isRequest: Boolean = false;
  public isEdit: Boolean = false;
  public requestData: any = "";
  // edit request
  public isRequestEdit: Boolean = false;
  public requestEditId: number | string = "";
  public requestEditName: string = "";
  public requestEditDescription: string = "";

  // REPO
  public isRepoEdit: Boolean = false;
  public repoData: any = "";
  public updateRepoId: number | string = "";

  // filters
  public isFilters: Boolean = false;

  repos: Selector[] = [{ viewValue: "REST" }, { viewValue: "SOAP" }];
  methods: Selector[] = [
    { viewValue: "GET" },
    { viewValue: "POST" },
    { viewValue: "DELETE" },
    { viewValue: "PUT" },
  ];

  assertions: Selector[] = [
    { viewValue: "Status Code" },
    { viewValue: "JSON Path" },
  ];

  comparisons: Selector[] = [
    { viewValue: "Equals" },
    { viewValue: "Not Equals" },
    { viewValue: "Less than" },
    { viewValue: "Greater than" },
  ];

  comparisonsJSON: Selector[] = [
    { viewValue: "Equals" },
    { viewValue: "Not Equals" },
    { viewValue: "Less than" },
    { viewValue: "Greater than" },
    { viewValue: "Contains" },
    { viewValue: "Not Contains" },
  ];

  constructor(
    public dialog: MatDialog,
    private apiService: ApiRepositoryService,
    private caseService: TestCaseService,
    private shared: SharedService
  ) {}

  ngOnInit() {
    if (this.isAssertionUpdate) {
      this.updateAssertions.assertionType =
        this.specificAssertions.assertionType;
      this.updateAssertions.comparison = this.specificAssertions.comparison;
      this.updateAssertions.path = this.specificAssertions.path;
      this.updateAssertions.value = this.specificAssertions.value;
    }

    if (this.requestData) {
      this.createRequestType.name = this.requestData.name;
      this.createRequestType.description = this.requestData.description;
      this.createRequestType.requestType = this.requestData.repositoryType;
      this.createRequestType.requestUrl = this.requestData.endpointUrl;
      this.createRequestType.requestId = this.requestData.id;
      this.createRequestType.projectId = this.requestData.projectId;
    }

    if (this.repoData) {
      this.createRepositoryType.name = this.repoData.name;
      this.createRepositoryType.description = this.repoData.description;
      this.createRepositoryType.repositoryType = this.repoData.repositoryType;
      this.createRepositoryType.repositoryUrl = this.repoData.repositoryUrl;
      this.createRepositoryType.projectId = this.repoData.projectId;
    }
  }

  handleCreate() {
    this.apiService
      .createRepository(this.createRepositoryType)
      .subscribe((data: any) => {
        const { message } = data;
        this.openDialog(message);
      });
  }

  handleCreateRequest() {
    const body = {
      repositoryId: this.updateRepoId,
      name: this.createRequestType.name,
      url: this.createRequestType.requestUrl,
      description: this.createRequestType.description,
      projectId: this.createRepositoryType.projectId,
      method: this.createRequestType.requestMethod,
    };
    this.apiService.addNewRequestinRepo(body).subscribe((data: any) => {
      this.dialog.closeAll();
    });
  }

  handleEditRequest() {
    const id = this.requestEditId;

    const body = {
      name: this.requestEditName,
      description: this.requestEditDescription,
    };

    this.apiService.updateRequest(body, id).subscribe((data: any) => {
      this.shared.updateRequestSubjectData.next(data);
      this.dialog.closeAll();
    });
  }

  updateRepo() {
    const body = {
      name: this.createRepositoryType.name,
      description: this.createRepositoryType.description,
    };
    this.apiService
      .updateRepo(body, this.updateRepoId)
      .subscribe((response) => {
        this.dialog.closeAll();
      });
  }

  openDialog(message: string) {
    const dialogRef = this.dialog.open(EsDialogboxComponent, {
      width: "300px",
    });
    dialogRef.componentInstance.success = message;

    dialogRef.afterClosed().subscribe((result) => {});
  }

  updateTestCaseAssertions() {
    const dialogRef = this.dialog;
    if (this.updateAssertions.assertionType === "Status Code") {
      if (!this.updateAssertions.value) {
        this.caseService.errorMessage("Please enter value");
        return;
      }
    } else {
      if (!this.updateAssertions.path) {
        this.caseService.errorMessage("Please enter path");
        return;
      } else if (!this.updateAssertions.value) {
        this.caseService.errorMessage("Please enter value");
        return;
      }
    }

    if (this.isAssertionUpdate) {
      const { id } = this.specificAssertions;
      this.assertionsList = this.assertionsList.filter((assertID) => {
        return assertID.id !== id;
      });

      this.updateAssertions.id = id;
    }
    this.assertionsList.push(this.updateAssertions);

    this.caseService
      .updateAssertions(
        this.assertionId,
        JSON.stringify({ assertions: this.assertionsList })
      )
      .subscribe((data: any) => {
        dialogRef.closeAll();
        const { assertions } = data.data;
        this.shared.subject.next(assertions);
        return assertions;
      });
  }

  ngOnDestroy() {
    this.isAssertions = false;
    this.isAssertionUpdate = false;
    this.assertionId = "";
    this.assertionsList = [];
    this.specificAssertions = {};
  }
}

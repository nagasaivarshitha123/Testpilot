import { Component, OnInit, ViewEncapsulation } from "@angular/core";
import { MatDialog } from "@angular/material";
import { Router } from "@angular/router";
import { ApiRepositoryService } from "src/app/services/api-repository.service";
import { SharedService } from "src/app/services/shared.service";
import { DropDown } from "typings";
import { AddNewTestCaseComponent } from "../add-new-test-case/add-new-test-case.component";
import { Location } from "@angular/common";

@Component({
  selector: "app-test-case-board",
  templateUrl: "./test-case-board.component.html",
  styleUrls: ["./test-case-board.component.scss"],
  encapsulation: ViewEncapsulation.None,
})
export class TestCaseBoardComponent implements OnInit {
  requestId: any = {};
  repoId: any = {};

  public JSONFORMAT: any;
  public isLoading: Boolean = false;
  private paramId: string | number = "";
  public tabName: string = "";
  public repoName: string = "";
  public requestName: string = "";

  public runProps: any = {
    endpointUrl: "",
    method: "",
    name: "",
    description: "",
  };

  rawHeaders = [];
  public jsonHeaders: any = {};

  methods: DropDown[] = [
    { value: "GET", viewValue: "GET" },
    { value: "POST", viewValue: "POST" },
    { value: "PUT", viewValue: "PUT" },
    { value: "DELETE", viewValue: "DELETE" },
  ];

  isRun: boolean = false;
  testCaseParams: any = "";

  constructor(
    private router: Router,
    private shared: SharedService,
    public dialog: MatDialog,
    private apiRepoService: ApiRepositoryService,
    private _location: Location
  ) {
    this.testCaseParams =
      this.router.getCurrentNavigation().extras.queryParams.requestParams;
    const testCaseRun =
      this.router.getCurrentNavigation().extras.queryParams.isRun;
    this.requestId = this.testCaseParams;
    this.isRun = testCaseRun;

    this.runProps.endpointUrl = this.requestId.endpointUrl;
    this.runProps.name = this.requestId.name;
    this.runProps.description = this.requestId.description;
    this.runProps.method = this.requestId.method;

    this.paramId = this.requestId.id;
    this.tabName = this.requestId.name;
    this.repoName = this.requestId.repositoryName;
    this.requestName = this.requestId.requestName;
    this.JSONFORMAT = false;
  }

  ngOnInit() {
    this.shared.loadingSubject$.subscribe((data) => {
      this.isLoading = false;
      this.JSONFORMAT = false;
    });

    if (this.isRun) {
      this.runTestCase();
    }

    this.shared.editTestcaseSubjectData$.subscribe((data: any) => {
      if (data) {
        this.tabName = data.data.name;
        this.requestId.name = data.data.name;
        this.requestId.description = data.data.description;

        this.testCaseParams.name = data.data.name;
        this.testCaseParams.description = data.data.description;
        this.runProps.name = data.data.name;
      }
    });
  }

  editTestCaseData() {
    const dialogRef = this.dialog.open(AddNewTestCaseComponent, {
      width: "500px",
      height: "auto",
    });

    dialogRef.componentInstance.isTestCaseEdit = true;
    dialogRef.componentInstance.testCaseEditData = this.testCaseParams;

    dialogRef.afterClosed().subscribe((result) => {});
  }

  ngOnChanges() {
    this.runProps.endpointUrl = this.requestId.endpointUrl;
    this.runProps.name = this.requestId.name;
    this.runProps.description = this.requestId.description;
    this.runProps.method = this.requestId.method;
    this.runProps.body = this.requestId.body;

    this.paramId = this.requestId.id;
    this.tabName = this.requestId.name;
  }

  runTestCase() {
    const body = {
      endpointUrl: this.runProps.endpointUrl.trim(),
      method: this.runProps.method.trim(),
      name: this.runProps.name.trim(),
      description: this.runProps.description.trim(),
    };

    if (!this.runProps.method || !this.runProps.endpointUrl) {
      this.isLoading = false;
      return;
    }
    this.isLoading = true;

    this.apiRepoService
      .runTestCase(body, this.paramId)
      .subscribe((data: any) => {
        this.isLoading = false;
        this.JSONFORMAT = data;
      });
  }

  openNewTestCasePopUp() {
    if (this.requestId) {
      const dialogRef = this.dialog.open(AddNewTestCaseComponent, {
        width: "500px",
      });

      dialogRef.componentInstance.selectedRepoProps = this.repoId;
      dialogRef.componentInstance.isProps = true;

      dialogRef.afterClosed().subscribe((result) => {});
    }
  }

  goBack() {
    this._location.back();
  }

  ngOnDestroy() {
    this.jsonHeaders = {};
  }
}

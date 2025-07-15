import { Component, OnInit, ViewEncapsulation } from "@angular/core";
import { MatDialog } from "@angular/material";
import { ApiRepositoryService } from "src/app/services/api-repository.service";
import { SharedService } from "src/app/services/shared.service";
import { DropDown } from "typings";
import { AddNewTestCaseComponent } from "../../test-cases/add-new-test-case/add-new-test-case.component";
import { CustomdialogboxComponent } from "src/app/dialogboxes/customdialogbox/customdialogbox.component";

@Component({
  selector: "app-api-board",
  templateUrl: "./api-board.component.html",
  styleUrls: ["./api-board.component.scss"],
  encapsulation: ViewEncapsulation.None,
})
export class ApiBoardComponent implements OnInit {
  requestId: any = {};
  repoId: any = {};

  public JSONFORMAT: any;
  public isLoading: Boolean = false;
  private paramId: string | number = "";
  public tabName: string = "";

  public runProps: any = {
    endpointUrl: "",
    method: "",
    name: "",
    description: "",
  };

  rawHeaders = [];
  public jsonHeaders: any = {};

  public dummy = this.runProps.body;

  methods: DropDown[] = [
    { value: "GET", viewValue: "GET" },
    { value: "POST", viewValue: "POST" },
    { value: "PUT", viewValue: "PUT" },
    { value: "DELETE", viewValue: "DELETE" },
  ];

  constructor(
    private shared: SharedService,
    public dialog: MatDialog,
    private apiRepoService: ApiRepositoryService
  ) {
    this.shared.repositorySubject.subscribe((data: any) => {
      this.shared.repoSubjectData.next(data);
      const { requestId, repoId } = data;
      this.requestId = requestId;
      this.repoId = repoId;

      this.runProps.endpointUrl = this.requestId.endpointUrl;
      this.runProps.name = this.requestId.name;
      this.runProps.description = this.requestId.description;
      this.runProps.method = this.requestId.method;

      this.paramId = this.requestId.id;
      this.tabName = this.requestId.name;
      this.JSONFORMAT = false;
    });
  }

  ngOnInit() {
    this.shared.repoSubjectData$.subscribe((res: any) => {
      if (res !== null) {
        const { requestId, repoId } = res;
        this.requestId = requestId;
        this.repoId = repoId;

        this.runProps.endpointUrl = this.requestId.endpointUrl;
        this.runProps.name = this.requestId.name;
        this.runProps.description = this.requestId.description;
        this.runProps.method = this.requestId.method;
        this.paramId = this.requestId.id;
        this.tabName = this.requestId.name;
        this.JSONFORMAT = false;
      }
    });

    this.shared.loadingSubject$.subscribe((data) => {
      this.isLoading = false;
      this.JSONFORMAT = false;
    });

    this.shared.updateRequestSubjectData$.subscribe((data: any) => {
      if (data) {
        this.runProps.name = data.data.name;
        this.runProps.description = data.data.description;

        this.tabName = data.data.name;
      }
    });

    // this.shared.updateRequestURLSubjectData$.subscribe((data: any) => {
    //   if (data) {
    //     this.runProps.endpointUrl = data.data.endpointUrl;
    //   }
    // });

    // this.shared.updateRequestMethodSubjectData$.subscribe((data: any) => {
    //   if (data) {
    //     this.runProps.method = data.data.method;
    //   }
    // });
  }

  editRequestData() {
    const dialogRef = this.dialog.open(CustomdialogboxComponent, {
      width: "500px",
    });

    dialogRef.componentInstance.isRequestEdit = true;
    dialogRef.componentInstance.requestEditId = this.requestId.id;
    dialogRef.componentInstance.requestEditName = this.requestId.name;
    dialogRef.componentInstance.requestEditDescription =
      this.requestId.description;
  }

  runTasks() {
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

    this.apiRepoService.run(body, this.paramId).subscribe((data: any) => {
      this.isLoading = false;
      this.JSONFORMAT = data;

      if (data.data.success === false) {
        const { errorMessage } = data.data;
      }
    });
  }

  openNewTestCasePopUp() {
    if (this.requestId) {
      const dialogRef = this.dialog.open(AddNewTestCaseComponent, {
        width: "500px",
      });

      dialogRef.componentInstance.selectedRepoProps = this.repoId;
      dialogRef.componentInstance.selectedRequestProps = this.requestId;
      dialogRef.componentInstance.isProps = true;

      dialogRef.afterClosed().subscribe((result) => {});
    }
  }

  ngOnDestroy() {
    this.jsonHeaders = {};
  }
}

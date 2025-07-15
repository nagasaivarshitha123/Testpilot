import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material";
import { ApiRepositoryService } from "src/app/services/api-repository.service";

@Component({
  selector: "app-es-dialogbox",
  templateUrl: "./es-dialogbox.component.html",
  styleUrls: ["./es-dialogbox.component.scss"],
})
export class EsDialogboxComponent implements OnInit {
  public success: string = "";
  public error: any;
  public delete: any;
  public deleteId: any;

  public errorValues: string[];
  public errorMessage: any;
  public errorMessageValues: string[];
  public errorMessageKeys: string[];

  constructor(
    public dialog: MatDialog,
    private apiRepoReq: ApiRepositoryService
  ) {}

  ngOnInit() {
    this.errorValues = Object.values(this.error.errors);
    this.errorMessageValues = Object.values(this.errorMessage);
    this.errorMessageKeys = Object.keys(this.errorMessage);
  }

  closeDialog() {
    this.dialog.closeAll();
  }

  deleteRepo() {
    this.apiRepoReq
      .deleteRepository(this.deleteId)
      .subscribe((data: any) => {});
  }
}

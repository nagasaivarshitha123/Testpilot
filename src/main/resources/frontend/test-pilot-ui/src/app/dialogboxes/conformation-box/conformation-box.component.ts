import { Component, OnInit, ViewEncapsulation } from "@angular/core";
import { MatDialog } from "@angular/material";
import { ApiRepositoryService } from "src/app/services/api-repository.service";
import { SharedService } from "src/app/services/shared.service";

@Component({
  selector: "app-conformation-box",
  templateUrl: "./conformation-box.component.html",
  styleUrls: ["./conformation-box.component.scss"],
})
export class ConformationBoxComponent implements OnInit {
  instance: string = "";
  requestId: number | string = "";
  repoId: number | string = "";
  isRepo: Boolean = false;
  isRequest: Boolean = false;

  constructor(
    private shared: SharedService,
    private dialog: MatDialog,
    private apiRepo: ApiRepositoryService
  ) {}

  ngOnInit() {}

  handleNo() {
    this.shared.deleteSubject.next("No");
  }

  handleYes() {
    const dialogRef = this.dialog;

    if (this.instance === "SIDENAV" && this.isRequest) {
      this.apiRepo.deleteRequest(this.requestId).subscribe((response) => {
        dialogRef.closeAll();
      });
    } else {
      this.apiRepo.deleteRepository(this.repoId).subscribe((response) => {
        dialogRef.closeAll();
      });
    }
  }
}

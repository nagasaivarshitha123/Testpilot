import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material";
import { SharedService } from "src/app/services/shared.service";
import { TestCaseService } from "src/app/services/test-case.service";
import { TestSuitesService } from "src/app/services/test-suites.service";
import { TestSuiteCreate } from "typings";

@Component({
  selector: "app-add-existing-test-cases",
  templateUrl: "./add-existing-test-cases.component.html",
  styleUrls: ["./add-existing-test-cases.component.scss"],
})
export class AddExistingTestCasesComponent implements OnInit {
  NotExistingCases: any = [];
  suiteData: any = [];
  testCaseData: TestSuiteCreate;
  existingLength = 0;

  constructor(
    private testCaseReq: TestCaseService,
    private testSuiteReq: TestSuitesService,
    private shared: SharedService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.testCaseReq.getAllTestCasesWithoutLimit().subscribe((data: any) => {
      this.NotExistingCases = data.items;

      this.existingLength = this.testCaseData.testCases.length;

      for (let i = 0; i < this.testCaseData.testCases.length; i++) {
        this.filterEsistingCases(this.testCaseData.testCases[i].testCaseId);
      }
    });
  }

  filterEsistingCases(i) {
    this.NotExistingCases = this.NotExistingCases.filter((id) => id.id !== i);
  }

  toggle(e: any, item) {
    const { id } = item;
    if (e.checked) {
      this.testCaseData.testCases.push({ testCaseId: id, enabled: true });
    } else {
      this.testCaseData.testCases = this.testCaseData.testCases.filter(
        (ids) => ids.testCaseId !== id
      );
    }
  }

  handleSave() {
    const dialogRef = this.dialog;

    if (this.existingLength === this.testCaseData.testCases.length) {
      this.testCaseReq.errorMessage("Please select a test case");
      return;
    }
    this.testSuiteReq
      .updateTestSuite(this.testCaseData, this.suiteData.id)
      .subscribe((data) => {
        this.dialog.closeAll();

        this.shared.existingTestCaseSubject.next(data);
      });
  }
}

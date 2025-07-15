import { Component, OnInit } from "@angular/core";
import { SelectionModel } from "@angular/cdk/collections";
import {
  MatDialog,
  MatDialogConfig,
  MatTableDataSource,
} from "@angular/material";
import { TestSuiteCreate } from "typings";
import { TestCaseService } from "src/app/services/test-case.service";
import { TestSuitesService } from "src/app/services/test-suites.service";
import { RunTestSuiteComponent } from "../run-test-suite/run-test-suite.component";
import { AddExistingTestCasesComponent } from "../add-existing-test-cases/add-existing-test-cases.component";
import { SharedService } from "src/app/services/shared.service";

export interface TableElement {
  id: number;
  name: string;
  description: string | number;
}

@Component({
  selector: "app-edit-test-suite",
  templateUrl: "./edit-test-suite.component.html",
  styleUrls: ["./edit-test-suite.component.scss"],
})
export class EditTestSuiteComponent implements OnInit {
  ELEMENT_DATA: TableElement[] = [];

  public TestSuiteCreate: TestSuiteCreate = {
    name: "",
    description: "",
    projectId: 1,
    testCases: [],
  };

  public suiteData: any;
  public isRunPressed: boolean = false;
  isRun: boolean = false;

  constructor(
    private testCaseReq: TestCaseService,
    private testSuiteReq: TestSuitesService,
    private dialog: MatDialog,
    private shared: SharedService
  ) {
    this.shared.existingTestCaseSubject$.subscribe((data: any) => {
      if (data) {
        this.suiteData = data.data;

        this.assignDataToTable();
      }
    });
  }

  displayedColumns: string[] = ["select", "name", "description", "actions"];
  dataSource = new MatTableDataSource<TableElement>(this.ELEMENT_DATA);
  selection = new SelectionModel<TableElement>(true, []);

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;

    return numSelected === numRows;
  }

  masterToggle() {
    this.isAllSelected()
      ? this.selection.clear()
      : this.dataSource.data.forEach((row) => this.selection.select(row));
  }

  checkboxLabel(row?: TableElement): string {
    if (!row) {
      return `${this.isAllSelected() ? "select" : "deselect"} all`;
    }
    return `${this.selection.isSelected(row) ? "deselect" : "select"} row ${
      row.id + 1
    }`;
  }

  ngOnInit() {
    this.shared.loadingSubject$.subscribe((data) => {
      this.isRunPressed = false;
    });

    this.assignDataToTable();

    if (this.isRun) {
      this.runTestSuite();
    }
  }

  assignDataToTable() {
    this.TestSuiteCreate.name = this.suiteData.name;
    this.TestSuiteCreate.description = this.suiteData.description;

    this.ELEMENT_DATA = this.suiteData.testCases;
    this.dataSource = new MatTableDataSource<TableElement>(this.ELEMENT_DATA);
    this.TestSuiteCreate.testCases = [];

    this.dataSource.data.forEach((row: any) => {
      if (row.enabled) {
        this.selection.select(row);
        const object = {
          testCaseId: row.testCaseId,
          enabled: true,
        };
        this.TestSuiteCreate.testCases.push(object);
      } else {
        const object = {
          testCaseId: row.testCaseId,
          enabled: false,
        };
        this.TestSuiteCreate.testCases.push(object);
      }
    });
  }

  saveTestCase() {
    this.testSuiteReq
      .createTestSuite(this.TestSuiteCreate)
      .subscribe((data) => {});
  }

  checkboxData(event, i) {
    this.TestSuiteCreate.testCases[i].enabled = event.checked;
  }

  updateTestCase() {
    this.testSuiteReq
      .updateTestSuite(this.TestSuiteCreate, this.suiteData.id)
      .subscribe((data: any) => {
        this.suiteData = data.data;
        this.assignDataToTable();
      });
  }

  deleteTestSuite(element: any) {
    for (let i = 0; i < this.TestSuiteCreate.testCases.length; i++) {
      if (this.TestSuiteCreate.testCases[i].testCaseId === element.testCaseId) {
        this.TestSuiteCreate.testCases.splice(i, 1);
      }
    }

    for (let i = 0; i < this.selection.selected.length; i++) {
      // @ts-ignore
      if (this.selection.selected[i].testCaseId === element.testCaseId) {
        this.selection.selected.splice(i, 1);
      }
    }

    const data = this.suiteData.testCases.filter((row: any) => row !== element);
    this.suiteData.testCases = data;

    this.ELEMENT_DATA = this.suiteData.testCases;
    this.dataSource = new MatTableDataSource<TableElement>(this.ELEMENT_DATA);
  }

  addTestCases() {
    const dialogRef = this.dialog.open(AddExistingTestCasesComponent, {
      width: "400px",
    });

    dialogRef.componentInstance.suiteData = this.suiteData;
    dialogRef.componentInstance.testCaseData = this.TestSuiteCreate;

    dialogRef.afterClosed().subscribe((result) => {});
  }

  runTestSuite() {
    this.isRunPressed = true;
    const body = [];

    for (let i = 0; i < this.TestSuiteCreate.testCases.length; i++) {
      if (this.TestSuiteCreate.testCases[i].enabled) {
        body.push(this.TestSuiteCreate.testCases[i].testCaseId);
      }
    }

    if (body.length === 0) {
      this.testCaseReq.errorMessage(
        "Please select atleast one test case to run"
      );
      this.isRunPressed = false;
      return;
    }

    this.testSuiteReq
      .runTestSuite(body, this.suiteData.id)
      .subscribe((data) => {
        this.isRunPressed = false;

        const matDialogConfig = new MatDialogConfig();

        const dialogRef = this.dialog.open(RunTestSuiteComponent, {
          width: "650px",
          minHeight: "calc(100%)",
          height: "100%",
          panelClass: ["animate__animated", "animate__slideInRight"],
        });
        matDialogConfig.position = { right: `0px`, top: `0px` };
        dialogRef.updatePosition(matDialogConfig.position);
        dialogRef.componentInstance.runProps = data;

        dialogRef.afterClosed().subscribe((result) => {});
      });
  }
}

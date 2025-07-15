import { Component, OnInit } from "@angular/core";
import { SelectionModel } from "@angular/cdk/collections";
import { MatDialog, MatTableDataSource } from "@angular/material";
import { TestSuiteCreate } from "typings";
import { TestCaseService } from "src/app/services/test-case.service";
import { TestSuitesService } from "src/app/services/test-suites.service";

export interface TableElement {
  id: number;
  name: string;
  description: string | number;
}

@Component({
  selector: "app-add-new-test-suite",
  templateUrl: "./add-new-test-suite.component.html",
  styleUrls: ["./add-new-test-suite.component.scss"],
})
export class AddNewTestSuiteComponent implements OnInit {
  ELEMENT_DATA: TableElement[] = [];

  public isUpdate: Boolean = false;
  public isNew: Boolean = false;

  public TestSuiteCreate: TestSuiteCreate = {
    name: "",
    description: "",
    projectId: 1,
    testCases: [],
  };

  constructor(
    private testCaseReq: TestCaseService,
    private testSuiteReq: TestSuitesService,
    private dialog: MatDialog
  ) {
    this.testCaseReq.getAllTestCasesWithoutLimit().subscribe((data: any) => {
      const { items } = data;
      this.ELEMENT_DATA = items;
      this.dataSource = new MatTableDataSource<TableElement>(this.ELEMENT_DATA);
    });
  }

  displayedColumns: string[] = ["select", "name", "description"];
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
    if (this.isUpdate) {
      this.displayedColumns.push("actions");
    }
  }

  saveTestCase() {
    const selectedData = this.selection.selected;
    this.getData();
    const dialogRef = this.dialog;

    if (this.TestSuiteCreate.testCases.length == 0) {
      this.testSuiteReq.errorMessage("Please select atleast one test case");
      return;
    }

    this.testSuiteReq
      .createTestSuite(this.TestSuiteCreate)
      .subscribe((data) => {
        dialogRef.closeAll();
      });
  }

  getData() {
    this.TestSuiteCreate.testCases = [];
    const selectedData = this.selection.selected;

    for (let select of selectedData) {
      const object = {
        testCaseId: select.id,
        enabled: true,
      };
      this.TestSuiteCreate.testCases.push(object);
    }
  }
}

import { Component, OnInit } from "@angular/core";
import { SelectionModel } from "@angular/cdk/collections";
import { MatTableDataSource } from "@angular/material";

export interface TableElement {
  id: number;
  name: string;
  description: string | number;
  status: string;
}

@Component({
  selector: "app-run-test-suite",
  templateUrl: "./run-test-suite.component.html",
  styleUrls: ["./run-test-suite.component.scss"],
})
export class RunTestSuiteComponent implements OnInit {
  runProps: any;
  assertions: any = [];
  assertionsBody: any = "";
  ELEMENT_DATA: TableElement[] = [
    { id: 1, name: "Hydrogen", description: 1.0079, status: "PASSED" },
  ];

  displayedColumns: string[] = ["id", "name", "description", "status"];
  dataSource = new MatTableDataSource<TableElement>(this.ELEMENT_DATA);
  selection = new SelectionModel<TableElement>(true, []);

  public failedCount = 0;
  public passedCount = 0;
  public failedCount1 = 1;
  public passedCount1 = 1;
  public barWidth = "";

  line = 0;

  constructor() {}

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
    this.ELEMENT_DATA = this.runProps.data.testCasesRunDetails;
    this.dataSource = new MatTableDataSource<TableElement>(this.ELEMENT_DATA);

    this.getAssertionsBarData(this.runProps.data);
    this.test(this.line, this.runProps.data.testCasesRunDetails[0]);
  }

  test(e, ele) {
    this.assertions = [];
    this.line = e;
    this.assertions = ele;
    try {
      this.assertionsBody = JSON.parse(ele.responseBody);
    } catch (e) {
      this.assertionsBody = "Not a valid JSON";
    }
  }

  getAssertionsBarData(assertions) {
    const total = assertions.totalTestCases;

    const failed = assertions.failedTestCases;
    const passed = assertions.passedTestCases;

    this.failedCount1 = failed;
    this.passedCount1 = passed;

    const failPercent = failed / total;
    const failedTotal = failPercent * 100;

    const passPercent = total - failed;
    const passTotal = passPercent / total;
    const totalPassPercentage = passTotal * 100;

    this.barWidth = Math.floor(totalPassPercentage) + "%";
  }

  bar(assertions) {
    const total = assertions.totalAssertions;

    const failed = assertions.failedAssertions;
    const passed = total - failed;

    this.failedCount = failed;
    this.passedCount = passed;

    const failPercent = failed / total;
    const failedTotal = failPercent * 100;

    const passPercent = total - failed;
    const passTotal = passPercent / total;
    const totalPassPercentage = passTotal * 100;

    return Math.floor(totalPassPercentage) + "%";
  }
}

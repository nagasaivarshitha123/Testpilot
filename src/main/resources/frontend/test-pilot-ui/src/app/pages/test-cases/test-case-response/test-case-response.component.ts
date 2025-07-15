import { Component, OnInit, Input, ViewEncapsulation } from "@angular/core";
import { MatDialog } from "@angular/material";
import { metaData } from "typings";

@Component({
  selector: "app-test-case-response",
  templateUrl: "./test-case-response.component.html",
  styleUrls: ["./test-case-response.component.scss"],
  encapsulation: ViewEncapsulation.None,
})
export class TestCaseResponseComponent implements OnInit {
  @Input() JSONFORMAT: any = {};
  @Input() isAssertion: Boolean = false;
  public selectedIndex = 2;
  public selectedChildIndex = 2;
  public assertions: any[] = [];
  public barWidth = "";
  public failedCount = 0;
  public passedCount = 0;

  public jsonData: any;
  public jsonHeadersData: any;
  public metaData: metaData = {
    size: "",
    status: "",
    time: "",
  };

  constructor(public dialog: MatDialog) {}

  ngOnInit() {}

  ngOnChanges() {
    if (this.JSONFORMAT) {
      this.jsonHeadersData = this.JSONFORMAT.data.responseHeaders;
      this.metaData = this.JSONFORMAT.data.responseMetadata;

      if (this.JSONFORMAT.data.responseBody === null) {
        if (this.JSONFORMAT.data.errorMessage) {
          try {
            this.jsonData = JSON.parse(this.JSONFORMAT.data.errorMessage);
          } catch (e) {
            this.jsonData = this.JSONFORMAT.data.errorMessage;
          }
        }
      } else {
        try {
          this.jsonData = JSON.parse(this.JSONFORMAT.data.responseBody);
        } catch (e) {
          this.jsonData = "Not a valid JSON";
        }
      }

      this.assertions = this.JSONFORMAT.data.responseAssertions;
      if (this.assertions) {
        this.getAssertionsBarData(this.assertions);
      }
    } else {
      this.jsonHeadersData = null;
      this.metaData = { status: "", size: "", time: "" };
      this.assertions = [];
      this.jsonData = null;
      this.failedCount = 0;
      this.passedCount = 0;
    }
  }

  getAssertionsBarData(assertions) {
    let count = 0;
    const total = assertions.length;

    assertions.map((data, i) => {
      if (data.status === "Failed") {
        count++;
      }
    });

    const failed = count;
    const passed = total - failed;

    this.failedCount = failed;
    this.passedCount = passed;

    const failPercent = failed / total;
    const failedTotal = failPercent * 100;

    const passPercent = total - failed;
    const passTotal = passPercent / total;
    const totalPassPercentage = passTotal * 100;

    this.barWidth = Math.floor(totalPassPercentage) + "%";
  }
}

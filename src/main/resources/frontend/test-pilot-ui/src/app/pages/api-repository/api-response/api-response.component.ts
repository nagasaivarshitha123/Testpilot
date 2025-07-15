import { Component, OnInit, Input, ViewEncapsulation } from "@angular/core";
import { MatDialog } from "@angular/material";
import { metaData } from "typings";

@Component({
  selector: "app-api-response",
  templateUrl: "./api-response.component.html",
  styleUrls: ["./api-response.component.scss"],
  encapsulation: ViewEncapsulation.None,
})
export class ApiResponseComponent implements OnInit {
  @Input() JSONFORMAT: any = {};
  public selectedIndex = 2;
  public selectedChildIndex = 2;

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
    } else {
      this.jsonHeadersData = null;
      this.metaData = { status: "", size: "", time: "" };
      this.jsonData = null;
    }
  }
}

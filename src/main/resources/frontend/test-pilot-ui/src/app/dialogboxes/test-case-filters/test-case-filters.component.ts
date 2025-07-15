import { Component, OnInit, ViewEncapsulation } from "@angular/core";
import { DatePipe } from "@angular/common";
import { FormControl } from "@angular/forms";
import { TestCaseService } from "src/app/services/test-case.service";
import { SharedService } from "src/app/services/shared.service";

export interface ApiType {
  viewValue: string;
}

@Component({
  selector: "app-test-case-filters",
  templateUrl: "./test-case-filters.component.html",
  styleUrls: ["./test-case-filters.component.scss"],
  providers: [DatePipe],
})
export class TestCaseFiltersComponent implements OnInit {
  startDate = new FormControl(new Date());
  EndDate = new FormControl(new Date());
  public type: string = "REST";
  public status: string = "All";

  pastDate = new Date();

  date = new FormControl(new Date());
  serializedDate = new FormControl(new Date());
  public searchText: string = "";

  apiType: ApiType[] = [{ viewValue: "REST" }, { viewValue: "SOAP" }];
  statusType: ApiType[] = [
    { viewValue: "All" },
    { viewValue: "Passed" },
    { viewValue: "Failed" },
  ];

  constructor(
    private serverReq: TestCaseService,
    private datePipe: DatePipe,
    private testCaseShared: SharedService
  ) {
    this.testCaseShared.filterSubject.subscribe((data: any) => {
      this.searchText = data;
    });

    this.pastDate.setMonth(this.pastDate.getMonth() - 1);
    this.date = new FormControl(this.pastDate.toISOString());
  }

  ngOnInit() {
    this.testCaseShared.filtersCaseSubject$.subscribe((data: any) => {
      if (data) {
        this.searchText = data.searchText;
        this.type = data.repositoryType;
        this.status = data.status;

        this.date = new FormControl(new Date(data.startDate).toISOString());
        this.serializedDate = new FormControl(
          new Date(data.endDate).toISOString()
        );
      }
    });
  }

  Reset() {
    this.searchText = " ";
    this.type = "REST";
    this.status = "All";
    this.pastDate.setMonth(this.pastDate.getMonth() - 1);
    this.date = new FormControl(this.pastDate.toISOString());
    this.serializedDate = new FormControl(new Date());
  }

  Apply() {
    const filterTestCase = {
      searchText: this.searchText ? this.searchText : " ",
      startDate: this.datePipe.transform(this.date.value, "yyyy-MM-dd"),
      endDate: this.datePipe.transform(this.serializedDate.value, "yyyy-MM-dd"),
      status: this.status,
      repositoryType: this.type,
      pageNo: 1,
      pageSize: 5,
    };

    this.testCaseShared.filtersCaseSubject.next(filterTestCase);
    this.testCaseShared.searchTextEmitterSubject.next(filterTestCase);

    this.serverReq
      .filterTestCaseReports(filterTestCase)
      .subscribe((data: any) => {
        this.testCaseShared.filterSubject.next(data);
      });
  }
}

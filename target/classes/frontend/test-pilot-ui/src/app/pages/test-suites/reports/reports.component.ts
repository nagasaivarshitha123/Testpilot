import { Component, OnDestroy, OnInit, ViewChild } from "@angular/core";
import {
  MatDialog,
  MatPaginator,
  MatTableDataSource,
  PageEvent,
} from "@angular/material";
import { Subscription } from "rxjs";
import { FiltersComponent } from "src/app/dialogboxes/filters/filters.component";
import { SharedService } from "src/app/services/shared.service";
import { TestSuitesService } from "src/app/services/test-suites.service";
import { filterTestCase } from "typings";

export interface TableElement {
  name: string;
  Description: string | number;
  Date: string;
  Trend: string;
  unselected: string;
  Actions: string;
}
@Component({
  selector: "app-reports",
  templateUrl: "./reports.component.html",
  styleUrls: ["./reports.component.scss"],
})
export class ReportsComponent implements OnInit, OnDestroy {
  ELEMENT_DATA: TableElement[] = [];
  isLoading: boolean = false;
  displayedColumns: string[] = [
    "name",
    "total",
    "passed",
    "failed",
    "status",
    "Actions",
  ];
  dataSource = new MatTableDataSource<TableElement>(this.ELEMENT_DATA);

  @ViewChild(MatPaginator) paginator: MatPaginator;
  public countDown: any;

  // MatPaginator Output
  pageSizeOptions: number[] = [5, 10];
  length = 1;
  pageEvent: PageEvent;
  currentPageIndex: number = 0;

  public filterTestCase: filterTestCase = {
    searchText: "",
    startDate: "",
    endDate: "",
    status: "All",
    repositoryType: "REST",
    pageNo: 1,
    pageSize: 5,
  };

  filterSubject: Subscription;
  filtersSuitesSubject: Subscription;

  constructor(
    private dialog: MatDialog,
    private testSuiteReq: TestSuitesService,
    private testSuiteShared: SharedService
  ) {
    this.initFiltersDate();
    if (!this.filterSubject) {
      this.filterSubject = this.testSuiteShared.filterSubject.subscribe(
        (data: any) => {
          const { items } = data;
          this.ELEMENT_DATA = items;
          this.length = data.totalElements;
          this.currentPageIndex = 0;
          this.dataSource = new MatTableDataSource<TableElement>(
            this.ELEMENT_DATA
          );
        }
      );
    }

    this.testSuiteShared.searchTextEmitterSubject.subscribe((data: any) => {
      this.filterTestCase.endDate = data.endDate;
      this.filterTestCase.startDate = data.startDate;
      this.filterTestCase.repositoryType = data.repositoryType;
      this.filterTestCase.status = data.status;
    });
  }

  initFiltersDate() {
    const filterTestCase = {
      searchText: "",
      startDate: "",
      endDate: "",
      status: "",
      repositoryType: "REST",
      pageNo: 1,
      pageSize: 5,
    };

    this.testSuiteReq
      .filterTestSuiteReports(filterTestCase)
      .subscribe((data: any) => {
        const { items } = data;
        this.ELEMENT_DATA = items;
        this.length = data.totalElements;
        this.currentPageIndex = 0;
        this.dataSource = new MatTableDataSource<TableElement>(
          this.ELEMENT_DATA
        );
      });
  }

  ngOnInit() {
    this.dataSource.paginator = this.paginator;

    if (!this.filtersSuitesSubject) {
      this.filtersSuitesSubject =
        this.testSuiteShared.filtersSuitesSubject$.subscribe((data: any) => {
          if (data) {
            this.filterTestCase.searchText = data.searchText;
            this.filterTestCase.repositoryType = data.repositoryType;
            this.filterTestCase.status = data.status;
            this.filterTestCase.startDate = data.startDate;
            this.filterTestCase.endDate = data.endDate;
          }
          this.searchTextFilter();
        });
    }
  }

  delayedSearch(e: KeyboardEvent) {
    // @ts-ignore
    // clearTimeout(this.countDown);
    this.isLoading = true;
    // this.countDown = setTimeout(() => {
    // @ts-ignore
    this.isLoading = false;
    if (!this.filterTestCase.searchText) {
      this.initFiltersDate();
    } else {
      this.searchTextFilter();
    }
    // }, 2000);
  }

  filterDialog() {
    const dialogRef = this.dialog.open(FiltersComponent, {
      width: "400px",
      height: "380px",
    });
    dialogRef.componentInstance.searchText = this.filterTestCase.searchText;

    dialogRef.afterClosed().subscribe((result) => {});
  }

  onChangePage(e: PageEvent) {
    this.filterTestCase.pageSize = e.pageSize;
    this.filterTestCase.pageNo = e.pageIndex + 1;

    this.testSuiteReq
      .filterTestSuiteReportsSearch(this.filterTestCase)
      .subscribe((data: any) => {
        const { items } = data;
        this.ELEMENT_DATA = items;
        this.length = data.totalElements;
        this.dataSource = new MatTableDataSource<TableElement>(
          this.ELEMENT_DATA
        );
      });
  }

  searchTextFilter() {
    this.testSuiteReq
      .filterTestSuiteReportsSearch(this.filterTestCase)
      .subscribe((data: any) => {
        this.length = data.totalElements;
        this.currentPageIndex = 0;
        this.testSuiteShared.filterSubject.next(data);
      });
  }

  ngOnDestroy(): void {
    this.filterSubject.unsubscribe();
    this.filtersSuitesSubject.unsubscribe();
  }
}

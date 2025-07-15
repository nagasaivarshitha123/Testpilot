import {
  Component,
  EventEmitter,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
} from "@angular/core";
import { MatDialog, MatPaginator, MatTableDataSource } from "@angular/material";
import { NavigationExtras, Router } from "@angular/router";
import { TestCaseService } from "src/app/services/test-case.service";
import { TestCaseFilter } from "typings";
import { AddNewTestCaseComponent } from "../add-new-test-case/add-new-test-case.component";
import { PageEvent } from "@angular/material";
import { SharedService } from "src/app/services/shared.service";
import { Subscription } from "rxjs";

export interface TableElement {
  id: number;
  name: string;
  description: string | number;
  createdBy: string;
  trend: any;
  Actions: string;
}

@Component({
  selector: "app-manage",
  templateUrl: "./manage.component.html",
  styleUrls: ["./manage.component.scss"],
})
export class ManageComponent implements OnInit, OnDestroy {
  ELEMENT_DATA: TableElement[] = [];
  public storedRequestId: string | number = "";

  pageIndex: number = 0;

  // MatPaginator Output
  pageSizeOptions: number[] = [5, 10];
  length = 1;
  pageEvent: PageEvent;
  currentPageIndex: number = 0;
  currentPageSize: number = 0;
  isLoading: boolean = false;
  searchText: string = "";

  displayedColumns: string[] = [
    "id",
    "name",
    "description",
    "Repository name",
    "Request name",
    "createdBy",
    // "trend",
    "Actions",
  ];
  dataSource = new MatTableDataSource<TableElement>(this.ELEMENT_DATA);

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @Output() setRunEmitter = new EventEmitter<string>();
  @Output() runTestCaseDataEmitter = new EventEmitter<string>();
  @Output() assertionsEmitter = new EventEmitter<Boolean>();
  @Output() setAutoRunEmitter = new EventEmitter<string>();

  public countDown: any;

  public filterTestCase: TestCaseFilter = {
    searchText: "",
    pageNo: 1,
    pageSize: 10,
    projectId: 1,
  };
  subscription: Subscription;

  constructor(
    private dialog: MatDialog,
    private caseService: TestCaseService,
    private router: Router,
    private shared: SharedService
  ) {
    this.getTestCases();
  }

  ngOnInit() {
    this.dataSource.paginator = this.paginator;

    if (!this.subscription) {
      this.subscription = this.caseService.ManageRefreshRequired.subscribe(
        (response) => {
          this.getTestCases();
        }
      );
    }
  }

  onPropsChange() {
    this.shared.paginationSubject$
      .subscribe((props: any) => {
        if (props !== null) {
          this.filterTestCase.pageSize = props.pageSize;
          this.filterTestCase.pageNo = props.pageNo;
          this.currentPageIndex = props.pageNo - 1;

          this.caseService
            .filterTestCaseSearch(this.filterTestCase)
            .subscribe((data: any) => {
              const { items } = data;
              this.ELEMENT_DATA = items;
              this.currentPageSize = items.length;
              this.length = data.totalElements;

              this.dataSource = new MatTableDataSource<TableElement>(
                this.ELEMENT_DATA
              );
            });
        }
      })
      .unsubscribe();
  }

  getTestCases() {
    this.caseService
      .getAllTestCases(this.filterTestCase)
      .subscribe((data: any) => {
        const { items } = data;
        this.ELEMENT_DATA = items;
        this.length = data.totalElements;
        this.currentPageIndex = data.pageNo - 1;
        this.dataSource = new MatTableDataSource<TableElement>(
          this.ELEMENT_DATA
        );

        this.onPropsChange();
      });
  }

  onChangePage(e: PageEvent) {
    this.filterTestCase.pageSize = e.pageSize;
    this.filterTestCase.pageNo = e.pageIndex + 1;

    const paginationProps = {
      pageSize: e.pageSize,
      pageNo: e.pageIndex + 1,
    };
    this.shared.paginationSubject.next(paginationProps);
    this.shared.previiousPageIndexSubject.next(e.pageIndex + 1);

    this.caseService
      .filterTestCaseSearch(this.filterTestCase)
      .subscribe((data: any) => {
        const { items } = data;
        this.ELEMENT_DATA = items;
        this.length = data.totalElements;
        this.dataSource = new MatTableDataSource<TableElement>(
          this.ELEMENT_DATA
        );

        this.pageIndex =
          data.pageNo * data.requestedPageSize - data.requestedPageSize;
      });
  }

  openNewTestCasePopUp() {
    const dialogRef = this.dialog.open(AddNewTestCaseComponent, {
      width: "500px",
      height: "auto",
    });

    dialogRef.afterClosed().subscribe((result) => {});
  }

  deleteTestCase(element: any) {
    const { id } = element;
    this.caseService.deleteTestCase(id).subscribe((data) => {});
  }

  setRun(element: any) {
    const navigationExtras: NavigationExtras = {
      queryParams: {
        isRun: true,
        requestParams: element,
      },
    };
    this.router.navigateByUrl("/testcases/requests", navigationExtras);
  }

  setEdit(element: any) {
    const navigationExtras: NavigationExtras = {
      queryParams: {
        isRun: false,
        requestParams: element,
      },
    };
    this.router.navigateByUrl("/testcases/requests", navigationExtras);
  }

  delayedSearch(e: KeyboardEvent) {
    // @ts-ignore
    this.searchTextFilter();
    // clearTimeout(this.countDown);
    // this.isLoading = true;
    // this.countDown = setTimeout(() => {
    //   // @ts-ignore
    //   this.isLoading = false;
    //   // this.searchTextFilter();
    // }, 2000);
  }

  searchTextFilter() {
    this.filterTestCase.pageNo = 1;
    this.filterTestCase.pageSize = 10;

    this.caseService
      .filterTestCaseSearch(this.filterTestCase)
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

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}

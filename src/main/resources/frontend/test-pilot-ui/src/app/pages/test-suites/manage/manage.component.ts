import {
  Component,
  EventEmitter,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
} from "@angular/core";
import {
  MatDialog,
  MatDialogConfig,
  MatPaginator,
  MatTableDataSource,
  PageEvent,
} from "@angular/material";
import { SharedService } from "src/app/services/shared.service";
import { TestSuitesService } from "src/app/services/test-suites.service";
import { TestCaseFilter } from "typings";
import { AddNewTestSuiteComponent } from "../add-new-test-suite/add-new-test-suite.component";
import { EditTestSuiteComponent } from "../edit-test-suite/edit-test-suite.component";
import { RunTestSuiteComponent } from "../run-test-suite/run-test-suite.component";
import { Subscription } from "rxjs";

export interface TableElement {
  id: number;
  name: string;
  description: string | number;
  creationDate: string;
  testCases: number;
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

  searchText: string = "";
  isLoading: boolean = false;

  displayedColumns: string[] = [
    "id",
    "name",
    "description",
    "creationDate",
    "testCases",
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
    projectId: 1
  };

  // initProps = {
  //   pageNo: 1,
  //   pageSize: 10,
  //   projectId: 1,
  // };
  subscription: Subscription;

  constructor(
    private dialog: MatDialog,
    private testSuiteReq: TestSuitesService,
    private shared: SharedService
  ) {
    this.getTestSuites();
  }

  ngOnInit() {
    this.dataSource.paginator = this.paginator;

    if (!this.subscription) {
      this.subscription = this.testSuiteReq.RefreshRequired.subscribe(
        (response) => {
          this.getTestSuites();
        }
      );
    }

    this.shared.loadingSubject.subscribe((data) => {
      this.isLoading = false;
    });
  }

  getTestSuites() {
    this.testSuiteReq
      .getAllTestSuites(this.filterTestCase)
      .subscribe((data: any) => {
        const { items } = data;
        this.length = data.totalElements;
        this.ELEMENT_DATA = items;
        this.dataSource = new MatTableDataSource<TableElement>(
          this.ELEMENT_DATA
        );

        this.onPropsChange();
      });
  }

  openNewTestSuitePopUp() {
    const matDialogConfig = new MatDialogConfig();
    const dialogRef = this.dialog.open(AddNewTestSuiteComponent, {
      width: "600px",
      minHeight: "calc(100% - 90px)",
      height: "100%",
      panelClass: ["animate__animated", "animate__slideInRight"],
    });

    matDialogConfig.position = { right: `0px`, top: `0px` };
    dialogRef.updatePosition(matDialogConfig.position);
    dialogRef.componentInstance.isNew = true;
    dialogRef.afterClosed().subscribe((result) => {});
  }

  onPropsChange() {
    this.shared.paginationSuitesSubject$.subscribe((props: any) => {
      if (props !== null) {
        this.filterTestCase.pageSize = props.pageSize;
        this.filterTestCase.pageNo = props.pageNo;
        this.currentPageIndex = props.pageNo - 1;

        this.testSuiteReq
          .filterTestSuiteSearch(this.filterTestCase)
          .subscribe((data: any) => {
            const { items } = data;
            this.ELEMENT_DATA = items;
            this.currentPageSize = items.length;
            this.length = data.totalElements;

            this.dataSource = new MatTableDataSource<TableElement>(
              this.ELEMENT_DATA
            );

            this.pageIndex =
              data.pageNo * data.requestedPageSize - data.requestedPageSize;
          });
      }
    });
  }

  onChangePage(e: PageEvent) {
    this.filterTestCase.pageSize = e.pageSize;
    this.filterTestCase.pageNo = e.pageIndex + 1;

    const paginationProps = {
      pageSize: e.pageSize,
      pageNo: e.pageIndex + 1,
    };
    this.shared.paginationSuitesSubject.next(paginationProps);
    this.shared.previiousPageSuitesIndexSubject.next(e.pageIndex + 1);

    this.testSuiteReq
      .filterTestSuiteSearch(this.filterTestCase)
      .subscribe((data: any) => {
        const { items } = data;
        this.ELEMENT_DATA = items;
        this.length = data.totalElements;
        this.dataSource = new MatTableDataSource<TableElement>(
          this.ELEMENT_DATA
        );
      });
  }

  deleteTestSuit(element: any) {
    const { id } = element;
    this.testSuiteReq.deleteTestSuite(id).subscribe((data) => {});
  }

  setRun(element: any) {
    this.storedRequestId = element.id;
    this.runTestCaseDataEmitter.emit(element);
    // API REPOSITORY
    this.setRunEmitter.emit("API Repository");
    this.assertionsEmitter.emit(true);
    this.setAutoRunEmitter.emit("emit");
  }

  setEdit(element: any) {
    const matDialogConfig = new MatDialogConfig();
    const dialogRef = this.dialog.open(EditTestSuiteComponent, {
      height: "100%",
      width: "600px",
      minHeight: "calc(100vh - 90px)",
      panelClass: ["animate__animated", "animate__slideInRight"],
    });

    matDialogConfig.position = { right: `0px`, top: `0px` };
    dialogRef.updatePosition(matDialogConfig.position);
    dialogRef.componentInstance.suiteData = element;

    dialogRef.afterClosed().subscribe((result) => {});
  }

  delayedSearch(e: KeyboardEvent) {
    // @ts-ignore
    // clearTimeout(this.countDown);
    this.isLoading = true;
    // this.countDown = setTimeout(() => {
    // @ts-ignore
    this.isLoading = false;
    this.searchTextFilter();
    // }, 1000);
  }

  searchTextFilter() {
    this.filterTestCase.pageNo = 1;
    this.filterTestCase.pageSize = 10;

    this.testSuiteReq
      .filterTestSuiteSearch(this.filterTestCase)
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

  runTestSuit(element: any) {
    this.isLoading = true;
    const body = [];

    for (let i = 0; i < element.testCases.length; i++) {
      if (element.testCases[i].enabled) {
        body.push(element.testCases[i].testCaseId);
      }
    }

    this.testSuiteReq.runTestSuite(body, element.id).subscribe((data) => {
      const matDialogConfig = new MatDialogConfig();
      this.isLoading = false;

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

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}

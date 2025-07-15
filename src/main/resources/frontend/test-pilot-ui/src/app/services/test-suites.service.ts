import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Subject } from "rxjs/internal/Subject";
import { BASE_URL } from "src/environments/environment";
import { HeadersService } from "./headers.service";
import { map, catchError, tap } from "rxjs/operators";
import { filterTestCase, TestCaseFilter, TestSuiteCreate } from "typings";
import { NotifierService } from "angular-notifier";
import { SharedService } from "./shared.service";

@Injectable({
  providedIn: "root",
})
export class TestSuitesService {
  private refreshRequired = new Subject<void>();
  private readonly notifier: NotifierService;
  public errorValues: string[];

  constructor(
    private http: HttpClient,
    private headers: HeadersService,
    notifier: NotifierService,
    private shared: SharedService
  ) {
    this.notifier = notifier;
  }

  get RefreshRequired() {
    return this.refreshRequired;
  }

  errorNotofier(err: any) {
    this.errorValues = Object.values(err.error.errors);

    this.errorValues.map((err, i) => {
      this.notifier.notify("error", err);
    });
  }

  errorMessage(err: any) {
    this.notifier.notify("error", err);
  }

  successNotifier(response) {
    this.notifier.notify("success", response.message);
  }

  getAllTestSuites(props: any) {
    return this.http
      .get(
        BASE_URL +
          `/testsuite?pageNo=${props.pageNo}&pageSize=${props.pageSize}&projectId=${props.projectId}&sortBy=id:desc`,
        {
          headers: this.headers.normalHeaders().headers,
        }
      )
      .pipe(
        map((response) => {
          return response;
        })
      )
      .pipe(
        catchError((err) => {
          this.errorNotofier(err);
          return err;
        })
      );
  }

  createTestSuite(body: TestSuiteCreate) {
    return this.http
      .post(BASE_URL + `/testsuite`, body, {
        headers: this.headers.normalHeaders().headers,
      })
      .pipe(
        tap(() => {
          this.refreshRequired.next();
        })
      )
      .pipe(
        map((response) => {
          this.successNotifier(response);
          return response;
        })
      )
      .pipe(
        catchError((err) => {
          this.errorNotofier(err);
          return err;
        })
      );
  }

  deleteTestSuite(id: number | string) {
    return this.http
      .delete(BASE_URL + `/testsuite/${id}`, {
        headers: this.headers.normalHeaders().headers,
      })
      .pipe(
        tap(() => {
          this.refreshRequired.next();
        })
      )
      .pipe(
        map((response) => {
          this.successNotifier(response);
          return response;
        })
      )
      .pipe(
        catchError((err) => {
          this.errorNotofier(err);
          return err;
        })
      );
  }

  filterTestSuiteSearch(params: TestCaseFilter) {
    return this.http
      .get(
        BASE_URL +
          `/testsuite?pageNo=${params.pageNo}&pageSize=${params.pageSize}&projectId=1&searchText=${params.searchText}&sortBy=id:desc`,
        {
          headers: this.headers.normalHeaders().headers,
        }
      )
      .pipe(
        map((response) => {
          return response;
        })
      )
      .pipe(
        catchError((err) => {
          this.errorNotofier(err);
          return err;
        })
      );
  }

  filterTestSuiteReports(params: filterTestCase) {
    return this.http
      .get(
        BASE_URL +
          `/testsuite/report?pageNo=${params.pageNo}&pageSize=${params.pageSize}&projectId=1&startDate=${params.startDate}&endDate=${params.endDate}&status=${params.status}`,
        {
          headers: this.headers.normalHeaders().headers,
        }
      )
      .pipe(
        map((response) => {
          return response;
        })
      )
      .pipe(
        catchError((err) => {
          this.errorNotofier(err);
          return err;
        })
      );
  }

  filterTestSuiteReportsSearch(params: filterTestCase) {
    return this.http
      .get(
        BASE_URL +
          `/testsuite/report?pageNo=${params.pageNo}&pageSize=${params.pageSize}&projectId=1&searchText=${params.searchText}&repositoryType=${params.repositoryType}&startDate=${params.startDate}&endDate=${params.endDate}&status=${params.status}`,
        {
          headers: this.headers.normalHeaders().headers,
        }
      )
      .pipe(
        map((response) => {
          return response;
        })
      )
      .pipe(
        catchError((err) => {
          return err;
        })
      );
  }

  updateTestSuite(body: any, id: number | string) {
    return this.http
      .put(BASE_URL + `/testsuite/${id}`, body, {
        headers: this.headers.normalHeaders().headers,
      })
      .pipe(
        tap(() => {
          this.refreshRequired.next();
        })
      )
      .pipe(
        map((response) => {
          this.successNotifier(response);
          return response;
        })
      )
      .pipe(
        catchError((err) => {
          this.errorNotofier(err);
          return err;
        })
      );
  }

  runTestSuite(body: number[], id: number) {
    return this.http
      .post(BASE_URL + `/testsuite/${id}/run`, body, {
        headers: this.headers.normalHeaders().headers,
      })
      .pipe(
        tap(() => {
          this.refreshRequired.next();
        })
      )
      .pipe(
        map((response) => {
          return response;
        })
      )
      .pipe(
        catchError((err) => {
          this.shared.loadingSubject.next(false);
          this.errorNotofier(err);
          return err;
        })
      );
  }
}

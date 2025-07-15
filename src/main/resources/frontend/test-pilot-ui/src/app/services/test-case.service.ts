import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { BASE_URL } from "src/environments/environment";
import { HeadersService } from "./headers.service";
import { map, catchError, tap, share } from "rxjs/operators";
import { CreateTestCaseType, filterTestCase, TestCaseFilter } from "typings";
import { NotifierService } from "angular-notifier";
import { Observable, Subject } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class TestCaseService {
  private refreshRequired = new Subject<void>();
  private testCaseManageRefreshRequired = new Subject<void>();

  private readonly notifier: NotifierService;
  public errorValues: string[];

  constructor(
    private http: HttpClient,
    private headers: HeadersService,
    notifier: NotifierService
  ) {
    this.notifier = notifier;
  }

  get RefreshRequired() {
    return this.refreshRequired;
  }

  get ManageRefreshRequired(): Observable<any> {
    return this.testCaseManageRefreshRequired.asObservable();
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

  getAllTestCases(filters: TestCaseFilter) {
    return this.http
      .get(
        BASE_URL +
          `/testcase?pageNo=${filters.pageNo}&pageSize=${filters.pageSize}&projectId=${filters.projectId}&sortBy=id:desc`,
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

  getAllTestCasesWithoutLimit() {
    return this.http
      .get(BASE_URL + `/testcase?projectId=1&fetchAll=true`, {
        headers: this.headers.normalHeaders().headers,
      })
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

  createTestCase(body: CreateTestCaseType) {
    return this.http
      .post(BASE_URL + `/testcase`, body, {
        headers: this.headers.normalHeaders().headers,
      })
      .pipe(
        tap(() => {
          this.testCaseManageRefreshRequired.next();
        }),
        share()
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

  deleteTestCase(id: number | string) {
    return this.http
      .delete(BASE_URL + `/testcase/${id}`, {
        headers: this.headers.normalHeaders().headers,
      })
      .pipe(
        tap(() => {
          this.testCaseManageRefreshRequired.next();
        }),
        share()
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

  filterTestCaseSearch(params: TestCaseFilter) {
    return this.http
      .get(
        BASE_URL +
          `/testcase?pageNo=${params.pageNo}&pageSize=${params.pageSize}&projectId=1&searchText=${params.searchText}&sortBy=id:desc`,
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

  filterTestCaseReports(params: filterTestCase) {
    return this.http
      .get(
        BASE_URL +
          `/testcase/report?pageNo=${params.pageNo}&pageSize=${params.pageSize}&projectId=1&repositoryType=${params.repositoryType}&startDate=${params.startDate}&endDate=${params.endDate}&status=${params.status}`,
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

  filterTestCaseReportsSearch(params: filterTestCase) {
    return this.http
      .get(
        BASE_URL +
          `/testcase/report?pageNo=${params.pageNo}&pageSize=${params.pageSize}&projectId=1&searchText=${params.searchText}&repositoryType=${params.repositoryType}&startDate=${params.startDate}&endDate=${params.endDate}&status=${params.status}`,
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

  updateTestCaseHeaders(body: any, id: number | string) {
    return this.http
      .put(BASE_URL + `/testcase/${id}/headers`, body, {
        headers: this.headers.normalHeaders().headers,
      })
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

  editTestCase(body: any, id: number | string) {
    return this.http
      .put(BASE_URL + `/testcase/${id}`, body, {
        headers: this.headers.normalHeaders().headers,
      })
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

  updateTestCaseAuth(body: any, id: number | string) {
    return this.http
      .put(BASE_URL + `/testcase/${id}/authorization`, body, {
        headers: this.headers.normalHeaders().headers,
      })
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

  updateAssertions(id: string | number, body: any) {
    return this.http
      .put(BASE_URL + `/testcase/${id}/assertions`, body, {
        headers: this.headers.normalHeaders().headers,
      })
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

  updateTestCaseBody(body: any, id: number | string) {
    return this.http
      .put(BASE_URL + `/testcase/${id}/body`, body, {
        headers: this.headers.normalHeaders().headers,
      })
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
}

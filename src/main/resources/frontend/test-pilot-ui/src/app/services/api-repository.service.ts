import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { BASE_URL } from "src/environments/environment";
import { HeadersService } from "./headers.service";
import { map, catchError, tap } from "rxjs/operators";
import { createRepository, runRequest } from "typings";
import { Subject } from "rxjs/internal/Subject";
import { NotifierService } from "angular-notifier";
import { SharedService } from "./shared.service";

@Injectable({
  providedIn: "root",
})
export class ApiRepositoryService {
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

  getAllRequests(projectId: number | string) {
    return this.http
      .get(BASE_URL + `/request?projectId=${projectId}`, {
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

  runTestCase(body: any, id: number | string) {
    return this.http
      .put(BASE_URL + `/testcase/${id}/run`, body, {
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

  getAllRepositories(id: number) {
    return this.http
      .get(BASE_URL + `/repository?projectId=${1}`, {
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

  createRepository(body: createRepository) {
    return this.http
      .post(BASE_URL + "/repository", body, {
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

  run(body: runRequest, id: number | string) {
    return this.http
      .put(BASE_URL + `/request/${id}/run`, body, {
        headers: this.headers.normalHeaders().headers,
      })
      .pipe(
        map((response) => {
          this.shared.updateRequestURLSubjectData.next(response);
          this.shared.updateRequestMethodSubjectData.next(response);
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

  deleteRepository(id: number | string) {
    return this.http
      .delete(BASE_URL + `/repository/${id}`, {
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

  deleteRequest(id: number | string) {
    return this.http
      .delete(BASE_URL + `/request/${id}`, {
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

  updateApiHeaders(body: any, id: number | string) {
    return this.http
      .put(BASE_URL + `/request/${id}/headers`, body, {
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

  updateApiAuth(body: any, id: number | string) {
    return this.http
      .put(BASE_URL + `/request/${id}/authorization`, body, {
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

  updateApiBody(body: any, id: number | string) {
    return this.http
      .put(BASE_URL + `/request/${id}/body`, body, {
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

  updateRepo(body: any, id: number | string) {
    return this.http
      .put(BASE_URL + `/repository/${id}`, body, {
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

  updateRequest(body: any, id: number | string) {
    return this.http
      .put(BASE_URL + `/request/${id}`, body, {
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

  addNewRequestinRepo(body: any) {
    return this.http
      .post(BASE_URL + `/request/create`, body, {
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
}

import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { HeadersService } from "./headers.service";
import { BASE_URL } from "src/environments/environment";
import { NotifierService } from "angular-notifier";
import { map, catchError, tap } from "rxjs/operators";

@Injectable({
  providedIn: "root",
})
export class MainService {
  private readonly notifier: NotifierService;
  public errorValues: string[];

  constructor(
    private http: HttpClient,
    private headers: HeadersService,
    notifier: NotifierService
  ) {
    this.notifier = notifier;
  }

  errorNotofier(err: any) {
    this.errorValues = Object.values(err.error.errors);

    this.errorValues.map((err, i) => {
      this.notifier.notify("error", err);
    });
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
}

import { Injectable } from "@angular/core";
import { HttpHeaders } from "@angular/common/http";
import { AuthService } from "./auth.service";

@Injectable({
  providedIn: "root",
})
export class HeadersService {
  constructor(private auth: AuthService) {}

  normalHeaders() {
    const headers = {
      headers: new HttpHeaders({
        "Content-Type": "application/json",
        Accept: "application/json",
        Authorization: "Bearer " + this.auth.getToken(),
      }),
    };
    return headers;
  }
}

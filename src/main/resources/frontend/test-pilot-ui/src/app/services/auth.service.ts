import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { Observable, of, throwError } from "rxjs";
import { map } from "rxjs/operators";
import { BASE_URL } from "src/environments/environment";

@Injectable({
  providedIn: "root",
})
export class AuthService {
  constructor(private router: Router, private http: HttpClient) {}

  setToken(token: string): void {
    localStorage.setItem("token", token);
  }

  setUserDetails(userDetails: any): void {
    localStorage.setItem("userDetails", userDetails);
  }

  getToken(): string | null {
    return localStorage.getItem("token");
  }

  getUserDetails(): any | null {
    return localStorage.getItem("userDetails");
  }

  isLoggedIn(): Observable<boolean> {
    const isLoggedIn = this.getToken() !== null;
    return of(isLoggedIn); // Return the value as an Observable
  }

  logout() {
    localStorage.removeItem("token");
    this.router.navigate(["login"]);
  }

  getOtp(email: string): Observable<any> {
    return this.http
      .post(`${BASE_URL}/authentication/register-or-login?email=${email}`, {})
      .pipe(
        map((response) => {
          return response;
        })
      );
  }
  verifyOtp(email: string, otp: string): Observable<any> {
    return this.http
      .post(`${BASE_URL}/authentication/verify`, {
        email,
        otp,
      })
      .pipe(
        map((response: any) => {
          this.setToken(response.data.jwt);
          this.setUserDetails(
            JSON.stringify({
              name: response.data.name,
              email: response.data.email,
            })
          );
          console.log(response);
          return response;
        })
      );
  }

  login({ email, password }: any): Observable<any> {
    if (email === "admin@gmail.com" && password === "admin123") {
      this.setToken("abcdefghijklmnopqrstuvwxyz");
      return of({ name: "Tarique Akhtar", email: "admin@gmail.com" });
    }
    return throwError(new Error("Failed to login"));
  }
}

import { Component, Input, OnInit } from "@angular/core";
import { AuthService } from "../services/auth.service";

@Component({
  selector: "app-body",
  templateUrl: "./body.component.html",
  styleUrls: ["./body.component.scss"],
})
export class BodyComponent implements OnInit {
  @Input() collapsed: boolean = false;
  @Input() screenWidth: number = 0;
  user: Boolean = false;

  constructor(public authService: AuthService) {}

  // ngAfterViewInit() {
  //   this.user = this.auth.isLoggedIn();
  // }

  ngOnInit() {
    // this.auth.isLoggedIn().subscribe((isLoggedIn) => {
    //   this.user = isLoggedIn;
    // });
  }

  getBodyClass(): string {
    let styleClass = "";
    if (this.collapsed && this.screenWidth > 768) {
      styleClass = "body-trimmed";
    } else if (
      this.collapsed &&
      this.screenWidth <= 768 &&
      this.screenWidth > 0
    ) {
      styleClass = "body-md-screen";
    }

    return styleClass;
  }

  getHeaderClass(): string {
    let styleClass = "";
    if (this.collapsed && this.screenWidth > 768) {
      styleClass = "header-trimmed";
    } else if (
      this.collapsed &&
      this.screenWidth <= 768 &&
      this.screenWidth > 0
    ) {
      styleClass = "header-md-screen";
    }

    return styleClass;
  }
}

import { Component, Input, OnInit } from "@angular/core";
import { MatSelect } from "@angular/material";
import { NavigationEnd, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";

export interface Projects {
  value: string;
  viewValue: string;
}

@Component({
  selector: "app-header",
  templateUrl: "./header.component.html",
  styleUrls: ["./header.component.scss"],
})
export class HeaderComponent implements OnInit {
  @Input() selectedMenu: string = "";
  projects: Projects[] = [{ value: "steak-0", viewValue: "Project 1" }];
  username: any = "";

  constructor(private router: Router, private auth: AuthService) {
    console.log(JSON.parse(this.auth.getUserDetails()));
    if (this.auth.getUserDetails()) {
      this.username = JSON.parse(this.auth.getUserDetails());
    }
  }

  ngOnInit() {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        const arr = event.url
          .split("/")
          .toString()
          .replace(",", " ")
          .replace(",", "  /  ")
          .replace(",", "")
          .split(" ");

        for (var i = 0; i < arr.length; i++) {
          arr[i] = arr[i].charAt(0).toUpperCase() + arr[i].slice(1);
        }

        this.selectedMenu = arr.join(" ");
      }
    });
  }

  s1(sel: MatSelect) {
    sel.placeholder = "";
  }

  s2(sel: MatSelect) {
    if (sel.value === undefined) {
      sel.placeholder = "";
    }
  }

  logout() {
    this.auth.logout();
  }
}

import { Component, ViewChild } from "@angular/core";
import { AuthService } from "./services/auth.service";
import { SidenavComponent } from "./sidenav/sidenav.component";

interface SideNavToggle {
  screenWidth: number;
  collapsed: boolean;
}

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.scss"],
})
export class AppComponent {
  title = "TestPilot";
  isSideNavCollapsed: boolean = false;
  screenWidth: number = 0;
  user: Boolean = false;
  @ViewChild(SidenavComponent) childComponent!: SidenavComponent;

  constructor(public authService: AuthService) {}

  onToggleSideNav(data: SideNavToggle): void {
    this.screenWidth = data.screenWidth;
    this.isSideNavCollapsed = data.collapsed;
  }

  toggleSideNav() {
    this.childComponent.toggleCollapse();
  }
}

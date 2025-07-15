import {
  animate,
  keyframes,
  state,
  style,
  transition,
  trigger,
} from "@angular/animations";
import {
  Component,
  OnInit,
  EventEmitter,
  Output,
  HostListener,
} from "@angular/core";
import { MatDialog } from "@angular/material";
import { Router } from "@angular/router";
import { ConformationBoxComponent } from "../dialogboxes/conformation-box/conformation-box.component";
import { CustomdialogboxComponent } from "../dialogboxes/customdialogbox/customdialogbox.component";
import { ApiRepositoryService } from "../services/api-repository.service";
import { MainService } from "../services/main.service";
import { SharedService } from "../services/shared.service";
import { navbarData } from "./nav-data";

interface SideNavToggle {
  screenWidth: number;
  collapsed: boolean;
}

@Component({
  selector: "app-sidenav",
  templateUrl: "./sidenav.component.html",
  styleUrls: ["./sidenav.component.scss"],
  animations: [
    trigger("rotate", [
      transition(":enter", [
        animate(
          "1000ms",
          keyframes([
            style({ transform: "rotate(0deg)", offset: "0" }),
            style({ transform: "rotate(2turn)", offset: "1" }),
          ])
        ),
      ]),
    ]),
    trigger("submenu", [
      state(
        "hidden",
        style({
          height: "0",
          overflow: "hidden",
        })
      ),
      state(
        "visible",
        style({
          height: "*",
        })
      ),
      transition("visible <=> hidden", [
        style({ overflow: "hidden" }),
        animate("{{transitionParams}}"),
      ]),
      transition("void => *", animate(0)),
    ]),
  ],
})
export class SidenavComponent implements OnInit {
  @Output() onToggleSideNav: EventEmitter<SideNavToggle> = new EventEmitter();
  collapsed = false;
  screenWidth = 0;
  navData = navbarData;

  animating: boolean | undefined;
  expanded: boolean | undefined;
  multiple: boolean = false;

  restExpansion: boolean = false;
  soapExpansion: boolean = false;

  public repositoryData: [] = [];
  public requestId: any = "";
  public repoId: any = "";
  private repoStateData: any = null;

  repoType: string = "";
  repoName: string = "";
  repoUId: string = "";

  search: string = "";

  constructor(
    private main: MainService,
    private router: Router,
    private shared: SharedService,
    private dialog: MatDialog,
    private apiRepo: ApiRepositoryService
  ) {
    this.apiRepo.RefreshRequired.subscribe((refresh) => {
      this.getAllRepos();
    });
  }

  @HostListener("window:resize", ["$event"])
  onResize(event: any) {
    this.screenWidth = window.innerWidth;
    if (this.screenWidth <= 768) {
      this.collapsed = false;
      this.onToggleSideNav.emit({
        collapsed: this.collapsed,
        screenWidth: this.screenWidth,
      });
    }
  }

  searchRepo(e: any) {
    let searchValue = e.target.value.trim();
    if (e.target.value === "" || e.keyCode === 8) {
      this.getAllRepos();
      return;
    }

    this.navData[0].items[0]["rest"] = this.navData[0].items[0]["rest"].filter(
      (repository) => {
        const repositorySearchData = repository.name.toLowerCase();

        return repositorySearchData.includes(searchValue.toLowerCase());
      }
    );
  }

  filterNested(repository) {
    repository.requests.filter((req) => {
      if (req.name.toLowerCase().includes(this.search.toLowerCase())) {
        return repository;
      }
    });
  }

  ngOnInit() {
    this.screenWidth = window.innerWidth;
    this.getAllRepos();

    this.shared.updateRequestSubjectData$.subscribe((data: any) => {
      if (data) {
        this.requestId.name = data.data.name;
        this.requestId.description = data.data.description;
      }
    });

    this.shared.updateRequestURLSubjectData$.subscribe((data: any) => {
      if (data) {
        this.requestId.endpointUrl = data.data.endpointUrl;
      }
    });

    this.shared.updateRequestMethodSubjectData$.subscribe((data: any) => {
      if (data) {
        this.requestId.method = data.data.method;
      }
    });
  }

  getAllRepos() {
    this.main.getAllRepositories(1).subscribe((data: any) => {
      this.repositoryData = data;
      this.navData[0].items = Array(data.data);
    });
  }

  toggleCollapse(): void {
    this.collapsed = !this.collapsed;
    this.onToggleSideNav.emit({
      collapsed: this.collapsed,
      screenWidth: this.screenWidth,
    });
  }

  closeSidenav(): void {
    this.collapsed = false;
    this.onToggleSideNav.emit({
      collapsed: this.collapsed,
      screenWidth: this.screenWidth,
    });
  }

  handleClick(item: any, isCapture: boolean): void {
    if (isCapture) {
      this.repoStateData = item;
    }

    // this.repoId = this.repoStateData;

    if (item.routerlink === "apirepository" && this.repoStateData !== null) {
      if (this.multiple) {
        if (this.navData[0].items && this.navData[0].items.length > 0) {
          for (let modelItem of this.navData[0].items) {
            if (item !== modelItem && modelItem.expanded) {
              modelItem.expanded = false;
            }
          }
        }
      }

      item.expanded = !item.expanded;
      this.repoName = item.name;
    } else {
      // this.repoId = item;
      if (this.multiple) {
        if (this.navData[0].items && this.navData[0].items.length > 0) {
          for (let modelItem of this.navData[0].items) {
            if (item !== modelItem && modelItem.expanded) {
              modelItem.expanded = false;
            }
          }
        }
      }

      item.expanded = !item.expanded;
      this.repoName = item.name;
    }
  }

  openDialog() {
    const dialogRef = this.dialog.open(CustomdialogboxComponent, {
      width: "500px",
    });

    dialogRef.afterClosed().subscribe((result) => {});
  }

  addNewRequest(repo) {
    const dialogRef = this.dialog.open(CustomdialogboxComponent, {
      width: "500px",
    });
    dialogRef.componentInstance.isRequest = true;
    dialogRef.componentInstance.isEdit = false;
    dialogRef.componentInstance.updateRepoId = repo.id;

    dialogRef.afterClosed().subscribe((result) => {});
  }

  editNewRequest(repo) {
    const dialogRef = this.dialog.open(CustomdialogboxComponent, {
      width: "500px",
    });

    dialogRef.componentInstance.isRepoEdit = true;
    dialogRef.componentInstance.repoData = repo;
    dialogRef.componentInstance.updateRepoId = repo.id;

    dialogRef.afterClosed().subscribe((result) => {});
  }

  deleteRepository(repo: any) {
    const dialogRef = this.dialog.open(ConformationBoxComponent, {
      width: "500px",
    });

    const { id } = repo;

    dialogRef.componentInstance.repoId = id;
    dialogRef.componentInstance.instance = "SIDENAV";
    dialogRef.componentInstance.isRepo = true;

    dialogRef.afterClosed().subscribe((result) => {});
  }

  deleteRequest(request: any) {
    const dialogRef = this.dialog.open(ConformationBoxComponent, {
      width: "500px",
    });

    dialogRef.componentInstance.requestId = request;
    dialogRef.componentInstance.instance = "SIDENAV";
    dialogRef.componentInstance.isRequest = true;

    dialogRef.afterClosed().subscribe((result) => {});
  }

  restExpansionHandler() {
    this.restExpansion = !this.restExpansion;
    this.router.navigateByUrl("apirepository");
    this.repoType = "REST";
  }
  soapExpansionHandler() {
    this.soapExpansion = !this.soapExpansion;
    this.router.navigateByUrl("apirepository");
    this.repoType = "SOAP";
  }

  getRequests(requests: any, item: any) {
    this.repoId = item;
    // this.repoStateData = item;
    this.requestId = requests;

    this.repoName = item.name;
    this.repoUId = requests.id;
    const params = {
      requestId: requests,
      repoId: this.repoId,
    };

    this.router.navigateByUrl("apirepository").then(() => {
      this.shared.repositorySubject.next(params);
    });
  }
}

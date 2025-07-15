import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { LoginComponent } from "./login/login.component";
import { ApiRepositoryModule } from "./pages/api-repository/api-repository.module";
import { AuthGuard } from "./guards/auth.guard";
import { ApiBoardComponent } from "./pages/api-repository/api-board/api-board.component";

const routes: Routes = [
  { path: "", redirectTo: "/login", pathMatch: "full" },
  { path: "login", component: LoginComponent },
  {
    path: "apirepository",
    // loadChildren: () =>
    //   import("./pages/api-repository/api-repository.module").then(
    //     (m) => m.ApiRepositoryModule
    //   ),
    component: ApiBoardComponent,
    canActivate: [AuthGuard],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule],
})
export class AppRoutingModule {}

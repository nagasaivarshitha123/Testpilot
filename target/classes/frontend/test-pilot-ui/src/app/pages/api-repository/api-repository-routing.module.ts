import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { ApiBoardComponent } from "./api-board/api-board.component";

const routes: Routes = [
  {
    path: "apirepository",
    component: ApiBoardComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ApiRepositoryRoutingModule {}

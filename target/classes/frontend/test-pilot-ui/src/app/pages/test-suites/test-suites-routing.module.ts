import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { ManageComponent } from "./manage/manage.component";
import { ReportsComponent } from "./reports/reports.component";

const routes: Routes = [
  {
    path: "testsuites",
    children: [
      { path: "manage", component: ManageComponent },
      { path: "reports", component: ReportsComponent },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TestSuitesRoutingModule {}

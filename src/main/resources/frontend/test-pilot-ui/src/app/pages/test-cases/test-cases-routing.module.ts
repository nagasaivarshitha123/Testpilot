import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { ManageComponent } from "./manage/manage.component";
import { ReportsComponent } from "./reports/reports.component";
import { TestCaseBoardComponent } from "./test-case-board/test-case-board.component";

const routes: Routes = [
  {
    path: "testcases",
    children: [
      {
        path: "manage",
        component: ManageComponent,
      },
      { path: "reports", component: ReportsComponent },
      {
        path: "requests",
        component: TestCaseBoardComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TestCasesRoutingModule {}

import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { CommonModule } from "@angular/common";

import { TestSuitesRoutingModule } from "./test-suites-routing.module";
import { ManageComponent } from "./manage/manage.component";
import { ReportsComponent } from "./reports/reports.component";
import { AddNewTestSuiteComponent } from "./add-new-test-suite/add-new-test-suite.component";
import { RunTestSuiteComponent } from "./run-test-suite/run-test-suite.component";
import {
  MatFormFieldModule,
  MatInputModule,
  MatSidenavModule,
  MatTreeModule,
  MatIconModule,
  MatCardModule,
  MatButtonModule,
  MatTabsModule,
  MatTableModule,
  MatDialogModule,
  MatSnackBarModule,
  MatExpansionModule,
  MatListModule,
  MatProgressBarModule,
  MatRippleModule,
  MatPaginatorModule,
  MatRadioModule,
  MatDatepickerModule,
  MatNativeDateModule,
  MatSelectModule,
  MatCheckboxModule,
} from "@angular/material";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { EditTestSuiteComponent } from "./edit-test-suite/edit-test-suite.component";
import { AddExistingTestCasesComponent } from "./add-existing-test-cases/add-existing-test-cases.component";
import { TooltipModule } from "primeng/tooltip";

@NgModule({
  declarations: [
    ManageComponent,
    ReportsComponent,
    AddNewTestSuiteComponent,
    RunTestSuiteComponent,
    EditTestSuiteComponent,
    AddExistingTestCasesComponent,
  ],
  imports: [
    CommonModule,
    TestSuitesRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSidenavModule,
    MatTreeModule,
    MatIconModule,
    MatCardModule,
    MatButtonModule,
    MatTabsModule,
    MatTableModule,
    MatDialogModule,
    MatSnackBarModule,
    MatExpansionModule,
    MatListModule,
    MatProgressBarModule,
    MatRippleModule,
    MatPaginatorModule,
    MatRadioModule,
    MatProgressBarModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule,
    TooltipModule,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class TestSuitesModule {}

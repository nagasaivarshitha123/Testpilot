import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { CommonModule } from "@angular/common";

import { TestCasesRoutingModule } from "./test-cases-routing.module";
import { TestCaseRequestComponent } from "./test-case-request/test-case-request.component";
import { TestCaseResponseComponent } from "./test-case-response/test-case-response.component";
import { TestCaseBoardComponent } from "./test-case-board/test-case-board.component";
import { ManageComponent } from "./manage/manage.component";
import { ReportsComponent } from "./reports/reports.component";
import { AddNewTestCaseComponent } from "./add-new-test-case/add-new-test-case.component";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
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
} from "@angular/material";
import { NgJsonEditorModule } from "ang-jsoneditor";
import { TooltipModule } from "primeng/tooltip";

@NgModule({
  declarations: [
    TestCaseRequestComponent,
    TestCaseResponseComponent,
    TestCaseBoardComponent,
    ManageComponent,
    ReportsComponent,
    AddNewTestCaseComponent,
  ],
  imports: [
    CommonModule,
    TestCasesRoutingModule,
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
    NgJsonEditorModule,
    TooltipModule,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class TestCasesModule {}

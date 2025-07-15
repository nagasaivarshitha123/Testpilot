import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";

import { ApiRepositoryRoutingModule } from "./api-repository-routing.module";
import { ApiResponseComponent } from "./api-response/api-response.component";
import { ApiBoardComponent } from "./api-board/api-board.component";
import { ApiRequestComponent } from "./api-request/api-request.component";
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
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { NgJsonEditorModule } from "ang-jsoneditor";
import { TooltipModule } from "primeng/tooltip";


@NgModule({
  declarations: [ApiResponseComponent, ApiBoardComponent, ApiRequestComponent],
  imports: [
    CommonModule,
    ApiRepositoryRoutingModule,
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
    TooltipModule
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ApiRepositoryModule {}

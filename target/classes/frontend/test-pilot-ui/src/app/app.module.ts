import { BrowserModule } from "@angular/platform-browser";
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";

import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { ApiRepositoryModule } from "./pages/api-repository/api-repository.module";
import { TestCasesModule } from "./pages/test-cases/test-cases.module";
import { TestSuitesModule } from "./pages/test-suites/test-suites.module";
import { HeaderComponent } from "./header/header.component";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
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
  MatMenuModule,
} from "@angular/material";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatSelectModule } from "@angular/material/select";
import { HttpClientModule } from "@angular/common/http";
import { SidenavComponent } from "./sidenav/sidenav.component";
import { BodyComponent } from "./body/body.component";
import { AddNewTestCaseComponent } from "./pages/test-cases/add-new-test-case/add-new-test-case.component";
import { CustomdialogboxComponent } from "./dialogboxes/customdialogbox/customdialogbox.component";
import { EsDialogboxComponent } from "./dialogboxes/es-dialogbox/es-dialogbox.component";
import { FiltersComponent } from "./dialogboxes/filters/filters.component";
import { AddNewTestSuiteComponent } from "./pages/test-suites/add-new-test-suite/add-new-test-suite.component";
import { RunTestSuiteComponent } from "./pages/test-suites/run-test-suite/run-test-suite.component";
import { EditTestSuiteComponent } from "./pages/test-suites/edit-test-suite/edit-test-suite.component";
import { TestCaseFiltersComponent } from "./dialogboxes/test-case-filters/test-case-filters.component";
import { ConformationBoxComponent } from "./dialogboxes/conformation-box/conformation-box.component";
import { NotifierModule, NotifierOptions } from "angular-notifier";
import { NgJsonEditorModule } from "ang-jsoneditor";
import { AddExistingTestCasesComponent } from "./pages/test-suites/add-existing-test-cases/add-existing-test-cases.component";
import { TooltipModule } from "primeng/tooltip";
import { LoginComponent } from "./login/login.component";

const customNotifierOptions: NotifierOptions = {
  position: {
    horizontal: {
      position: "right",
      distance: 12,
    },
    vertical: {
      position: "top",
      distance: 12,
      gap: 10,
    },
  },
  theme: "material",
  behaviour: {
    autoHide: 5000,
    onClick: "hide",
    onMouseover: "pauseAutoHide",
    showDismissButton: true,
    stacking: 4,
  },
  animations: {
    enabled: true,
    show: {
      preset: "slide",
      speed: 300,
      easing: "ease",
    },
    hide: {
      preset: "fade",
      speed: 300,
      easing: "ease",
      offset: 50,
    },
    shift: {
      speed: 300,
      easing: "ease",
    },
    overlap: 150,
  },
};

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    SidenavComponent,
    BodyComponent,
    CustomdialogboxComponent,
    EsDialogboxComponent,
    FiltersComponent,
    TestCaseFiltersComponent,
    ConformationBoxComponent,
    LoginComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ApiRepositoryModule,
    TestCasesModule,
    TestSuitesModule,
    BrowserAnimationsModule,
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
    HttpClientModule,
    MatProgressBarModule,
    MatDatepickerModule,
    MatNativeDateModule,
    NotifierModule.withConfig(customNotifierOptions),
    NgJsonEditorModule,
    TooltipModule,
    MatMenuModule,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  providers: [],
  bootstrap: [AppComponent],
  entryComponents: [
    CustomdialogboxComponent,
    EsDialogboxComponent,
    AddNewTestCaseComponent,
    FiltersComponent,
    AddNewTestSuiteComponent,
    RunTestSuiteComponent,
    EditTestSuiteComponent,
    TestCaseFiltersComponent,
    ConformationBoxComponent,
    AddExistingTestCasesComponent,
  ],
})
export class AppModule {}

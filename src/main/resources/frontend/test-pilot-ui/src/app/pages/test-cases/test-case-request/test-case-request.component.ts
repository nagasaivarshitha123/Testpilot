import {
  Input,
  ViewEncapsulation,
  Output,
  EventEmitter,
  ViewChild,
} from "@angular/core";
import { Component, OnInit } from "@angular/core";
import { FormArray, FormBuilder, FormGroup } from "@angular/forms";
import { MatDialog } from "@angular/material";
import { JsonEditorComponent, JsonEditorOptions } from "ang-jsoneditor";
import { CustomdialogboxComponent } from "src/app/dialogboxes/customdialogbox/customdialogbox.component";
import { SharedService } from "src/app/services/shared.service";
import { TestCaseService } from "src/app/services/test-case.service";
import { AuthType } from "typings";

export interface DropDown {
  value: string;
  viewValue: string;
}
@Component({
  selector: "app-test-case-request",
  templateUrl: "./test-case-request.component.html",
  styleUrls: ["./test-case-request.component.scss"],
  encapsulation: ViewEncapsulation.None,
})
export class TestCaseRequestComponent implements OnInit {
  @Input() requestBody: string = "";

  @Input() requestData: any = "";

  @Output() authEmitter = new EventEmitter<{}>();
  @Output() headerEmitter = new EventEmitter<{}>();

  @ViewChild(JsonEditorComponent) editor: JsonEditorComponent;
  options = new JsonEditorOptions();

  private putId: number | string = "";
  public headersLength: number = 0;

  public authType: AuthType = {
    authorization: {
      type: "No Auth",
      userName: "",
      password: "",
      token: "",
    },
  };

  home = {
    headerName: "",
    headerValue: "",
  };

  dataArray = [];

  authorizationObject: any = {};
  public assertions: any[] = [];

  public selectedIndex = 3;

  constructor(
    private _fb: FormBuilder,
    public dialog: MatDialog,
    private caseService: TestCaseService,
    private shared: SharedService
  ) {
    this.shared.subject.subscribe((data: any) => {
      this.assertions = data;
    });

    this.options.mode = "text";
    this.options.statusBar = false;
    this.options.onChange = () => (this.requestBody = this.editor.getText());
  }

  data = {};

  public addMore: FormGroup;
  headerUpdate: number = 0;

  methods: DropDown[] = [
    { value: "No Auth", viewValue: "No Auth" },
    { value: "Basic", viewValue: "Basic" },
    { value: "Bearer Token", viewValue: "Bearer Token" },
  ];

  get formArr() {
    return this.addMore.get("itemRows") as FormArray;
  }

  ngOnInit() {
    this.addMore = this._fb.group({
      itemRows: this._fb.array([this.initItemRows()]),
    });
  }

  ngOnChanges() {
    this.dataArray = [];
    this.authorizationObject = {};
    const { id, assertions, headers } = this.requestData;
    this.putId = id;

    this.data = this.requestData.body;

    this.assertions = assertions;

    this.authType.authorization.type = "No Auth";
    this.authType.authorization.password = "";
    this.authType.authorization.token = "";
    this.authType.authorization.userName = "";

    if (headers !== null) {
      this.headersUpdateSubject(headers);
      if (this.dataArray.length === 0) {
        const data = { headerName: "", headerValue: "" };
        this.dataArray.push(data);
        this.updateHeadersLength();
      }
    } else {
      if (headers === null) {
        if (this.dataArray.length === 0) {
          const data = { headerName: "", headerValue: "" };
          this.dataArray.push(data);
          this.updateHeadersLength();
        }
      }
    }
  }

  headersUpdateSubject(headers) {
    if (Array(headers).length === 0) {
      if (this.dataArray.length === 0) {
        const data = { headerName: "", headerValue: "" };
        this.dataArray.push(data);
      }
      this.updateHeadersLength();
    }

    if (headers && Array(headers).length > 0) {
      this.dataArray.splice(0, 1);
    }
    for (var i = 0; i < Object.keys(headers).length; i++) {
      const key1 = Object.keys(headers);
      const key2 = Object.values(headers);

      const data = { headerName: key1[i], headerValue: key2[i] };

      if (key1[i] === "Authorization") {
        this.setAuth(key1[i], key2[i]);
      }

      this.dataArray.push(data);
      this.updateHeadersLength();
    }

    const data = { headerName: "", headerValue: "" };
    this.dataArray.push(data);
    this.updateHeadersLength();

    this.headerUpdate = this.headerUpdate + 1;
  }

  setAuth(key1, key2) {
    const [type, decodeString] = key2.split(" ");

    if (type === null || type === undefined || type === "") {
      this.authType.authorization.type = "No Auth";
      return;
    }
    if (key1 && key2) {
      if (type === "Basic") {
        this.authType.authorization.type = type;
        const [userName, password] = window.atob(decodeString).split(" ");
        this.authType.authorization.userName = userName;
        this.authType.authorization.password = password;
      } else if (type === "Bearer") {
        this.authType.authorization.type = "Bearer Token";

        this.authType.authorization.token = decodeString;
      }
    } else {
      this.authType.authorization.userName = "";
      this.authType.authorization.password = "";
      this.authType.authorization.token = "";
      this.authType.authorization.type = "No Auth";
    }
  }

  initItemRows() {
    return this._fb.group({
      headerName: [""],
      headerValue: [""],
    });
  }

  changeHeader(e: any) {
    const { itemRows } = this.addMore.value;
    this.headerEmitter.emit(itemRows);
  }

  addNewRow() {
    this.formArr.push(this.initItemRows());
    this.updateHeadersLength();
    const { itemRows } = this.addMore.value;
    var array = itemRows;

    for (var i = 0; i < array.length; i++) {
      this.authorizationObject[array[i].headerName] = array[i].headerValue;
    }

    this.caseService
      .updateTestCaseHeaders(this.authorizationObject, this.putId)
      .subscribe((data: any) => {
        this.headersUpdateSubject(data.data);
      });

    this.headerEmitter.emit(this.authorizationObject);
  }

  addForm() {
    if (this.dataArray[this.headersLength].headerName === "") {
      this.caseService.errorMessage("Please enter header name");
      return;
    } else if (this.dataArray[this.headersLength].headerValue === "") {
      this.caseService.errorMessage("Please enter header value");
      return;
    }

    const data = { headerName: "", headerValue: "" };
    this.dataArray.push(data);
    this.updateHeadersLength();

    for (var i = 0; i < this.dataArray.length; i++) {
      if (this.dataArray[i].headerName !== "") {
        this.authorizationObject[this.dataArray[i].headerName] =
          this.dataArray[i].headerValue;
      }
    }

    this.caseService
      .updateTestCaseHeaders(this.authorizationObject, this.putId)
      .subscribe((data: any) => {
        this.dataArray = [];
        this.authorizationObject = {};
        this.headersUpdateSubject(data.data);
      });
  }

  addFormEditable(id: number) {
    if (this.dataArray[id].headerName === "") {
      this.caseService.errorMessage("Please enter header name");
      return;
    } else if (this.dataArray[id].headerValue === "") {
      this.caseService.errorMessage("Please enter header value");
      return;
    }

    for (var i = 0; i < this.dataArray.length; i++) {
      if (this.dataArray[i].headerName !== "") {
        this.authorizationObject[this.dataArray[i].headerName] =
          this.dataArray[i].headerValue;
      }
    }

    this.caseService
      .updateTestCaseHeaders(this.authorizationObject, this.putId)
      .subscribe((data: any) => {
        this.dataArray = [];
        this.authorizationObject = {};
        this.headersUpdateSubject(data.data);
      });
  }

  deleteRow(index: number) {
    this.formArr.removeAt(index);
    this.updateHeadersLength();

    const { itemRows } = this.addMore.value;
    var array = itemRows;

    for (var i = 0; i < array.length; i++) {
      this.authorizationObject[array[i].headerName] = array[i].headerValue;
    }

    this.caseService
      .updateTestCaseHeaders(this.authorizationObject, this.putId)
      .subscribe((data: any) => {});

    this.headerEmitter.emit(this.authorizationObject);
  }

  removeForm(index: number) {
    this.dataArray.splice(index, 1);
    this.updateHeadersLength();

    this.authorizationObject = {};

    for (var i = 0; i < this.dataArray.length; i++) {
      if (this.dataArray[i].headerName !== "") {
        this.authorizationObject[this.dataArray[i].headerName] =
          this.dataArray[i].headerValue;
      }
    }

    this.caseService
      .updateTestCaseHeaders(this.authorizationObject, this.putId)
      .subscribe((data: any) => {
        this.authorizationObject = {};
      });
  }

  clearRow(index: number) {}

  authReset() {
    this.authType.authorization.userName = "";
    this.authType.authorization.password = "";
    this.authType.authorization.token = "";
  }

  testAuthApply() {
    this.authorizationObject = {};

    for (var i = 0; i < this.dataArray.length; i++) {
      if (this.dataArray[i].headerName !== "") {
        this.authorizationObject[this.dataArray[i].headerName] =
          this.dataArray[i].headerValue;
      }
    }

    if (this.authType.authorization.type === "No Auth") {
      return;
    } else if (this.authType.authorization.type === "Bearer Token") {
      if (this.authType.authorization.token === "") {
        this.caseService.errorMessage("Please Enter Token");
        return;
      } else {
        this.authorizationObject.Authorization = `Bearer ${this.authType.authorization.token}`;
      }
    } else if (this.authType.authorization.type === "Basic") {
      let username = this.authType.authorization.userName;
      let password = this.authType.authorization.password;

      if (username === "") {
        this.caseService.errorMessage("Please Enter Username");
        return;
      }
      if (password === "") {
        this.caseService.errorMessage("Please Enter Password");
        return;
      }

      this.authorizationObject.Authorization =
        "Basic" + " " + window.btoa(username + " " + password);
    }

    this.caseService
      .updateTestCaseHeaders(this.authorizationObject, this.putId)
      .subscribe((data: any) => {
        this.dataArray = [];
        this.authorizationObject = {};
        this.headersUpdateSubject(data.data);
      });
  }

  updateHeadersLength() {
    this.headersLength = this.dataArray.length - 1;
  }

  addAssertionsDialog() {
    const dialogRef = this.dialog.open(CustomdialogboxComponent, {
      width: "500px",
    });
    dialogRef.componentInstance.isAssertions = true;
    dialogRef.componentInstance.assertionId = this.putId;
    dialogRef.componentInstance.assertionsList = this.assertions;

    dialogRef.afterClosed().subscribe((result) => {
      dialogRef.componentInstance.assertionsList = null;
    });
  }

  updateAssertionsDialog(assertion: any) {
    const dialogRef = this.dialog.open(CustomdialogboxComponent, {
      width: "500px",
    });
    dialogRef.componentInstance.isAssertions = true;
    dialogRef.componentInstance.assertionId = this.putId;
    dialogRef.componentInstance.assertionsList = this.assertions;
    dialogRef.componentInstance.specificAssertions = assertion;
    dialogRef.componentInstance.isAssertionUpdate = true;

    dialogRef.afterClosed().subscribe((result) => {
      dialogRef.componentInstance.assertionsList = null;
      dialogRef.componentInstance.specificAssertions = null;
    });
  }

  deleteAssertion(assertion: any) {
    this.assertions = this.assertions.filter((assertID) => {
      return assertID.id !== assertion.id;
    });

    this.caseService
      .updateAssertions(
        this.putId,
        JSON.stringify({ assertions: this.assertions })
      )
      .subscribe((data: any) => {
        const { assertions } = data.data;
        this.shared.subject.next(assertions);
      });
  }

  saveBody() {
    this.caseService
      .updateTestCaseBody(this.requestBody, this.putId)
      .subscribe((data) => {});
  }
}

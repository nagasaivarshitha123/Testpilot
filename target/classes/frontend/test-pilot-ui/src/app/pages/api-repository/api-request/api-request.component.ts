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
import { ApiRepositoryService } from "src/app/services/api-repository.service";
import { SharedService } from "src/app/services/shared.service";
import { AuthType, DropDown } from "typings";
import { JsonEditorComponent, JsonEditorOptions } from "ang-jsoneditor";

@Component({
  selector: "app-api-request",
  templateUrl: "./api-request.component.html",
  styleUrls: ["./api-request.component.scss"],
  encapsulation: ViewEncapsulation.None,
})
export class ApiRequestComponent implements OnInit {
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
  headerUpdate: number = 0;

  data = null;

  authorizationObject: any = {};
  constructor(
    private _fb: FormBuilder,
    public dialog: MatDialog,
    private apiRepoService: ApiRepositoryService,
    private shared: SharedService
  ) {
    this.options.mode = "text";
    this.options.statusBar = false;
    this.options.onChange = () => (this.requestBody = this.editor.getText());
  }

  public addMore: FormGroup;

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
    this.headerUpdate = 0;
    this.dataArray = [];
    this.authorizationObject = {};
    const { id, headers } = this.requestData;
    this.putId = id;

    this.authType.authorization.type = "No Auth";
    this.authType.authorization.password = "";
    this.authType.authorization.token = "";
    this.authType.authorization.userName = "";

    if (this.requestData.body === null) {
      this.data = {};
    } else {
      this.data = this.requestData.body;
    }

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

  initItemRows() {
    return this._fb.group({
      headerName: [""],
      headerValue: [""],
    });
  }

  setAuth(key1: any, key2: any) {
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
    this.apiRepoService
      .updateApiHeaders(this.authorizationObject, this.putId)
      .subscribe((data: any) => {
        this.dataArray = [];
        this.headersUpdateSubject(data.data);
      });
    this.headerEmitter.emit(this.authorizationObject);
  }

  addForm() {
    if (this.dataArray[this.headersLength].headerName === "") {
      this.apiRepoService.errorMessage("Please enter header name");
      return;
    } else if (this.dataArray[this.headersLength].headerValue === "") {
      this.apiRepoService.errorMessage("Please enter header value");
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

    this.apiRepoService
      .updateApiHeaders(this.authorizationObject, this.putId)
      .subscribe((data: any) => {
        this.dataArray = [];
        this.authorizationObject = {};
        this.headersUpdateSubject(data.data);
      });
  }

  addFormEditable(id: number) {
    if (this.dataArray[id].headerName === "") {
      this.apiRepoService.errorMessage("Please enter header name");
      return;
    } else if (this.dataArray[id].headerValue === "") {
      this.apiRepoService.errorMessage("Please enter header value");
      return;
    }

    for (var i = 0; i < this.dataArray.length; i++) {
      if (this.dataArray[i].headerName !== "") {
        this.authorizationObject[this.dataArray[i].headerName] =
          this.dataArray[i].headerValue;
      }
    }

    this.apiRepoService
      .updateApiHeaders(this.authorizationObject, this.putId)
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
    this.apiRepoService
      .updateApiHeaders(this.authorizationObject, this.putId)
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

    this.apiRepoService
      .updateApiHeaders(this.authorizationObject, this.putId)
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
        this.apiRepoService.errorMessage("Please Enter Token");
        return;
      } else {
        this.authorizationObject.Authorization = `Bearer ${this.authType.authorization.token}`;
      }
    } else if (this.authType.authorization.type === "Basic") {
      let username = this.authType.authorization.userName;
      let password = this.authType.authorization.password;

      if (username === "") {
        this.apiRepoService.errorMessage("Please Enter Username");
        return;
      }
      if (password === "") {
        this.apiRepoService.errorMessage("Please Enter Password");
        return;
      }

      this.authorizationObject.Authorization =
        "Basic" + " " + window.btoa(username + " " + password);
    }

    this.apiRepoService
      .updateApiHeaders(this.authorizationObject, this.putId)
      .subscribe((data: any) => {
        this.dataArray = [];
        this.authorizationObject = {};
        this.headersUpdateSubject(data.data);
      });
  }

  updateHeadersLength() {
    this.headersLength = this.dataArray.length - 1;
  }

  saveBody() {
    this.apiRepoService
      .updateApiBody(this.requestBody, this.putId)
      .subscribe((data) => {});
  }
}

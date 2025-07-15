import { Component, OnInit } from "@angular/core";
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  Validators,
} from "@angular/forms";
import { AuthService } from "../services/auth.service";
import { Router } from "@angular/router";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.scss"],
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  otpForm: FormGroup;
  isEmailVerified: boolean = false;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: [
        "",
        [Validators.required, Validators.email, this.emailDomainValidator],
      ],
    });

    this.otpForm = this.fb.group({
      otp: ["", [Validators.required]],
    });
  }

  ngOnInit() {}

  emailDomainValidator(control: AbstractControl): ValidationErrors | null {
    const email: string = control.value;
    if (email && !email.endsWith("@innocito.com")) {
      return { emailDomain: true };
    }
    return null;
  }

  get emailControl() {
    return this.loginForm.get("email")!;
  }

  getOtp() {
    console.log(this.loginForm.value);
    if (this.loginForm.valid) {
      this.auth.getOtp(this.loginForm.value.email).subscribe(
        (data) => {
          this.isEmailVerified = true;
        },
        (err) => {
          console.log(err);
        }
      );
    }
  }
  onSubmit() {
    if (this.loginForm.valid) {
      const data = {
        email: this.loginForm.value.email,
        password: this.otpForm.value.otp,
      };
      this.auth
        .verifyOtp(this.loginForm.value.email, this.otpForm.value.otp)
        .subscribe(
          (data) => {
            console.log("Form Submitted", data);
            this.router.navigate(["/apirepository"]);
          },
          (err) => {
            console.log(err);
          }
        );
    }
  }
}

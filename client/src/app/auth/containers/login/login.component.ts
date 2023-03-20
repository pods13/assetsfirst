import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['../auth.scss']
})
export class LoginComponent implements OnInit {

  loginForm!: UntypedFormGroup;

  constructor(private fb: UntypedFormBuilder,
              private authService: AuthService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: this.fb.control('', Validators.compose([Validators.required])),
      password: this.fb.control('', Validators.required),
    });
  }

  login() {
    const loginRequest = this.loginForm?.value;

    this.authService.login(loginRequest)
      .subscribe((user) => {
        this.router.navigate([this.authService.INITIAL_PATH])
      });
  }

}

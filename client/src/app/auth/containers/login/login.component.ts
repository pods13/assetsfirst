import { Component, OnInit } from '@angular/core';
import { NonNullableFormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['../auth.scss']
})
export class LoginComponent implements OnInit {

  loginForm = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  constructor(private fb: NonNullableFormBuilder,
              private authService: AuthService,
              private router: Router) {
  }

  ngOnInit(): void {
  }

  login() {
    const loginRequest = this.loginForm?.getRawValue();

    this.authService.login(loginRequest)
      .subscribe((user) => {
        this.router.navigate([this.authService.INITIAL_PATH])
      });
  }

}

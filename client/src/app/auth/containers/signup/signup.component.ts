import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  template: `
    <form [formGroup]="signupForm">
      <div class="header">Sign up</div>

      <mat-form-field>
        <input matInput type="text" id="username" placeholder="Username" autocomplete="username"
               formControlName="username"
               autofocus>
      </mat-form-field>

      <mat-form-field>
        <input matInput type="password" id="password" placeholder="Password" autocomplete="new-password"
               formControlName="password">
      </mat-form-field>

      <div class="actions">
        <button mat-flat-button color="primary" type="submit" (click)="signup()" [disabled]="!signupForm.valid">Create Account
        </button>
        <div class="separator">
          <span>OR</span>
        </div>
        <button mat-stroked-button type="button" routerLink="/login">Login</button>
      </div>
    </form>

  `,
  styleUrls: ['../auth.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SignupComponent implements OnInit {

  signupForm!: UntypedFormGroup;

  constructor(private fb: UntypedFormBuilder,
              private authService: AuthService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.signupForm = this.fb.group({
      username: this.fb.control('', Validators.compose([Validators.required])),
      password: this.fb.control('', Validators.required),
    });
  }

  signup() {
    const signupRequest = this.signupForm?.value;
    this.authService.signup(signupRequest)
      .subscribe((user) => {
        this.router.navigate([this.authService.INITIAL_PATH])
      });
  }

}

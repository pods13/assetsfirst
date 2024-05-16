import {ChangeDetectionStrategy, Component, Inject, OnInit} from '@angular/core';
import {FormGroup, NonNullableFormBuilder} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
    selector: 'app-change-password-dialog',
    template: `
    <form [formGroup]="form">
      <h2 mat-dialog-title>Change password</h2>
      <div mat-dialog-content class="content">
        <mat-form-field class="username-field">
          <input matInput type="text" id="username" placeholder="Username" autocomplete="username" formControlName="username">
        </mat-form-field>
        <mat-form-field *ngIf="!data.firstLogin">
          <input matInput type="password" id="currentPassword" placeholder="Current password"
                 autocomplete="old-password"
                 formControlName="currentPassword">
        </mat-form-field>
        <mat-form-field>
          <input matInput type="password" id="newPassword" placeholder="New Password" autocomplete="new-password"
                 formControlName="newPassword">
        </mat-form-field>

        <mat-form-field>
          <input matInput type="password" id="confirmNewPassword" placeholder="Confirm New Password" autocomplete="new-password"
                 formControlName="confirmNewPassword">
        </mat-form-field>
      </div>

      <div mat-dialog-actions class="actions" align="start">
        <button mat-flat-button color="accent" type="submit" [disabled]="!form.valid" (click)="onChangePasswordClick()">Save changes</button>
        <button mat-button mat-dialog-close>Cancel</button>
      </div>
    </form>

  `,
    styleUrls: ['./change-password-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChangePasswordDialogComponent implements OnInit {

    form!: FormGroup;

    constructor(private fb: NonNullableFormBuilder,
                private dialogRef: MatDialogRef<any>,
                @Inject(MAT_DIALOG_DATA) public data: ChangePasswordDialogData) {
    }

    ngOnInit(): void {
        const {username, firstLogin} = this.data;
        if (firstLogin) {
            this.form = this.fb.group({
                username,
                newPassword: '',
                confirmNewPassword: ''
            });
        } else {
            this.form = this.fb.group({
                username,
                currentPassword: '',
                newPassword: '',
                confirmNewPassword: ''
            });
        }
    }

    onChangePasswordClick() {
        this.dialogRef.close(this.form.value);
    }

}

export interface ChangePasswordDialogData {
    username: string;
    firstLogin: boolean;
}

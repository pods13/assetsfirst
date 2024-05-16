import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {AuthService} from '../../../../auth/services/auth.service';
import {MatDialog} from '@angular/material/dialog';
import {
    ChangePasswordDialogComponent,
    ChangePasswordDialogData
} from '../../components/change-password-dialog/change-password-dialog.component';
import {User} from '../../../../auth/types/user';
import {ChangePasswordDto} from '../../types/change-password.dto';
import {EMPTY, switchMap} from 'rxjs';
import {UserService} from '../../services/user.service';

@Component({
    selector: 'app-profile-container',
    template: `
    <mat-card appearance="outlined" class="profile-info" *ngIf="user$ | async as user">
      <mat-card-content>
        <p>{{user.username}}</p>
        <button mat-raised-button color="primary" (click)="openChangePasswordDialog(user)">Change password</button>
      </mat-card-content>
    </mat-card>
  `,
    styleUrls: ['./profile-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProfileContainerComponent implements OnInit {

    user$ = this.authService.getCurrentUser$();

    constructor(private authService: AuthService,
                private userService: UserService,
                public dialog: MatDialog) {
    }

    ngOnInit(): void {
    }

    openChangePasswordDialog(currentUser: User) {
        const dialogRef = this.dialog.open<ChangePasswordDialogComponent, ChangePasswordDialogData, ChangePasswordDto>(ChangePasswordDialogComponent, {
            disableClose: true,
            data: {
                username: currentUser.username,
                firstLogin: currentUser.firstLogin
            }
        });

        dialogRef.afterClosed().pipe(
            switchMap(result => {
                if (result) {
                    return this.userService.changePassword(result);
                }
                return EMPTY;
            }))
            .subscribe(() => console.debug(`User password was successfully changed`));
    }
}

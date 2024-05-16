import {Injectable} from '@angular/core';
import {MatSnackBar, MatSnackBarConfig, MatSnackBarRef, SimpleSnackBar} from '@angular/material/snack-bar';

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    public static readonly durationShort = 3000;
    public static readonly durationLong = 9000;
    public static readonly defaultDuration = 6000;
    private defaultAction = 'x';

    constructor(private matSnackBar: MatSnackBar) {
    }

    show(message: string, config: MatSnackBarConfig): MatSnackBarRef<SimpleSnackBar> {
        return this.matSnackBar.open(message, this.defaultAction, Object.assign({
            duration: NotificationService.defaultDuration,
            verticalPosition: 'top',
            horizontalPosition: 'right'
        }, config));
    }

    showWarning(message: string): MatSnackBarRef<SimpleSnackBar> {
        return this.show(message, {
            duration: NotificationService.durationShort,
            panelClass: ['snackbar-warning']
        });
    }

    showError(message: string): MatSnackBarRef<SimpleSnackBar> {
        return this.show(message, {
            duration: NotificationService.durationLong,
            panelClass: ['snackbar-error']
        });
    }
}

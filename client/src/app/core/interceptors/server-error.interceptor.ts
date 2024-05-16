import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {NotificationService} from '../services/notification.service';
import {AuthService} from '../../auth/services/auth.service';

@Injectable()
export class ServerErrorInterceptor implements HttpInterceptor {

    constructor(private notificationService: NotificationService,
                private authService: AuthService) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(req).pipe(
            catchError((err) => {
                if (err instanceof HttpErrorResponse) {
                    this.handleServerError(err);
                }
                return throwError(() => err);
            })
        );
    }

    private handleServerError(err: HttpErrorResponse): void {
        if (err.status === 0) {
            this.notificationService.showError(`Unable to establish connection to server, please try again later`);
        } else if (err.status === 401 && !err.url?.includes('/auth/user')) {
            this.authService.doLogoutAndRedirectToLogin()
                .then(() => this.notificationService.showError('Your session has expired. Please re-login to renew your session'));
            ;
        }
    }
}

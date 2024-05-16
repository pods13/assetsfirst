import {Injectable} from '@angular/core';
import {Observable, tap} from 'rxjs';
import {AuthService} from '../services/auth.service';
import {Router} from '@angular/router';

@Injectable({
    providedIn: "root",
})
export class AppGuard {
    constructor(private authService: AuthService, private router: Router) {
    }

    canActivate(): Observable<boolean> {
        return this.canLoad();
    }

    canLoad(): Observable<boolean> {
        return this.authService.isLoggedIn$().pipe(
            tap((isLoggedIn) => {
                if (!isLoggedIn) {
                    this.authService.doLogoutAndRedirectToLogin()
                        .then(() => {
                        });
                }
            })
        );
    }
}

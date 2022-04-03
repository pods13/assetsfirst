import { CanActivate, Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { map, Observable, tap } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {
  }

  canActivate(): Observable<boolean> {
    return this.authService.isLoggedIn$().pipe(
      tap((isLoggedIn) => {
        if (isLoggedIn) {
          this.router.navigate([this.authService.INITIAL_PATH]);
        }
      }),
      map((isLoggedIn) => !isLoggedIn)
    );
  }

}

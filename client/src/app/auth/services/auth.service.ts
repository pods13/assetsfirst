import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { UserCredentials } from '../types/user-credentials';
import { catchError, concat, map, Observable, of, switchMap, tap, toArray } from 'rxjs';
import { User } from '../types/user';
import { MatDialog } from '@angular/material/dialog';
import { CreateUserDto } from '../types/create-user.dto';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  public readonly INITIAL_PATH = "app";

  private loggedUser!: User | null;

  constructor(private router: Router,
              private http: HttpClient,
              private dialog: MatDialog) {
  }

  login(userCredentials: UserCredentials) {
    const formData = new FormData();
    formData.append('username', userCredentials.username);
    formData.append('password', userCredentials.password);
    return this.http.post<User>(`/auth/login`, formData)
      .pipe(tap((data) => {
        this.doLoginUser(data)
      }));
  }

  private doLoginUser(user: User): void {
    this.loggedUser = user;
  }

  getCurrentUser() {
    if (this.loggedUser) {
      return of(this.loggedUser);
    }
    return this.http.get<User>('/auth/user')
      .pipe(tap((user) => (this.loggedUser = user)));
  }

  isLoggedIn$(): Observable<boolean> {
    return this.getCurrentUser().pipe(
      map((user) => !!user),
      catchError(() => of(false))
    );
  }

  logout() {
    return this.http.post('/auth/logout', {})
      .pipe(switchMap(() => this.doLogoutAndRedirectToLogin()))
  }

  doLogoutAndRedirectToLogin() {
    this.doLogout();
    this.dialog.closeAll();
    return this.router.navigate(['login']);
  }

  private doLogout() {
    this.loggedUser = null;
  }

  signup(dto: CreateUserDto) {
    return this.http.post<User>(`/auth/signup`, dto);
  }

  signupAsAnonymousUser() {
    return this.generateAnonymousUser().pipe(
      switchMap((dto) => {
        return concat(this.signup(dto), this.login({username: dto.username, password: dto.password})).pipe(toArray());
      })
    );
  }

  generateAnonymousUser() {
    return this.http.get<CreateUserDto>('/auth/user/generate');
  }
}

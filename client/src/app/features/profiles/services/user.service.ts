import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ChangePasswordDto } from '../types/change-password.dto';
import { User } from '../../../auth/types/user';
import { AuthService } from '../../../auth/services/auth.service';
import { tap } from 'rxjs';

@Injectable()
export class UserService {

  constructor(private http: HttpClient,
              private auth: AuthService) {
  }

  changePassword(dto: ChangePasswordDto) {
    return this.http.post<User>(`/users/change-password`, dto)
      .pipe(tap(this.auth.setLoggedUser));
  }
}

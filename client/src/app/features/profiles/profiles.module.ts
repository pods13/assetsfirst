import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileContainerComponent } from './containers/profile-container/profile-container.component';
import { ProfilesRoutingModule } from './profiles-routing.module';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { ChangePasswordDialogComponent } from './components/change-password-dialog/change-password-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import { UserService } from './services/user.service';



@NgModule({
  declarations: [
    ProfileContainerComponent,
    ChangePasswordDialogComponent
  ],
  imports: [
    CommonModule,
    ProfilesRoutingModule,
    MatCardModule,
    MatButtonModule,
    MatDialogModule,
    MatInputModule,
    ReactiveFormsModule
  ],
  providers: [
    UserService
  ]
})
export class ProfilesModule { }

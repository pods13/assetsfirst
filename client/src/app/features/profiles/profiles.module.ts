import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileContainerComponent } from './containers/profile-container/profile-container.component';
import { ProfilesRoutingModule } from './profiles-routing.module';
import { MatLegacyCardModule as MatCardModule } from '@angular/material/legacy-card';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { ChangePasswordDialogComponent } from './components/change-password-dialog/change-password-dialog.component';
import { MatLegacyDialogModule as MatDialogModule } from '@angular/material/legacy-dialog';
import { MatLegacyInputModule as MatInputModule } from '@angular/material/legacy-input';
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

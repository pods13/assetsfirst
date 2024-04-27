import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SingleColumnLayoutComponent } from './single-column-layout/single-column-layout.component';
import { RouterModule } from '@angular/router';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatLegacyProgressBarModule as MatProgressBarModule } from '@angular/material/legacy-progress-bar';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatLegacyListModule as MatListModule } from '@angular/material/legacy-list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatLegacyMenuModule as MatMenuModule } from '@angular/material/legacy-menu';



@NgModule({
  declarations: [
    SingleColumnLayoutComponent
  ],
    imports: [
        CommonModule,
        RouterModule,
        MatButtonModule,
        MatProgressBarModule,
        MatToolbarModule,
        MatIconModule,
        MatListModule,
        MatSidenavModule,
        MatMenuModule
    ],
  exports: [
    SingleColumnLayoutComponent
  ]
})
export class LayoutModule { }

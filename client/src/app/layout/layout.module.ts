import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SingleColumnLayoutComponent } from './single-column-layout/single-column-layout.component';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatMenuModule } from '@angular/material/menu';



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

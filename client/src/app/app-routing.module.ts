import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SingleColumnLayoutComponent } from './layout/single-column-layout/single-column-layout.component';
import { LayoutModule } from './layout/layout.module';
import { AppGuard } from './auth/guards/app.guard';
import { ViewsModule } from './views/views.module';
import { HomePageComponent } from './views/home-page/home-page.component';

const routes: Routes = [
  {
    path: '', component: HomePageComponent, pathMatch: 'full'
  },
  {
    path: 'app',
    component: SingleColumnLayoutComponent,
    canActivate: [AppGuard],
    children: [
      {path: '', redirectTo: 'portfolio', pathMatch: 'full'},
      {
        path: 'trades',
        loadChildren: () => import('./features/trades/trades.module').then(m => m.TradesModule)
      },
      {
        path: 'portfolio',
        loadChildren: () => import('./features/portfolios/portfolios.module').then(m => m.PortfoliosModule)
      },
      {
        path: 'positions',
        loadChildren: () => import('./features/positions/portfolio-positions.module').then(m => m.PortfolioPositionsModule)
      }
    ]
  }
];

@NgModule({
  imports: [
    LayoutModule,
    ViewsModule,
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

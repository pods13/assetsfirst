import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SingleColumnLayoutComponent } from './layout/single-column-layout/single-column-layout.component';
import { LayoutModule } from './layout/layout.module';
import { AppGuard } from './auth/guards/app.guard';

const routes: Routes = [
  {
    path: '', redirectTo: 'login', pathMatch: 'full'
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
        path: 'holdings',
        loadChildren: () => import('./features/holdings/portfolio-holdings.module').then(m => m.PortfolioHoldingsModule)
      }
    ]
  }
];

@NgModule({
  imports: [
    LayoutModule,
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

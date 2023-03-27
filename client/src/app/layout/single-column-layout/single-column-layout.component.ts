import { Component, OnInit } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { map, shareReplay } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';

@Component({
  selector: 'app-single-column-layout',
  templateUrl: './single-column-layout.component.html',
  styleUrls: ['./single-column-layout.component.scss']
})
export class SingleColumnLayoutComponent implements OnInit {

  isHandset$ = this.breakpointObserver.observe([Breakpoints.Handset]).pipe(
    map(result => result.matches),
    shareReplay()
  );

  loadingRoute = false;

  constructor(private breakpointObserver: BreakpointObserver,
              private authService: AuthService) { }

  ngOnInit(): void {
  }

  logout() {
    this.authService.logout().subscribe(() => console.debug('logout'));
  }
}

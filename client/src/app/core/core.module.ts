import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HTTP_INTERCEPTORS, HttpXsrfTokenExtractor } from '@angular/common/http';
import { ApiPrefixInterceptor } from './interceptors/api-prefix.interceptor';
import { XhrInterceptor } from './interceptors/xhr.interceptor';
import { MatLegacySnackBarModule as MatSnackBarModule } from '@angular/material/legacy-snack-bar';
import { ServerErrorInterceptor } from './interceptors/server-error.interceptor';
import { RxStompService, rxStompServiceFactory } from './services/rx-stomp.service';
import { TagCategoryService } from '@core/services/tag-category.service';
import { MatExpansionModule } from '@angular/material/expansion';
import { ReactiveFormsModule } from '@angular/forms';
import { MatLegacyFormFieldModule as MatFormFieldModule } from '@angular/material/legacy-form-field';
import { MatLegacyChipsModule as MatChipsModule } from '@angular/material/legacy-chips';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatIconModule } from '@angular/material/icon';
import { MatLegacyInputModule as MatInputModule } from '@angular/material/legacy-input';


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    MatSnackBarModule,
    MatExpansionModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatChipsModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule
  ],
  providers: [
    {
      provide: RxStompService,
      useFactory: rxStompServiceFactory,
      deps: [HttpXsrfTokenExtractor]
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ApiPrefixInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: XhrInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ServerErrorInterceptor,
      multi: true
    },
    TagCategoryService
  ],
  exports: []
})
export class CoreModule {
}

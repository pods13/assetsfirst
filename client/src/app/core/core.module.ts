import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HTTP_INTERCEPTORS, HttpXsrfTokenExtractor} from '@angular/common/http';
import {ApiPrefixInterceptor} from './interceptors/api-prefix.interceptor';
import {XhrInterceptor} from './interceptors/xhr.interceptor';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {ServerErrorInterceptor} from './interceptors/server-error.interceptor';
import {RxStompService, rxStompServiceFactory} from './services/rx-stomp.service';
import {TagCategoryService} from '@core/services/tag-category.service';
import {MatButtonModule} from '@angular/material/button';


@NgModule({
    declarations: [],
    imports: [
        CommonModule,
        MatSnackBarModule,
        MatButtonModule,
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

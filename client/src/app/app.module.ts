import {LOCALE_ID, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {CoreModule} from '@core/core.module';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AuthModule} from './auth/auth.module';
import {registerLocaleData} from '@angular/common';
import localeRu from '@angular/common/locales/ru';
import localeRuExtra from '@angular/common/locales/extra/ru';
import {MAT_DIALOG_DEFAULT_OPTIONS} from '@angular/material/dialog';
import {MAT_FORM_FIELD_DEFAULT_OPTIONS} from "@angular/material/form-field";

registerLocaleData(localeRu, 'ru-RU', localeRuExtra);

@NgModule({
    declarations: [
        AppComponent,
    ],
    imports: [
        BrowserModule,
        HttpClientModule,
        CoreModule,
        BrowserAnimationsModule,
        AuthModule,
        AppRoutingModule,
    ],
    providers: [
        {provide: LOCALE_ID, useValue: 'ru-RU'},
        {
            provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: {
                height: '100vh',
                width: '100vw',
                maxWidth: '512px',
                position: {
                    top: '0',
                    right: '0'
                },
            }
        },
        {provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {appearance: 'outline'}}
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}

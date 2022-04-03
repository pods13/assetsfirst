import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class UploadFileService {

  constructor(private http: HttpClient) {
  }

  upload(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(`/trades/upload`, formData, {responseType: 'json'});
  }

}

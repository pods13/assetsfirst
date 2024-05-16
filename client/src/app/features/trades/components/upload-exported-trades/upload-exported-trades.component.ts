import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {UploadFileService} from '../../services/upload-file.service';

@Component({
    selector: 'app-upload-exported-trades',
    template: `
    <input type="file" (change)="selectFile($event)"/>
    <button mat-button (click)="upload()">Upload File</button>
  `,
    styleUrls: ['./upload-exported-trades.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class UploadExportedTradesComponent implements OnInit {

    selectedFiles?: FileList | null;

    constructor(private uploadFileService: UploadFileService) {
    }

    ngOnInit(): void {
    }

    selectFile(event: Event) {
        const element = event.target as HTMLInputElement;
        this.selectedFiles = element.files
    }

    upload(): void {
        if (!this.selectedFiles?.length || !this.selectedFiles[0]) {
            return;
        }

        const file = this.selectedFiles[0];
        this.uploadFileService.upload(file).subscribe(res => {
            console.log(res.message);
        });
    }
}

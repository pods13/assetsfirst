import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {AbstractControl, FormArray, FormGroup, NonNullableFormBuilder, Validators} from '@angular/forms';
import {AnnualDividendProjection, DividendIncomeCard} from '../../types/in/dividend-income-card';

@Component({
    selector: 'app-edit-dividend-income-card',
    template: `
    <h1 mat-dialog-title>Edit Dividend Income Card</h1>
    <div mat-dialog-content>
      <h2>Configure custom allocation type</h2>
      <form [formGroup]="form">
        <mat-checkbox formControlName="useCustomDividendProjections">Use custom dividend projections</mat-checkbox>
        <div *ngIf="form.get('useCustomDividendProjections')?.value">
          <ng-container formArrayName="annualDividendProjections">
            <ng-container *ngFor="let projectionControl of annualDividendProjections.controls; let i = index">
              <div class="projection-form-row" [formGroup]="toFormGroup(projectionControl)">
                <mat-form-field appearance="fill">
                  <input matInput formControlName="ticker" placeholder="Ticker">
                </mat-form-field>
                <mat-form-field appearance="fill">
                  <input matInput formControlName="dividend" placeholder="Dividend">
                </mat-form-field>
                <mat-form-field appearance="fill">
                  <input matInput formControlName="currency" placeholder="Currency">
                </mat-form-field>
                <button mat-icon-button aria-label="Delete dividend projection" (click)="deleteAnnualDividendProjection(i)">
                  <mat-icon>delete</mat-icon>
                </button>
              </div>
            </ng-container>
          </ng-container>
          <button mat-button color="primary" (click)="addEmptyProjection()">Add one more projection</button>
        </div>
      </form>
    </div>
    <div mat-dialog-actions>
      <button mat-button (click)="handleOkClick()">Ok</button>
      <button mat-button [mat-dialog-close]="null">Cancel</button>
    </div>
  `,
    styleUrls: ['./edit-dividend-income-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditDividendIncomeCardComponent implements OnInit {

    form!: FormGroup;

    constructor(private cd: ChangeDetectorRef,
                @Inject(MAT_DIALOG_DATA) public data: { title: string; card: DividendIncomeCard },
                public dialogRef: MatDialogRef<EditDividendIncomeCardComponent>,
                private formBuilder: NonNullableFormBuilder) {
        this.form = this.formBuilder.group({
            useCustomDividendProjections: data.card.useCustomDividendProjections,
            annualDividendProjections: this.formBuilder.array([])
        });
        console.log(data.card)
        if (!data.card.annualDividendProjections || data.card.annualDividendProjections?.length === 0) {
            this.addEmptyProjection();
        } else {
            data.card.annualDividendProjections?.forEach(p => this.addAnnualDividendProjection(p));
        }
    }

    ngOnInit(): void {
    }

    addAnnualDividendProjection(projection: AnnualDividendProjection) {
        const dividendProjectionForm = this.formBuilder.group({
            ticker: [projection.ticker, Validators.required],
            dividend: [projection.dividend, Validators.required],
            currency: [projection.currency, Validators.required],
        });
        this.annualDividendProjections.push(dividendProjectionForm);
    }

    deleteAnnualDividendProjection(annualDividendProjectionIndex: number) {
        this.annualDividendProjections.removeAt(annualDividendProjectionIndex);
    }

    get annualDividendProjections() {
        return this.form.controls["annualDividendProjections"] as FormArray<any>;
    }

    handleOkClick() {
        console.log(this.form.value);
        this.dialogRef.close(this.form.value);
    }

    toFormGroup(projectionControl: AbstractControl<any>) {
        return projectionControl as FormGroup;
    }

    addEmptyProjection() {
        this.addAnnualDividendProjection({ticker: '', dividend: 0, currency: 'RUB'});
    }
}

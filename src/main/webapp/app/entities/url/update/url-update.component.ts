import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUrl } from '../url.model';
import { UrlService } from '../service/url.service';
import { UrlFormService, UrlFormGroup } from './url-form.service';

@Component({
  standalone: true,
  selector: 'jhi-url-update',
  templateUrl: './url-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class UrlUpdateComponent implements OnInit {
  isSaving = false;
  url: IUrl | null = null;

  protected urlService = inject(UrlService);
  protected urlFormService = inject(UrlFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: UrlFormGroup = this.urlFormService.createUrlFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ url }) => {
      this.url = url;
      if (url) {
        this.updateForm(url);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const url = this.urlFormService.getUrl(this.editForm);
    if (url.id !== null) {
      this.subscribeToSaveResponse(this.urlService.update(url));
    } else {
      this.subscribeToSaveResponse(this.urlService.create(url));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUrl>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(url: IUrl): void {
    this.url = url;
    this.urlFormService.resetForm(this.editForm, url);
  }
}

import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IUrl, NewUrl } from '../url.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUrl for edit and NewUrlFormGroupInput for create.
 */
type UrlFormGroupInput = IUrl | PartialWithRequiredKeyOf<NewUrl>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IUrl | NewUrl> = Omit<T, 'creationDateTime' | 'expirationDateTime'> & {
  creationDateTime?: string | null;
  expirationDateTime?: string | null;
};

type UrlFormRawValue = FormValueOf<IUrl>;

type NewUrlFormRawValue = FormValueOf<NewUrl>;

type UrlFormDefaults = Pick<NewUrl, 'id' | 'creationDateTime' | 'expirationDateTime'>;

type UrlFormGroupContent = {
  id: FormControl<UrlFormRawValue['id'] | NewUrl['id']>;
  shortUrl: FormControl<UrlFormRawValue['shortUrl']>;
  fullUrl: FormControl<UrlFormRawValue['fullUrl']>;
  creationDateTime: FormControl<UrlFormRawValue['creationDateTime']>;
  expirationDateTime: FormControl<UrlFormRawValue['expirationDateTime']>;
};

export type UrlFormGroup = FormGroup<UrlFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UrlFormService {
  createUrlFormGroup(url: UrlFormGroupInput = { id: null }): UrlFormGroup {
    const urlRawValue = this.convertUrlToUrlRawValue({
      ...this.getFormDefaults(),
      ...url,
    });
    return new FormGroup<UrlFormGroupContent>({
      id: new FormControl(
        { value: urlRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      shortUrl: new FormControl(urlRawValue.shortUrl),
      fullUrl: new FormControl(urlRawValue.fullUrl),
      creationDateTime: new FormControl(urlRawValue.creationDateTime),
      expirationDateTime: new FormControl(urlRawValue.expirationDateTime),
    });
  }

  getUrl(form: UrlFormGroup): IUrl | NewUrl {
    return this.convertUrlRawValueToUrl(form.getRawValue() as UrlFormRawValue | NewUrlFormRawValue);
  }

  resetForm(form: UrlFormGroup, url: UrlFormGroupInput): void {
    const urlRawValue = this.convertUrlToUrlRawValue({ ...this.getFormDefaults(), ...url });
    form.reset(
      {
        ...urlRawValue,
        id: { value: urlRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): UrlFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      creationDateTime: currentTime,
      expirationDateTime: currentTime,
    };
  }

  private convertUrlRawValueToUrl(rawUrl: UrlFormRawValue | NewUrlFormRawValue): IUrl | NewUrl {
    return {
      ...rawUrl,
      creationDateTime: dayjs(rawUrl.creationDateTime, DATE_TIME_FORMAT),
      expirationDateTime: dayjs(rawUrl.expirationDateTime, DATE_TIME_FORMAT),
    };
  }

  private convertUrlToUrlRawValue(
    url: IUrl | (Partial<NewUrl> & UrlFormDefaults),
  ): UrlFormRawValue | PartialWithRequiredKeyOf<NewUrlFormRawValue> {
    return {
      ...url,
      creationDateTime: url.creationDateTime ? url.creationDateTime.format(DATE_TIME_FORMAT) : undefined,
      expirationDateTime: url.expirationDateTime ? url.expirationDateTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}

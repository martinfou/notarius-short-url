import dayjs from 'dayjs/esm';

import { IUrl, NewUrl } from './url.model';

export const sampleWithRequiredData: IUrl = {
  id: 16359,
};

export const sampleWithPartialData: IUrl = {
  id: 9997,
  creationDateTime: dayjs('2024-08-24T06:00'),
  expirationDateTime: dayjs('2024-08-24T00:37'),
};

export const sampleWithFullData: IUrl = {
  id: 11888,
  shortUrl: 'however eek',
  fullUrl: 'moist',
  creationDateTime: dayjs('2024-08-23T21:41'),
  expirationDateTime: dayjs('2024-08-24T09:20'),
};

export const sampleWithNewData: NewUrl = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

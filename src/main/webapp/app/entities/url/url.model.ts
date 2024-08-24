import dayjs from 'dayjs/esm';

export interface IUrl {
  id: number;
  shortUrl?: string | null;
  fullUrl?: string | null;
  creationDateTime?: dayjs.Dayjs | null;
  expirationDateTime?: dayjs.Dayjs | null;
}

export type NewUrl = Omit<IUrl, 'id'> & { id: null };

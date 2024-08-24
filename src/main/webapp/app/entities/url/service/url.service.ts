import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUrl, NewUrl } from '../url.model';

export type PartialUpdateUrl = Partial<IUrl> & Pick<IUrl, 'id'>;

type RestOf<T extends IUrl | NewUrl> = Omit<T, 'creationDateTime' | 'expirationDateTime'> & {
  creationDateTime?: string | null;
  expirationDateTime?: string | null;
};

export type RestUrl = RestOf<IUrl>;

export type NewRestUrl = RestOf<NewUrl>;

export type PartialUpdateRestUrl = RestOf<PartialUpdateUrl>;

export type EntityResponseType = HttpResponse<IUrl>;
export type EntityArrayResponseType = HttpResponse<IUrl[]>;

@Injectable({ providedIn: 'root' })
export class UrlService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/urls');

  create(url: NewUrl): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(url);
    return this.http.post<RestUrl>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(url: IUrl): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(url);
    return this.http
      .put<RestUrl>(`${this.resourceUrl}/${this.getUrlIdentifier(url)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(url: PartialUpdateUrl): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(url);
    return this.http
      .patch<RestUrl>(`${this.resourceUrl}/${this.getUrlIdentifier(url)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestUrl>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestUrl[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getUrlIdentifier(url: Pick<IUrl, 'id'>): number {
    return url.id;
  }

  compareUrl(o1: Pick<IUrl, 'id'> | null, o2: Pick<IUrl, 'id'> | null): boolean {
    return o1 && o2 ? this.getUrlIdentifier(o1) === this.getUrlIdentifier(o2) : o1 === o2;
  }

  addUrlToCollectionIfMissing<Type extends Pick<IUrl, 'id'>>(urlCollection: Type[], ...urlsToCheck: (Type | null | undefined)[]): Type[] {
    const urls: Type[] = urlsToCheck.filter(isPresent);
    if (urls.length > 0) {
      const urlCollectionIdentifiers = urlCollection.map(urlItem => this.getUrlIdentifier(urlItem));
      const urlsToAdd = urls.filter(urlItem => {
        const urlIdentifier = this.getUrlIdentifier(urlItem);
        if (urlCollectionIdentifiers.includes(urlIdentifier)) {
          return false;
        }
        urlCollectionIdentifiers.push(urlIdentifier);
        return true;
      });
      return [...urlsToAdd, ...urlCollection];
    }
    return urlCollection;
  }

  protected convertDateFromClient<T extends IUrl | NewUrl | PartialUpdateUrl>(url: T): RestOf<T> {
    return {
      ...url,
      creationDateTime: url.creationDateTime?.toJSON() ?? null,
      expirationDateTime: url.expirationDateTime?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restUrl: RestUrl): IUrl {
    return {
      ...restUrl,
      creationDateTime: restUrl.creationDateTime ? dayjs(restUrl.creationDateTime) : undefined,
      expirationDateTime: restUrl.expirationDateTime ? dayjs(restUrl.expirationDateTime) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestUrl>): HttpResponse<IUrl> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestUrl[]>): HttpResponse<IUrl[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}

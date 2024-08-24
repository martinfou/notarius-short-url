import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { UrlService } from '../service/url.service';
import { IUrl } from '../url.model';
import { UrlFormService } from './url-form.service';

import { UrlUpdateComponent } from './url-update.component';

describe('Url Management Update Component', () => {
  let comp: UrlUpdateComponent;
  let fixture: ComponentFixture<UrlUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let urlFormService: UrlFormService;
  let urlService: UrlService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UrlUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(UrlUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UrlUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    urlFormService = TestBed.inject(UrlFormService);
    urlService = TestBed.inject(UrlService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const url: IUrl = { id: 456 };

      activatedRoute.data = of({ url });
      comp.ngOnInit();

      expect(comp.url).toEqual(url);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUrl>>();
      const url = { id: 123 };
      jest.spyOn(urlFormService, 'getUrl').mockReturnValue(url);
      jest.spyOn(urlService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ url });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: url }));
      saveSubject.complete();

      // THEN
      expect(urlFormService.getUrl).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(urlService.update).toHaveBeenCalledWith(expect.objectContaining(url));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUrl>>();
      const url = { id: 123 };
      jest.spyOn(urlFormService, 'getUrl').mockReturnValue({ id: null });
      jest.spyOn(urlService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ url: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: url }));
      saveSubject.complete();

      // THEN
      expect(urlFormService.getUrl).toHaveBeenCalled();
      expect(urlService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUrl>>();
      const url = { id: 123 };
      jest.spyOn(urlService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ url });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(urlService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

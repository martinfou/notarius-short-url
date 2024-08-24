import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: 'ea9b1057-4330-41c7-924d-6d631c9fd101',
};

export const sampleWithPartialData: IAuthority = {
  name: '2c2db3dc-7a1f-44d2-8ae4-81dbf15097a8',
};

export const sampleWithFullData: IAuthority = {
  name: 'ac2191dd-1b55-4290-b8cd-5ba5c1d6b589',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 7917,
  login: '!$l$4@dLep\\;7t\\2JjJo\\5RnX\\<yLL',
};

export const sampleWithPartialData: IUser = {
  id: 14291,
  login: 'SVCg@ATnJ\\AT\\2I1',
};

export const sampleWithFullData: IUser = {
  id: 18452,
  login: 'jQc2',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

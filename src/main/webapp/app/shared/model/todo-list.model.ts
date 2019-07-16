import { Moment } from 'moment';
import { ITask } from 'app/shared/model/task.model';

export interface ITODOList {
  id?: number;
  title?: string;
  description?: string;
  dateCreated?: Moment;
  tasks?: ITask[];
}

export const defaultValue: Readonly<ITODOList> = {};

import { ITODOList } from 'app/shared/model/todo-list.model';

export interface ITask {
  id?: number;
  title?: string;
  description?: string;
  tODOList?: ITODOList;
}

export const defaultValue: Readonly<ITask> = {};

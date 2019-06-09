import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ITODOList, defaultValue } from 'app/shared/model/todo-list.model';

export const ACTION_TYPES = {
  SEARCH_TODOLISTS: 'tODOList/SEARCH_TODOLISTS',
  FETCH_TODOLIST_LIST: 'tODOList/FETCH_TODOLIST_LIST',
  FETCH_TODOLIST: 'tODOList/FETCH_TODOLIST',
  CREATE_TODOLIST: 'tODOList/CREATE_TODOLIST',
  UPDATE_TODOLIST: 'tODOList/UPDATE_TODOLIST',
  DELETE_TODOLIST: 'tODOList/DELETE_TODOLIST',
  RESET: 'tODOList/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ITODOList>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type TODOListState = Readonly<typeof initialState>;

// Reducer

export default (state: TODOListState = initialState, action): TODOListState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_TODOLISTS):
    case REQUEST(ACTION_TYPES.FETCH_TODOLIST_LIST):
    case REQUEST(ACTION_TYPES.FETCH_TODOLIST):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_TODOLIST):
    case REQUEST(ACTION_TYPES.UPDATE_TODOLIST):
    case REQUEST(ACTION_TYPES.DELETE_TODOLIST):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_TODOLISTS):
    case FAILURE(ACTION_TYPES.FETCH_TODOLIST_LIST):
    case FAILURE(ACTION_TYPES.FETCH_TODOLIST):
    case FAILURE(ACTION_TYPES.CREATE_TODOLIST):
    case FAILURE(ACTION_TYPES.UPDATE_TODOLIST):
    case FAILURE(ACTION_TYPES.DELETE_TODOLIST):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_TODOLISTS):
    case SUCCESS(ACTION_TYPES.FETCH_TODOLIST_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_TODOLIST):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_TODOLIST):
    case SUCCESS(ACTION_TYPES.UPDATE_TODOLIST):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_TODOLIST):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/todo-lists';
const apiSearchUrl = 'api/_search/todo-lists';

// Actions

export const getSearchEntities: ICrudSearchAction<ITODOList> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_TODOLISTS,
  payload: axios.get<ITODOList>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<ITODOList> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_TODOLIST_LIST,
  payload: axios.get<ITODOList>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<ITODOList> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_TODOLIST,
    payload: axios.get<ITODOList>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<ITODOList> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_TODOLIST,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<ITODOList> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_TODOLIST,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<ITODOList> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_TODOLIST,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});

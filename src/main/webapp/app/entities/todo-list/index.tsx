import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import TODOList from './todo-list';
import TODOListDetail from './todo-list-detail';
import TODOListUpdate from './todo-list-update';
import TODOListDeleteDialog from './todo-list-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={TODOListUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={TODOListUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={TODOListDetail} />
      <ErrorBoundaryRoute path={match.url} component={TODOList} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={TODOListDeleteDialog} />
  </>
);

export default Routes;

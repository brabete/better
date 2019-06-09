/* tslint:disable no-unused-expression */
import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import TODOListComponentsPage from './todo-list.page-object';
import { TODOListDeleteDialog } from './todo-list.page-object';
import TODOListUpdatePage from './todo-list-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('TODOList e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let tODOListUpdatePage: TODOListUpdatePage;
  let tODOListComponentsPage: TODOListComponentsPage;
  let tODOListDeleteDialog: TODOListDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();

    await signInPage.username.sendKeys('admin');
    await signInPage.password.sendKeys('admin');
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();
    await waitUntilDisplayed(navBarPage.entityMenu);
  });

  it('should load TODOLists', async () => {
    await navBarPage.getEntityPage('todo-list');
    tODOListComponentsPage = new TODOListComponentsPage();
    expect(await tODOListComponentsPage.getTitle().getText()).to.match(/TODO Lists/);
  });

  it('should load create TODOList page', async () => {
    await tODOListComponentsPage.clickOnCreateButton();
    tODOListUpdatePage = new TODOListUpdatePage();
    expect(await tODOListUpdatePage.getPageTitle().getText()).to.match(/Create or edit a TODOList/);
    await tODOListUpdatePage.cancel();
  });

  it('should create and save TODOLists', async () => {
    async function createTODOList() {
      await tODOListComponentsPage.clickOnCreateButton();
      await tODOListUpdatePage.setTitleInput('title');
      expect(await tODOListUpdatePage.getTitleInput()).to.match(/title/);
      await tODOListUpdatePage.setDescriptionInput('description');
      expect(await tODOListUpdatePage.getDescriptionInput()).to.match(/description/);
      await tODOListUpdatePage.setDateCreatedInput('01-01-2001');
      expect(await tODOListUpdatePage.getDateCreatedInput()).to.eq('2001-01-01');
      await waitUntilDisplayed(tODOListUpdatePage.getSaveButton());
      await tODOListUpdatePage.save();
      await waitUntilHidden(tODOListUpdatePage.getSaveButton());
      expect(await tODOListUpdatePage.getSaveButton().isPresent()).to.be.false;
    }

    await createTODOList();
    await tODOListComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeCreate = await tODOListComponentsPage.countDeleteButtons();
    await createTODOList();

    await tODOListComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await tODOListComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last TODOList', async () => {
    await tODOListComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await tODOListComponentsPage.countDeleteButtons();
    await tODOListComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    tODOListDeleteDialog = new TODOListDeleteDialog();
    expect(await tODOListDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/betterApp.tODOList.delete.question/);
    await tODOListDeleteDialog.clickOnConfirmButton();

    await tODOListComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await tODOListComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});

import { element, by, ElementFinder } from 'protractor';

export default class TaskUpdatePage {
  pageTitle: ElementFinder = element(by.id('betterApp.task.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  titleInput: ElementFinder = element(by.css('input#task-title'));
  descriptionInput: ElementFinder = element(by.css('input#task-description'));
  tODOListSelect: ElementFinder = element(by.css('select#task-tODOList'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setTitleInput(title) {
    await this.titleInput.sendKeys(title);
  }

  async getTitleInput() {
    return this.titleInput.getAttribute('value');
  }

  async setDescriptionInput(description) {
    await this.descriptionInput.sendKeys(description);
  }

  async getDescriptionInput() {
    return this.descriptionInput.getAttribute('value');
  }

  async tODOListSelectLastOption() {
    await this.tODOListSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async tODOListSelectOption(option) {
    await this.tODOListSelect.sendKeys(option);
  }

  getTODOListSelect() {
    return this.tODOListSelect;
  }

  async getTODOListSelectedOption() {
    return this.tODOListSelect.element(by.css('option:checked')).getText();
  }

  async save() {
    await this.saveButton.click();
  }

  async cancel() {
    await this.cancelButton.click();
  }

  getSaveButton() {
    return this.saveButton;
  }
}

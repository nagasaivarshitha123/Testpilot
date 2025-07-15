import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddNewTestCaseComponent } from './add-new-test-case.component';

describe('AddNewTestCaseComponent', () => {
  let component: AddNewTestCaseComponent;
  let fixture: ComponentFixture<AddNewTestCaseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddNewTestCaseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddNewTestCaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

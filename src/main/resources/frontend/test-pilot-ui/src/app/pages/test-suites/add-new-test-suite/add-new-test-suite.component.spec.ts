import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddNewTestSuiteComponent } from './add-new-test-suite.component';

describe('AddNewTestSuiteComponent', () => {
  let component: AddNewTestSuiteComponent;
  let fixture: ComponentFixture<AddNewTestSuiteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddNewTestSuiteComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddNewTestSuiteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

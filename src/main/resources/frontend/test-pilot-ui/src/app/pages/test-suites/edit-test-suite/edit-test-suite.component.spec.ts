import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditTestSuiteComponent } from './edit-test-suite.component';

describe('EditTestSuiteComponent', () => {
  let component: EditTestSuiteComponent;
  let fixture: ComponentFixture<EditTestSuiteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditTestSuiteComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditTestSuiteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

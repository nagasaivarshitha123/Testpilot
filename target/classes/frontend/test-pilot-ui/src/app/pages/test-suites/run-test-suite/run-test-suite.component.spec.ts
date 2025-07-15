import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RunTestSuiteComponent } from './run-test-suite.component';

describe('RunTestSuiteComponent', () => {
  let component: RunTestSuiteComponent;
  let fixture: ComponentFixture<RunTestSuiteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RunTestSuiteComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RunTestSuiteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

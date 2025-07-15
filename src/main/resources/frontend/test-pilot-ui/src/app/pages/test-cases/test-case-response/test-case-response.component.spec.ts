import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestCaseResponseComponent } from './test-case-response.component';

describe('TestCaseResponseComponent', () => {
  let component: TestCaseResponseComponent;
  let fixture: ComponentFixture<TestCaseResponseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TestCaseResponseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestCaseResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

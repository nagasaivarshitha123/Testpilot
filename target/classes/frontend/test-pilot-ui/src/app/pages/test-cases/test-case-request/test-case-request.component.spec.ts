import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestCaseRequestComponent } from './test-case-request.component';

describe('TestCaseRequestComponent', () => {
  let component: TestCaseRequestComponent;
  let fixture: ComponentFixture<TestCaseRequestComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TestCaseRequestComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestCaseRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

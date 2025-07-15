import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestCaseFiltersComponent } from './test-case-filters.component';

describe('TestCaseFiltersComponent', () => {
  let component: TestCaseFiltersComponent;
  let fixture: ComponentFixture<TestCaseFiltersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TestCaseFiltersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestCaseFiltersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

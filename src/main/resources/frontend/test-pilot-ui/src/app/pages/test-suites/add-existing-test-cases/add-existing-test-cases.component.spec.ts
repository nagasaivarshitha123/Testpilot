import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddExistingTestCasesComponent } from './add-existing-test-cases.component';

describe('AddExistingTestCasesComponent', () => {
  let component: AddExistingTestCasesComponent;
  let fixture: ComponentFixture<AddExistingTestCasesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddExistingTestCasesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddExistingTestCasesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

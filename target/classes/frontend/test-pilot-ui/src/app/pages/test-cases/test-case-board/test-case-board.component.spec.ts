import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestCaseBoardComponent } from './test-case-board.component';

describe('TestCaseBoardComponent', () => {
  let component: TestCaseBoardComponent;
  let fixture: ComponentFixture<TestCaseBoardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TestCaseBoardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestCaseBoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ApiBoardComponent } from './api-board.component';

describe('ApiBoardComponent', () => {
  let component: ApiBoardComponent;
  let fixture: ComponentFixture<ApiBoardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ApiBoardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApiBoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

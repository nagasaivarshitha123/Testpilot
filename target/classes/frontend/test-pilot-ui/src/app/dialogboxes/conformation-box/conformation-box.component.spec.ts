import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConformationBoxComponent } from './conformation-box.component';

describe('ConformationBoxComponent', () => {
  let component: ConformationBoxComponent;
  let fixture: ComponentFixture<ConformationBoxComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConformationBoxComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConformationBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

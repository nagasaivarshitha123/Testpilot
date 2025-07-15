import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EsDialogboxComponent } from './es-dialogbox.component';

describe('EsDialogboxComponent', () => {
  let component: EsDialogboxComponent;
  let fixture: ComponentFixture<EsDialogboxComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EsDialogboxComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EsDialogboxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

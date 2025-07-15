import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomdialogboxComponent } from './customdialogbox.component';

describe('CustomdialogboxComponent', () => {
  let component: CustomdialogboxComponent;
  let fixture: ComponentFixture<CustomdialogboxComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CustomdialogboxComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomdialogboxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

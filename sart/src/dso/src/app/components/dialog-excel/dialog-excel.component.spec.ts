import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogExcelComponent } from './dialog-excel.component';

describe('DialogExcelComponent', () => {
  let component: DialogExcelComponent;
  let fixture: ComponentFixture<DialogExcelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogExcelComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DialogExcelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

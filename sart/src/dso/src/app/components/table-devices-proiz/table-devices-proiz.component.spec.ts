import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableDevicesProizComponent } from './table-devices-proiz.component';

describe('TableDevicesProizComponent', () => {
  let component: TableDevicesProizComponent;
  let fixture: ComponentFixture<TableDevicesProizComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TableDevicesProizComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TableDevicesProizComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

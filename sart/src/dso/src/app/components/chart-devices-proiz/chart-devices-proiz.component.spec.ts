import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartDevicesProizComponent } from './chart-devices-proiz.component';

describe('ChartDevicesProizComponent', () => {
  let component: ChartDevicesProizComponent;
  let fixture: ComponentFixture<ChartDevicesProizComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChartDevicesProizComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChartDevicesProizComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

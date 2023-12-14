import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartZbirPPIComponent } from './chart-zbir-ppi.component';

describe('ChartZbirPPIComponent', () => {
  let component: ChartZbirPPIComponent;
  let fixture: ComponentFixture<ChartZbirPPIComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChartZbirPPIComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChartZbirPPIComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

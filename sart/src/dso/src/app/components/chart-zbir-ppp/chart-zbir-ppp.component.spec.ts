import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartZbirPPPComponent } from './chart-zbir-ppp.component';

describe('ChartZbirPPPComponent', () => {
  let component: ChartZbirPPPComponent;
  let fixture: ComponentFixture<ChartZbirPPPComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChartZbirPPPComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChartZbirPPPComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

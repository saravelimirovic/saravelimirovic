import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TablePrediction7daysComponent } from './table-prediction7days.component';

describe('TablePrediction7daysComponent', () => {
  let component: TablePrediction7daysComponent;
  let fixture: ComponentFixture<TablePrediction7daysComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TablePrediction7daysComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TablePrediction7daysComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

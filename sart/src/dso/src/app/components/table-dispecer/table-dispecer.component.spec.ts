import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableDispecerComponent } from './table-dispecer.component';

describe('TableDispecerComponent', () => {
  let component: TableDispecerComponent;
  let fixture: ComponentFixture<TableDispecerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TableDispecerComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TableDispecerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

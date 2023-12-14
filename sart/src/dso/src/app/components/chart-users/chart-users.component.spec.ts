import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartUsersComponent } from './chart-users.component';

describe('ChartUsersComponent', () => {
  let component: ChartUsersComponent;
  let fixture: ComponentFixture<ChartUsersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChartUsersComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChartUsersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

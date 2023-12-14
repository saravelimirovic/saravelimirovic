import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartKorisniciComponent } from './chart-korisnici.component';

describe('ChartKorisniciComponent', () => {
  let component: ChartKorisniciComponent;
  let fixture: ComponentFixture<ChartKorisniciComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChartKorisniciComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChartKorisniciComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

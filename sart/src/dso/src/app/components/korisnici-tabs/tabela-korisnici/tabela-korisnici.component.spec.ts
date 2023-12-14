import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TabelaKorisniciComponent } from './tabela-korisnici.component';

describe('TabelaKorisniciComponent', () => {
  let component: TabelaKorisniciComponent;
  let fixture: ComponentFixture<TabelaKorisniciComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TabelaKorisniciComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TabelaKorisniciComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

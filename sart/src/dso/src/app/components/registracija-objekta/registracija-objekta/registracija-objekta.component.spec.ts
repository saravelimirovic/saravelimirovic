import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegistracijaObjektaComponent } from './registracija-objekta.component';

describe('RegistracijaObjektaComponent', () => {
  let component: RegistracijaObjektaComponent;
  let fixture: ComponentFixture<RegistracijaObjektaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegistracijaObjektaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegistracijaObjektaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

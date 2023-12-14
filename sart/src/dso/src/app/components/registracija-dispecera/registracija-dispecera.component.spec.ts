import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegistracijaDispeceraComponent } from './registracija-dispecera.component';

describe('RegistracijaDispeceraComponent', () => {
  let component: RegistracijaDispeceraComponent;
  let fixture: ComponentFixture<RegistracijaDispeceraComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegistracijaDispeceraComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegistracijaDispeceraComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

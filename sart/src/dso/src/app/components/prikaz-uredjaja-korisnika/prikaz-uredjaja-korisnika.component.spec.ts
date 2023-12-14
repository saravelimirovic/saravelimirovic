import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrikazUredjajaKorisnikaComponent } from './prikaz-uredjaja-korisnika.component';

describe('PrikazUredjajaKorisnikaComponent', () => {
  let component: PrikazUredjajaKorisnikaComponent;
  let fixture: ComponentFixture<PrikazUredjajaKorisnikaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PrikazUredjajaKorisnikaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PrikazUredjajaKorisnikaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

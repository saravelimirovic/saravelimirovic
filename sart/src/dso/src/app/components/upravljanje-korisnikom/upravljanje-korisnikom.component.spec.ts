import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpravljanjeKorisnikomComponent } from './upravljanje-korisnikom.component';

describe('UpravljanjeKorisnikomComponent', () => {
  let component: UpravljanjeKorisnikomComponent;
  let fixture: ComponentFixture<UpravljanjeKorisnikomComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpravljanjeKorisnikomComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpravljanjeKorisnikomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

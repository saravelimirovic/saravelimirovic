import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogRegistracijaComponent } from './dialog-registracija.component';

describe('DialogRegistracijaComponent', () => {
  let component: DialogRegistracijaComponent;
  let fixture: ComponentFixture<DialogRegistracijaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogRegistracijaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DialogRegistracijaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

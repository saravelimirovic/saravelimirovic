import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogRegistracija1Component } from './dialog-registracija1.component';

describe('DialogRegistracija1Component', () => {
  let component: DialogRegistracija1Component;
  let fixture: ComponentFixture<DialogRegistracija1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogRegistracija1Component ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DialogRegistracija1Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

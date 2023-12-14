import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SwitchUredjajaComponent } from './switch-uredjaja.component';

describe('SwitchUredjajaComponent', () => {
  let component: SwitchUredjajaComponent;
  let fixture: ComponentFixture<SwitchUredjajaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SwitchUredjajaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SwitchUredjajaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

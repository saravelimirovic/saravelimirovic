import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KontrolaModalComponent } from './kontrola-modal.component';

describe('KontrolaModalComponent', () => {
  let component: KontrolaModalComponent;
  let fixture: ComponentFixture<KontrolaModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ KontrolaModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(KontrolaModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

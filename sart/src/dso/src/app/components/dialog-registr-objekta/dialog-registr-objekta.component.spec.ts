import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogRegistrObjektaComponent } from './dialog-registr-objekta.component';

describe('DialogRegistrObjektaComponent', () => {
  let component: DialogRegistrObjektaComponent;
  let fixture: ComponentFixture<DialogRegistrObjektaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogRegistrObjektaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DialogRegistrObjektaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogObjectComponent } from './dialog-object.component';

describe('DialogObjectComponent', () => {
  let component: DialogObjectComponent;
  let fixture: ComponentFixture<DialogObjectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogObjectComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DialogObjectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

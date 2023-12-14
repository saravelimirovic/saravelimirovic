import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UredjajiComponent } from './uredjaji.component';

describe('UredjajiComponent', () => {
  let component: UredjajiComponent;
  let fixture: ComponentFixture<UredjajiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UredjajiComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UredjajiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

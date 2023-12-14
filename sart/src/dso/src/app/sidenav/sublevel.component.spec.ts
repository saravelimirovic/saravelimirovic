import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SublevelComponent } from './sublevel.component';

describe('SublevelComponent', () => {
  let component: SublevelComponent;
  let fixture: ComponentFixture<SublevelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SublevelComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SublevelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

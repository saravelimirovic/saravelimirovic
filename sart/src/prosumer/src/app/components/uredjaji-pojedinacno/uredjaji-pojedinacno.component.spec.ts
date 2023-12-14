import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UredjajiPojedinacnoComponent } from './uredjaji-pojedinacno.component';

describe('UredjajiPojedinacnoComponent', () => {
  let component: UredjajiPojedinacnoComponent;
  let fixture: ComponentFixture<UredjajiPojedinacnoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UredjajiPojedinacnoComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UredjajiPojedinacnoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

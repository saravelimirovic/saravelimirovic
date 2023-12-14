import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SkladistePojedinacnoComponent } from './skladiste-pojedinacno.component';

describe('SkladistePojedinacnoComponent', () => {
  let component: SkladistePojedinacnoComponent;
  let fixture: ComponentFixture<SkladistePojedinacnoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SkladistePojedinacnoComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SkladistePojedinacnoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

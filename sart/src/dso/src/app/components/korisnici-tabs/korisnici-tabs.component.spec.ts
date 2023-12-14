import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KorisniciTabsComponent } from './korisnici-tabs.component';

describe('KorisniciTabsComponent', () => {
  let component: KorisniciTabsComponent;
  let fixture: ComponentFixture<KorisniciTabsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ KorisniciTabsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(KorisniciTabsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

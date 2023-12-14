import { TestBed } from '@angular/core/testing';

import { TooltipDayService } from './tooltip-day.service';

describe('TooltipDayService', () => {
  let service: TooltipDayService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TooltipDayService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

import { TestBed } from '@angular/core/testing';

import { TooltipWeekHistoryService } from './tooltip-week-history.service';

describe('TooltipWeekHistoryService', () => {
  let service: TooltipWeekHistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TooltipWeekHistoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

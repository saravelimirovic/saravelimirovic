import { TestBed } from '@angular/core/testing';

import { TooltipShowEndService } from './tooltip-show-end.service';

describe('TooltipShowEndService', () => {
  let service: TooltipShowEndService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TooltipShowEndService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

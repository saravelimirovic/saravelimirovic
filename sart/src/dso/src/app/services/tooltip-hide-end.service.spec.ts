import { TestBed } from '@angular/core/testing';

import { TooltipHideEndService } from './tooltip-hide-end.service';

describe('TooltipHideEndService', () => {
  let service: TooltipHideEndService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TooltipHideEndService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

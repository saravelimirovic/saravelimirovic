import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TooltipWeekHistoryService {
  messageSource: BehaviorSubject<string> = new BehaviorSubject('');

  constructor() { }
}

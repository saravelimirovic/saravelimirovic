import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HideTooltipWeekService {
  messageSource: BehaviorSubject<string> = new BehaviorSubject('');
  constructor() { }
}

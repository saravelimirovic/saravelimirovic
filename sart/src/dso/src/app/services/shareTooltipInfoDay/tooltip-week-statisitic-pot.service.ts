import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TooltipWeekStatisiticPotService {
  messageSource: BehaviorSubject<string> = new BehaviorSubject('');

  constructor() { }
}

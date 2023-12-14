import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HideTooltipWeekPredictService {
  messageSource: BehaviorSubject<string> = new BehaviorSubject('');

  constructor() { }
}

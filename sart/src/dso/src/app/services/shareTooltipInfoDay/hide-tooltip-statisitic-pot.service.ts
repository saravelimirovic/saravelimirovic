import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HideTooltipStatisiticPotService {
  messageSource: BehaviorSubject<string> = new BehaviorSubject('');

  constructor() { }
}

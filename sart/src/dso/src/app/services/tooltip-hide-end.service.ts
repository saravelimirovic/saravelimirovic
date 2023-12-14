import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TooltipHideEndService {
  messageSource: BehaviorSubject<string> = new BehaviorSubject('');
  
  constructor() { }
}

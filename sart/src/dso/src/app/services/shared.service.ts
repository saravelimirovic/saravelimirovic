import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
@Injectable()
export class SharedService {

    messageSource: BehaviorSubject<string> = new BehaviorSubject('');

    constructor() { }
}

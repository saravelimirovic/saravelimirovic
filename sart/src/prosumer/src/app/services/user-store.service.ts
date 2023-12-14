import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserStoreService {
private userId$ = new BehaviorSubject<string>("");
private fullName$ = new BehaviorSubject<string>("");
private role$ = new BehaviorSubject<string>("");
constructor() { }

  public getUserId(){
    return this.userId$.asObservable();
  }

  public setUserId(id:string){
    this.userId$.next(id);
  }

  public getRole(){
    return this.role$.asObservable();
  }

  public setRole(role:string){
    this.role$.next(role);
  }

  public getFullName(){
    return this.fullName$.asObservable();
  }

  public setFullName(value:string){
    this.fullName$.next(value);
  }
}

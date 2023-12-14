import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'app/environment/environment';
import { ResetPassword } from 'app/models/resetPassword';

@Injectable({
  providedIn: 'root'
})
export class ResetPasswordService {
  private baseUrl = environment.apiUrl;
  constructor(private http: HttpClient) { }

  sendResetPasswordLink(email: string) {
    return this.http.post<any>(`${this.baseUrl}/api/User/send-reset-email/${email}`, {})
  }

  resetPassword(resetPasswordObj: ResetPassword) {
    return this.http.post<any>(`${this.baseUrl}/api/User/reset-password/`, resetPasswordObj)
  }
}

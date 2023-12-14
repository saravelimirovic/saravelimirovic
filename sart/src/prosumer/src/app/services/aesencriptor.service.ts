import { Injectable } from '@angular/core';
import { environment } from '../../environment/environment';
import * as CryptoJS from 'crypto-js';

@Injectable({
  providedIn: 'root'
})
export class AESencriptorService {

  private static key: string = environment.securityKey
  private static iv: string = environment.securityIv

  constructor() { }

  static encriptyString(str: string){
    var key = CryptoJS.enc.Utf8.parse(this.key)
    var iv = CryptoJS.enc.Utf8.parse(this.iv)
    var encrypted = CryptoJS.AES.encrypt(CryptoJS.enc.Utf8.parse(str.toString()), key,
    {
      FeedbackSize: 128,
      iv: iv,
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.Pkcs7
    })

    return encrypted.toString()
  }

  static decriptyString(str: string){
    var key = CryptoJS.enc.Utf8.parse(this.key)
    var iv = CryptoJS.enc.Utf8.parse(this.iv)
    var decrypted = CryptoJS.AES.decrypt(str, key,
    {
      FeedbackSize: 128,
      iv: iv,
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.Pkcs7
    })

    return decrypted.toString(CryptoJS.enc.Utf8)
  }
}

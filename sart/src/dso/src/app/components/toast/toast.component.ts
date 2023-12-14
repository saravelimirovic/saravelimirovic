import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.scss'],
  animations: [
    trigger('openClose', [
      state(
        'closed',
        style({
          visibility: 'hidden',
          right: '-200px',
        })
      ),
      state(
        'open',
        style({
          right: '40px',
        })
      ),
      transition('open <=> closed', [animate('0.3s ease-in-out')]),
    ]),
  ],
})
export class ToastComponent implements OnInit {
  @ViewChild('element', { static: false }) progressBar: ElementRef;
  progressInterval;

  constructor(public toastService: ToastService) {
    this.toastService.open.subscribe((data) => {
      if (data.show) {
        this.countDown();
      }
    });
  }

  ngOnInit() {}

  countDown() {
    this.progressBar.nativeElement.style.width =
      this.toastService.data.progressWidth;

    this.progressInterval = setInterval(() => {
      const width = parseInt(this.progressBar.nativeElement.style.width, 10);

      if (width <= 0) {
        this.toastService.hide();
        clearInterval(this.progressInterval);
        return;
      }

      this.toastService.data.progressWidth = String(width - 2);
      this.progressBar.nativeElement.style.width =
        this.toastService.data.progressWidth + '%';
    }, 100);
  }

  stopCountDown() {
    clearInterval(this.progressInterval);
  }
}
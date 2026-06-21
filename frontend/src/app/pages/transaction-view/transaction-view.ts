import {Component, OnInit, ElementRef, ViewChild, ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { Transaction } from '../../core/models/models';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import {MatProgressSpinner} from '@angular/material/progress-spinner';

@Component({
  selector: 'app-transaction-view',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule, // এই লাইনের কারণে 'routerLink' এররটি চলে যাবে
    MatButtonModule,
    MatIconModule,
    MatProgressSpinner
  ],
  templateUrl: './transaction-view.html',
  styleUrls: ['./transaction-view.scss']
})
export class TransactionView implements OnInit {
  transaction?: Transaction;

  @ViewChild('pdfContent', { static: false }) pdfContent!: ElementRef;

  constructor(private route: ActivatedRoute, private apiService: ApiService, private changeDetectorRef: ChangeDetectorRef) {}

  ngOnInit(): void {
    const txId = parseInt(this.route.snapshot.paramMap.get('id'))!;
    this.apiService.getTransactionById(txId).subscribe(data => {
      this.transaction = data
      this.changeDetectorRef.markForCheck()
    });
  }

  exportToPDF() {
    const data = this.pdfContent.nativeElement;

    html2canvas(data, { scale: 2 }).then(canvas => {
      const imgWidth = 190;
      const imgHeight = (canvas.height * imgWidth) / canvas.width;
      const contentDataURL = canvas.toDataURL('image/png');

      const pdf = new jsPDF('p', 'mm', 'a4');
      pdf.addImage(contentDataURL, 'PNG', 10, 10, imgWidth, imgHeight);
      pdf.save(`Receipt_UUID_${this.transaction?.transactionUuid}.pdf`);
    });
  }
}

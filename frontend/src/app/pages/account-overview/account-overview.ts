import {Component, OnInit, ViewChild, ElementRef, ChangeDetectorRef} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {ApiService} from '../../core/services/api.service';
import {Account, Transaction} from '../../core/models/models';
import {Chart, registerables} from 'chart.js';
import {MatCard, MatCardContent} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';
import {MatListItem, MatNavList} from '@angular/material/list';
import {MatProgressSpinner} from '@angular/material/progress-spinner'; // এই মডিউলটি mat-spinner এরর দূর করবে
import {DatePipe, DecimalPipe} from '@angular/common';

Chart.register(...registerables);

@Component({
  selector: 'app-account-overview',
  imports: [
    MatListItem,
    MatNavList,
    RouterLink,
    MatIcon,
    MatProgressSpinner,
    DatePipe,
    MatCardContent,
    MatCard,
    DecimalPipe
  ],
  templateUrl: './account-overview.html',
  styleUrl: './account-overview.scss',
})
export class AccountOverview implements OnInit {
  accountId!: number;
  account?: Account;
  transactions: Transaction[] = [];

  page = 0;
  size = 10;
  loading = false;
  hasMore = true;

  chart: any;
  @ViewChild('balanceChart', {static: false}) balanceChartCanvas!: ElementRef;

  constructor(private route: ActivatedRoute, private apiService: ApiService, private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.accountId = parseInt(this.route.snapshot.paramMap.get('id'))!;
    this.loadAccountDetails();
    this.loadNextPage();
  }

  loadAccountDetails() {
    this.apiService.getAccountById(this.accountId).subscribe(data => {
      this.account = data
      this.changeDetectorRef.markForCheck()
    });
  }

  loadNextPage() {
    if (this.loading || !this.hasMore) return;
    this.loading = true;

    this.apiService.getTransactions(this.accountId, this.page, this.size).subscribe({
      next: (data) => {
        if (data.size < this.size) {
          this.hasMore = false;
        }
        const content = data.content
        this.transactions = [...this.transactions, ...content];
        this.page++;
        this.loading = false;
        this.changeDetectorRef.markForCheck()
        setTimeout(() => this.updateChart(), 100)
      },
      error: () => {
        this.loading = false
        this.changeDetectorRef.markForCheck()
      }
    });
  }

  onTableScroll(event: Event) {
    const element = event.target as HTMLElement;

    const atBottom = element.scrollHeight - element.scrollTop <= element.clientHeight + 5;
    if (atBottom && !this.loading && this.hasMore) {
      this.loadNextPage();
    }
  }

  updateChart() {
    if (!this.account || this.transactions.length === 0 || !this.balanceChartCanvas) {
      return;
    }
    if (this.chart) this.chart.destroy();

    const labels: string[] = [];
    const balances: number[] = [];

    const sortedTransactions = [...this.transactions].sort((a, b) =>
      new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
    );
    let startingBalance = this.account.currentAmount;
    for (const tx of sortedTransactions) {
      if (tx.transactionType === 'CREDIT' || tx.transactionType === 'FX_CREDIT') {
        startingBalance -= tx.amount;
      } else if (tx.transactionType === 'DEBIT' || tx.transactionType === 'FX_DEBIT') {
        startingBalance += tx.amount;
      }
    }


    let runningBalance = startingBalance;
    for (const tx of sortedTransactions) {
      if (tx.transactionType === 'CREDIT' || tx.transactionType === 'FX_CREDIT') {
        runningBalance += tx.amount; // ক্রেডিট হলে ব্যালেন্স বাড়বে
      } else if (tx.transactionType === 'DEBIT' || tx.transactionType === 'FX_DEBIT') {
        runningBalance -= tx.amount; // ডেবিট হলে ব্যালেন্স কমবে
      }

      labels.push(new Date(tx.createdAt).toLocaleDateString());
      balances.push(runningBalance);
    }

    //labels.reverse();
    //balances.reverse();

    this.chart = new Chart(this.balanceChartCanvas.nativeElement, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          label: `Balance Timeline (${this.account.currency})`,
          data: balances,
          borderColor: '#3f51b5',
          backgroundColor: 'rgba(63, 81, 181, 0.1)',
          fill: true,
          tension: 0.1,
          borderWidth: 3,
          pointRadius: 5,
          pointBackgroundColor: '#3f51b5'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            title: { display: true, text: `Balance (${this.account.currency})`, font: { weight: 'bold' } },
            beginAtZero: true // ব্যাংকিং চার্ট ০ থেকে দেখালে গ্রাফ বুঝতে সুবিধা হয়
          },
          x: {
            title: { display: true, text: 'Date of Transaction', font: { weight: 'bold' } }
          }
        }
      }
    });
  }
}

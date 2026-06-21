import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {StateService} from '../../core/services/state.service';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardSubtitle,
  MatCardTitle
} from '@angular/material/card';
import {RouterLink} from '@angular/router';
import {DecimalPipe} from '@angular/common';
import {Account, SupportedCurrency, User} from '../../core/models/models';
import {ApiService} from '../../core/services/api.service';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatOption, MatSelect} from '@angular/material/select';
import {MatIcon} from '@angular/material/icon';
import {FormsModule} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from '@angular/material/expansion';

@Component({
  selector: 'app-home',
  imports: [
    MatCard,
    MatCardHeader,
    RouterLink,
    MatCardSubtitle,
    MatCardTitle,
    MatCardContent,
    MatCardActions,
    DecimalPipe,
    MatFormField,
    MatLabel,
    MatSelect,
    MatOption,
    MatIcon,
    FormsModule,
    MatButton,
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelTitle,
    MatExpansionPanelHeader,
    MatInput
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  supportedCurrencies = Object.values(SupportedCurrency)
  selectedCurrency = SupportedCurrency.EUR
  accounts: Account[] = []
  user!: User
  transactionAmountPerAccount: { [accountId: number]: number } = []
  exchangeTargetPerAccount: { [accountId: number]: number } = {};

  constructor(private stateService: StateService, private apiService: ApiService, private changeDetectorRef: ChangeDetectorRef) {
    this.user = this.stateService.getUser();
  }

  ngOnInit(): void {
    this.stateService.accounts$.subscribe(data => {
      this.accounts = data
      this.changeDetectorRef.markForCheck()
    });
    this.refreshAccounts()
  }

  onLogout() {
    this.stateService.logout()
  }

  refreshAccounts() {
    this.stateService.loadAccounts()
    this.changeDetectorRef.markForCheck()
  }

  onCreateAccount() {
    const user = this.stateService.getUser()
    if (user) {
      this.apiService.createAccount(user.id, this.selectedCurrency).subscribe({
        next: () => {
          alert('New Bank Account Created Successfully!');
          this.refreshAccounts();
        },
        error: () => alert("Failed to create account")
      })
    }
  }

  getAccountCurrencies(currentAccountId: number): Account[] {
    return this.accounts.filter(acc => acc.id !== currentAccountId && acc.active)
  }

  onCredit(accountId: number) {
    const amount = this.transactionAmountPerAccount[accountId];
    if (!amount || amount <= 0) {
      return
    }

    this.apiService.creditAccount(accountId, amount).subscribe({
      next: () => {
        alert(`Money credited to account ${accountId}`)
        this.transactionAmountPerAccount[accountId] = 0
        this.refreshAccounts()
      },
      error: () => alert(`Error crediting to account ${accountId}`)
    })
  }

  onDebit(accountId: number) {
    const amount = this.transactionAmountPerAccount[accountId];
    if (!amount || amount <= 0) {
      return
    }

    this.apiService.debitAccount(accountId, amount).subscribe({
      next: () => {
        alert(`Money debited from account ${accountId}`)
        this.transactionAmountPerAccount[accountId] = 0
        this.refreshAccounts()
      },
      error: () => alert(`Error debiting from account ${accountId}`)
    })
  }

  onExchange(sourceAccountId: number) {
    const sourceAmountToConvert = this.transactionAmountPerAccount[sourceAccountId]
    const targetAccountId = this.exchangeTargetPerAccount[sourceAccountId];
    if (!sourceAmountToConvert || sourceAmountToConvert <= 0 || !targetAccountId) {
      return
    }

    this.apiService.convertMoney(sourceAccountId, targetAccountId, sourceAmountToConvert).subscribe({
      next: () => {
        alert('Currency exchange successful')
        this.transactionAmountPerAccount[sourceAccountId] = 0
        this.exchangeTargetPerAccount[sourceAccountId] = 0
        this.refreshAccounts()
      },
      error: () => alert('Exchange failed')
    })
  }
}

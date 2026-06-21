import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {Account, SpringPage, SupportedCurrency, Transaction, User} from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8080/v1';

  constructor(private http: HttpClient) {}

  createUser(name: string): Observable<User> {
    return this.http.post<User>(`${this.baseUrl}/users`, { name });
  }

  getUser(userId: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/users/${userId}`);
  }

  getAccounts(userId: number): Observable<Account[]> {
    return this.http.get<Account[]>(`${this.baseUrl}/users/${userId}/accounts`);
  }

  getAccountById(id: number): Observable<Account> {
    return this.http.get<Account>(`${this.baseUrl}/accounts/${id}`);
  }

  createAccount(userId: number, currency: SupportedCurrency): Observable<Account> {
    return this.http.post<Account>(`${this.baseUrl}/accounts`, { userId, currency });
  }

  creditAccount(accountId: number, amount: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/accounts/${accountId}/credit`, { amount });
  }

  debitAccount(accountId: number, amount: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/accounts/${accountId}/debit`, { amount });
  }

  convertMoney(sourceAccountId: number, targetAccountId: number, sourceAmountToConvert: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/accounts/exchange`, { sourceAccountId, targetAccountId, sourceAmountToConvert });
  }

  getTransactions(accountId: number, page: number, size: number): Observable<SpringPage<Transaction>> {
    return this.http.get<SpringPage<Transaction>>(`${this.baseUrl}/accounts/${accountId}/transaction-history?page=${page}&size=${size}`);
  }

  getTransactionById(id: number): Observable<Transaction> {
    return this.http.get<Transaction>(`${this.baseUrl}/transactions/${id}`);
  }
}

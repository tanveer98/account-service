import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import {Account, User} from '../models/models';
import { ApiService } from './api.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class StateService {
  private accountsSubject = new BehaviorSubject<Account[]>([]);
  accounts$ = this.accountsSubject.asObservable();

  constructor(private apiService: ApiService, private router: Router) {}

  setUser(user: User) {
    sessionStorage.setItem('bank_user', JSON.stringify(user));
  }

  getUser(): User | null {
    return JSON.parse(sessionStorage.getItem('bank_user'));
  }

  isLoggedIn(): boolean {
    return this.getUser() !== null;
  }

  logout() {
    sessionStorage.removeItem('bank_user_id');
    this.accountsSubject.next([]); // স্টেট ক্লিয়ার
    this.router.navigate(['/login']);
  }

  loadAccounts() {
    const user = this.getUser();
    if (user) {
      this.apiService.getAccounts(user.id).subscribe({
        next: (accounts) => {
          if (Array.isArray(accounts)) {
            this.accountsSubject.next(accounts);
          } else {
            this.accountsSubject.next([accounts]);
          }
        },
        error: (err) => console.error('Error loading accounts', err)
      });
    }
  }
}

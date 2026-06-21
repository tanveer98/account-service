export interface User {
  id: number;
  name: string;
  active: boolean;
}

export interface Account {
  id: number;
  userId: number;
  currentAmount: number;
  currency: SupportedCurrency;
  active: boolean;
}

export interface Transaction {
  id: number;
  transactionType: 'CREDIT' | 'DEBIT' | 'FX_CREDIT' | 'FX_DEBIT';
  accountId: number;
  amount: number;
  currency: SupportedCurrency;
  transactionUuid: string;
  createdAt: string;
}

export enum SupportedCurrency {
  AED = 'AED',
  BDT = 'BDT',
  EUR = 'EUR',
  USD = 'USD',
  SEK = 'SEK',
  GBP = 'GBP',
  VND = 'VND',
  INR = 'INR',
}

export interface SpringPage<T> {
  content: T[];
  empty: boolean;
  first: boolean;
  last: boolean;
  number: number;
  numberOfElements: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

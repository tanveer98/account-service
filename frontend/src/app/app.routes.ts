import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { AccountOverview } from './pages/account-overview/account-overview';
import { TransactionView } from './pages/transaction-view/transaction-view';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { AuthGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: '', component: Home, canActivate: [AuthGuard] },
  { path: 'account/:id', component: AccountOverview, canActivate: [AuthGuard] },
  { path: 'transaction/:id', component: TransactionView, canActivate: [AuthGuard] },

  { path: '**', redirectTo: '' }
];

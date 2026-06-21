import {Component} from '@angular/core';
import {ApiService} from '../../core/services/api.service';
import {StateService} from '../../core/services/state.service';
import {Router, RouterLink} from '@angular/router';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardTitle,
} from '@angular/material/card';
import {MatFormField, MatLabel} from '@angular/material/form-field';
import {FormsModule} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {MatInput} from '@angular/material/input';
@Component({
  selector: 'app-login',
  imports: [
    MatCardActions,
    FormsModule,
    MatLabel,
    MatFormField,
    MatCardContent,
    MatCardTitle,
    MatCardHeader,
    MatCard,
    MatButton,
    RouterLink,
    MatInput
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  userId: number = -1;
  errorMessage: string = '';

  constructor(
    private apiService: ApiService,
    private stateService: StateService,
    private router: Router
  ) {
  }

  onLogin() {
    if (!this.userId) {
      this.errorMessage = 'Please enter a valid User ID.';
      return;
    }

    this.apiService.getUser(this.userId).subscribe({
      next: (user) => {
        this.stateService.setUser(user);
        this.router.navigate(['/']); // হোম পেজে রিডাইরেক্ট
      },
      error: () => {
        this.errorMessage = 'User ID not found. Please register first.';
      }
    });
  }
}

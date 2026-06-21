import {Component} from '@angular/core';
import {ApiService} from '../../core/services/api.service';
import {StateService} from '../../core/services/state.service';
import {Router, RouterLink} from '@angular/router';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardTitle
} from '@angular/material/card';
import {MatFormField, MatLabel} from '@angular/material/form-field';
import {FormsModule} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {MatInput} from '@angular/material/input';

@Component({
  selector: 'app-register',
  imports: [
    MatLabel,
    MatFormField,
    MatCardContent,
    MatCardTitle,
    MatCardHeader,
    MatCard,
    MatCardActions,
    FormsModule,
    MatButton,
    RouterLink,
    MatInput
  ],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
  name: string = '';
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private apiService: ApiService,
    private stateService: StateService,
    private router: Router
  ) {
  }

  onRegister() {
    if (!this.name.trim()) {
      this.errorMessage = 'Name cannot be empty.';
      return;
    }

    this.apiService.createUser(this.name).subscribe({
      next: (user) => {
        this.successMessage = `Registration Successful! Your ID is: ${user.id}`;
        this.stateService.setUser(user);

        setTimeout(() => {
          this.router.navigate(['/']);
        }, 1000);
      },
      error: () => {
        this.errorMessage = 'Registration failed. Try again.';
      }
    });
  }
}

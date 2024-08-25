import { Component, inject, signal, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import SharedModule from 'app/shared/shared.module';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';

@Component({
  standalone: true,
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  imports: [SharedModule, RouterModule, FormsModule],
})
export default class HomeComponent implements OnInit, OnDestroy {
  account = signal<Account | null>(null);
  shortenerFullUrl: string;
  shortenerShortUrl: string;
  expanderFullUrl: string;
  expanderShortUrl: string;
  errorMessage: string;
  fullUrl: string;
  shortUrl: string;

  constructor(private http: HttpClient) {
    this.fullUrl = '';
    this.shortUrl = '';
    this.shortenerFullUrl = '';
    this.shortenerShortUrl = '';
    this.expanderFullUrl = '';
    this.expanderShortUrl = '';
    this.errorMessage = '';
  }

  private readonly destroy$ = new Subject<void>();

  private accountService = inject(AccountService);
  private router = inject(Router);

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => this.account.set(account));
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  generateShortUrl(): void {
    console.log('generateShortUrl');
    console.log(this.fullUrl);
    this.http.post('/api/urls', { fullUrl: this.fullUrl }).subscribe((response: any) => {
      this.shortenerShortUrl = response.shortUrl;
    });
  }

  getFullUrl() {
    console.log('getFullUrl ' + this.expanderShortUrl);
    this.http.get('/api/urls/shorturl?url=' + this.expanderShortUrl).subscribe(
      (response: any) => {
        this.expanderFullUrl = response.fullUrl;
        this.errorMessage = '';
      },
      (error: HttpErrorResponse) => {
        if (error.status === 404) {
          this.errorMessage = this.expanderShortUrl + ' URL not found';
          this.expanderFullUrl = '';
        } else {
          this.errorMessage = 'An error occurred';
        }
      },
    );
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

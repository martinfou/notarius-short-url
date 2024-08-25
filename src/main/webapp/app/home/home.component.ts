import { Component, inject, signal, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';

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
  fullUrl: string;
  shortUrl: string;

  constructor(private http: HttpClient) {
    this.fullUrl = '';
    this.shortUrl = '';
    this.shortenerFullUrl = '';
    this.shortenerShortUrl = '';
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

  getFullUrl(): string {
    console.log('getFullUrl');
    this.http.get('/api/urls/shorturl?url=' + this.shortUrl).subscribe((response: any) => {
      this.fullUrl = response.fullUrl;
    });
    console.log(this.fullUrl);
    return this.fullUrl;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface Prompt {
  id: number;
  title: string;
  description: string;
  author: string;
  tags: string[];
  createdAt: string;
}

export interface PromptVersion {
  id: number;
  promptText: string;
  versionNumber: number;
  createdAt?: string;
}

export interface TestResult {
  id: number;
  inputs: any;
  outputs: string;
  executedAt: string;
}

export interface FeedbackStats {
  averageQuality: number;
  averageAccuracy: number;
  averageUsefulness: number;
  totalReviews: number;
}

@Injectable({
  providedIn: 'root'
})
export class PromptService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getPrompts(filters?: { query?: string, title?: string, author?: string, tag?: string }): Observable<Prompt[]> {
    let params = new HttpParams();
    if (filters) {
      if (filters.query) params = params.set('query', filters.query);
      if (filters.title) params = params.set('title', filters.title);
      if (filters.author) params = params.set('author', filters.author);
      if (filters.tag) params = params.set('tag', filters.tag);
    }
    return this.http.get<any>(`${this.apiUrl}/prompts`, { params }).pipe(
      map(response => response.content ? response.content : response)
    );
  }

  getPromptVersions(promptId: number): Observable<PromptVersion[]> {
    return this.http.get<PromptVersion[]>(`${this.apiUrl}/prompts/${promptId}/versions`);
  }

  createPrompt(payload: any): Observable<Prompt> {
    return this.http.post<Prompt>(`${this.apiUrl}/prompts`, payload);
  }

  createNewVersion(promptId: number, text: string): Observable<PromptVersion> {
    return this.http.put<PromptVersion>(`${this.apiUrl}/prompts/${promptId}/versions`, { promptText: text });
  }

  executePrompt(versionId: number, variables: any): Observable<TestResult> {
    return this.http.post<TestResult>(`${this.apiUrl}/playgrounds/execute`, {
      promptVersionId: versionId,
      variables: variables
    });
  }

  submitFeedback(versionId: number, feedback: { quality: number, accuracy: number, usefulness: number, comments: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/prompt-versions/${versionId}/feedback`, feedback);
  }

  getTestHistory(versionId: number): Observable<TestResult[]> {
    return this.http.get<TestResult[]>(`${this.apiUrl}/prompts/versions/${versionId}/tests`);
  }

  getFeedbackStats(versionId: number): Observable<FeedbackStats> {
    return this.http.get<FeedbackStats>(`${this.apiUrl}/prompts/versions/${versionId}/feedback/stats`);
  }

  compareVersions(promptId: number, versionA: number, versionB: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/prompts/${promptId}/compare`, {
      params: new HttpParams().set('versionA', versionA).set('versionB', versionB)
    });
  }
}

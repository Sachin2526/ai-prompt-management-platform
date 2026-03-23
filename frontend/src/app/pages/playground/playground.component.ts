import { Component, OnInit, signal, inject, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { PromptService, PromptVersion, TestResult, FeedbackStats } from '../../services/prompt.service';
import { ToastService } from '../../services/toast.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-playground',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './playground.component.html',
  styleUrls: ['./playground.component.css']
})
export class PlaygroundComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private promptService = inject(PromptService);
  private toastService = inject(ToastService);

  promptId = signal<number>(0);
  versionId = signal<number>(0);
  version = signal<PromptVersion | null>(null);

  variables = signal<{ key: string, value: string }[]>([{ key: '', value: '' }]);

  isLoading = signal<boolean>(false);
  response = signal<string>('');
  executionTime = signal<number>(0);

  // Feedback Metrics
  quality = signal<number>(5);
  accuracy = signal<number>(5);
  usefulness = signal<number>(5);
  feedbackComments = signal<string>('');

  isSubmittingFeedback = signal<boolean>(false);
  feedbackSuccess = signal<boolean>(false);

  // History and Stats
  history = signal<TestResult[]>([]);
  stats = signal<FeedbackStats | null>(null);
  activeTab = signal<'playground' | 'history' | 'stats'>('playground');

  constructor() {
    effect(() => {
      if (this.versionId()) {
        this.loadHistory();
        this.loadStats();
      }
    });
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.promptId.set(+params['promptId']);
      this.versionId.set(+params['versionId']);
      this.loadVersion();
    });
  }

  loadVersion() {
    this.promptService.getPromptVersions(this.promptId()).subscribe({
      next: (versions) => {
        const found = versions.find(v => v.id === this.versionId()) || null;
        this.version.set(found);
      },
      error: (err) => {
        console.error(err);
        this.toastService.error('Failed to load version details.');
      }
    });
  }

  loadHistory() {
    this.promptService.getTestHistory(this.versionId()).subscribe({
      next: (data) => this.history.set(data),
      error: (err) => console.error("Failed to load history", err)
    });
  }

  loadStats() {
    this.promptService.getFeedbackStats(this.versionId()).subscribe({
      next: (data) => this.stats.set(data),
      error: (err) => console.error("Failed to load stats", err)
    });
  }

  addVariable() {
    this.variables.update(v => [...v, { key: '', value: '' }]);
  }

  removeVariable(index: number) {
    this.variables.update(v => {
      const copy = [...v];
      copy.splice(index, 1);
      return copy;
    });
  }

  updateVariableKey(index: number, val: string) {
    this.variables.update(v => {
      const copy = [...v];
      copy[index].key = val;
      return copy;
    });
  }

  updateVariableValue(index: number, val: string) {
    this.variables.update(v => {
      const copy = [...v];
      copy[index].value = val;
      return copy;
    });
  }

  execute() {
    this.isLoading.set(true);
    this.response.set('');
    this.feedbackSuccess.set(false);

    const start = Date.now();
    const varMap: any = {};

    this.variables().forEach(v => {
      if (v.key) varMap[v.key] = v.value;
    });

    this.promptService.executePrompt(this.versionId(), varMap).subscribe({
      next: (result) => {
        this.response.set(result.outputs);
        this.executionTime.set(Date.now() - start);
        this.isLoading.set(false);
        this.toastService.success('Execution completed successfully');
        this.loadHistory();
      },
      error: (err) => {
        console.error(err);
        this.response.set("Error communicating with AI Playground API: \n" + (err.error?.message || err.message));
        this.isLoading.set(false);
        this.toastService.error('Execution failed');
      }
    });
  }

  submitFeedback() {
    this.isSubmittingFeedback.set(true);
    const feedback = {
      quality: this.quality(),
      accuracy: this.accuracy(),
      usefulness: this.usefulness(),
      comments: this.feedbackComments()
    };

    this.promptService.submitFeedback(this.versionId(), feedback).subscribe({
      next: () => {
        this.isSubmittingFeedback.set(false);
        this.feedbackSuccess.set(true);
        this.toastService.success('Feedback securely submitted');
        this.loadStats();
        setTimeout(() => this.feedbackSuccess.set(false), 3000);
      },
      error: (err) => {
        console.error("Failed to submit feedback", err);
        this.isSubmittingFeedback.set(false);
        this.toastService.error('Failed to submit feedback');
      }
    });
  }

  setTab(tab: 'playground' | 'history' | 'stats') {
    this.activeTab.set(tab);
  }
}

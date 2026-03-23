import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { PromptService, PromptVersion, Prompt } from '../../services/prompt.service';
import { ToastService } from '../../services/toast.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-version-history',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  providers: [DatePipe],
  templateUrl: './version-history.component.html',
  styleUrls: ['./version-history.component.css']
})
export class VersionHistoryComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private promptService = inject(PromptService);
  private toastService = inject(ToastService);

  promptId = signal<number>(0);
  prompt = signal<Prompt | null>(null);
  versions = signal<PromptVersion[]>([]);
  selectedVersion = signal<PromptVersion | null>(null);
  
  showNewVersionModal = signal<boolean>(false);
  newVersionText = signal<string>('');
  isSubmitting = signal<boolean>(false);

  // Comparison State
  comparisonMode = signal<boolean>(false);
  compareA = signal<PromptVersion | null>(null);
  compareB = signal<PromptVersion | null>(null);
  comparisonResult = signal<any>(null);

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.promptId.set(+params['id']);
      this.loadData();
    });
  }

  loadData() {
    this.promptService.getPrompts().subscribe({
      next: (prompts) => {
        const found = prompts.find(p => p.id === this.promptId());
        this.prompt.set(found || null);
      }
    });

    this.promptService.getPromptVersions(this.promptId()).subscribe({
      next: (v) => {
        const sorted = v.sort((a,b) => b.versionNumber - a.versionNumber);
        this.versions.set(sorted);
        if(sorted.length > 0 && !this.selectedVersion()) {
          this.selectedVersion.set(sorted[0]);
        }
      },
      error: (err) => {
        console.error(err);
        this.toastService.error('Failed to load version history.');
      }
    });
  }

  selectVersion(version: PromptVersion) {
    if (this.comparisonMode()) {
      if (!this.compareA()) {
        this.compareA.set(version);
      } else if (!this.compareB() && this.compareA()?.id !== version.id) {
        this.compareB.set(version);
        this.runComparison();
      } else {
        this.compareA.set(version);
        this.compareB.set(null);
        this.comparisonResult.set(null);
      }
    } else {
      this.selectedVersion.set(version);
    }
  }

  toggleComparisonMode() {
    this.comparisonMode.update(v => !v);
    this.compareA.set(null);
    this.compareB.set(null);
    this.comparisonResult.set(null);
    if (!this.comparisonMode() && this.versions().length > 0) {
      this.selectedVersion.set(this.versions()[0]);
    }
  }

  runComparison() {
    if (!this.compareA() || !this.compareB()) return;
    this.promptService.compareVersions(this.promptId(), this.compareA()!.versionNumber, this.compareB()!.versionNumber).subscribe({
      next: (res) => this.comparisonResult.set(res),
      error: (err) => {
        console.error(err);
        this.toastService.error('Comparison failed');
      }
    });
  }

  openVersionModal() {
    this.showNewVersionModal.set(true);
  }

  closeVersionModal() {
    this.showNewVersionModal.set(false);
    this.newVersionText.set('');
  }

  copyFromCurrent() {
    const list = this.versions();
    if (list.length > 0) {
      this.newVersionText.set(list[0].promptText);
    }
  }

  updateNewVersionText(val: string) {
    this.newVersionText.set(val);
  }

  submitNewVersion() {
    const text = this.newVersionText().trim();
    if (!text) return;
    
    this.isSubmitting.set(true);
    this.promptService.createNewVersion(this.promptId(), text).subscribe({
      next: (version) => {
        this.isSubmitting.set(false);
        this.closeVersionModal();
        this.versions.update(v => [version, ...v]);
        this.selectVersion(version);
        this.toastService.success(`Version ${version.versionNumber} published successfully.`);
      },
      error: (err) => {
        console.error(err);
        this.isSubmitting.set(false);
        this.toastService.error('Failed to publish new release.');
      }
    });
  }
}

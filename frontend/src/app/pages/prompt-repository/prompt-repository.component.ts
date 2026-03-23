import { Component, OnInit, signal, computed, inject, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { PromptService, Prompt } from '../../services/prompt.service';
import { ToastService } from '../../services/toast.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-prompt-repository',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './prompt-repository.component.html',
  styleUrls: ['./prompt-repository.component.css']
})
export class PromptRepositoryComponent implements OnInit {
  private promptService = inject(PromptService);
  private router = inject(Router);
  private toastService = inject(ToastService);

  // Signals
  prompts = signal<Prompt[]>([]);
  isLoading = signal<boolean>(true);
  showCreateModal = signal<boolean>(false);
  isSubmitting = signal<boolean>(false);

  searchQuery = signal<string>('');
  authorFilter = signal<string>('');
  tagFilter = signal<string>('');

  // Computed - now just an alias for backend-controlled list
  filteredPrompts = computed(() => this.prompts());

  constructor() {
    // React to filter changes
    effect(() => {
      this.loadPrompts();
    });
  }

  ngOnInit() {
    // Initial load happens via effect
  }

  loadPrompts() {
    this.isLoading.set(true);
    const filters = {
      title: this.searchQuery(),
      author: this.authorFilter(),
      tag: this.tagFilter()
    };

    this.promptService.getPrompts(filters).subscribe({
      next: (data) => {
        this.prompts.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.toastService.error('Failed to connect to the Prompt Service.');
        this.isLoading.set(false);
      }
    });
  }

  updateSearchQuery(val: string) { this.searchQuery.set(val); }
  updateAuthorFilter(val: string) { this.authorFilter.set(val); }
  updateTagFilter(val: string) { this.tagFilter.set(val); }

  runTest(promptId: number) {
    this.promptService.getPromptVersions(promptId).subscribe(versions => {
      if (versions.length > 0) {
        const latestId = versions[0].id;
        this.router.navigate(['/prompts', promptId, 'versions', latestId, 'test']);
      } else {
        this.router.navigate(['/prompts', promptId, 'versions']);
      }
    });
  }

  openCreateModal() {
    this.showCreateModal.set(true);
  }

  closeCreateModal() {
    this.showCreateModal.set(false);
    this.newPromptData = { title: '', description: '', author: '', tags: '', initialPromptText: '' };
  }

  isFormValid(): boolean {
    return !!(this.newPromptData.title.trim() && this.newPromptData.initialPromptText.trim() && this.newPromptData.author.trim());
  }

  submitCreate() {
    if (!this.isFormValid()) return;

    this.isSubmitting.set(true);
    const payload = {
      title: this.newPromptData.title,
      description: this.newPromptData.description,
      author: this.newPromptData.author,
      tags: this.newPromptData.tags.split(',').map((t: string) => t.trim()).filter((t: string) => t),
      initialPromptText: this.newPromptData.initialPromptText
    };

    this.promptService.createPrompt(payload).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.closeCreateModal();
        this.toastService.success('Prompt created successfully');
        this.loadPrompts();
      },
      error: (err) => {
        console.error(err);
        this.isSubmitting.set(false);
        this.toastService.error('Failed to create prompt.');
      }
    });
  }

  // Modal Form State
  newPromptData = {
    title: '',
    description: '',
    author: '',
    tags: '',
    initialPromptText: ''
  };
}

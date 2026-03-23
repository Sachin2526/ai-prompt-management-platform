import { Routes } from '@angular/router';
import { PromptRepositoryComponent } from './pages/prompt-repository/prompt-repository.component';
import { VersionHistoryComponent } from './pages/version-history/version-history.component';
import { PlaygroundComponent } from './pages/playground/playground.component';

export const routes: Routes = [
  { path: '', redirectTo: 'prompts', pathMatch: 'full' },
  { path: 'prompts', component: PromptRepositoryComponent },
  { path: 'prompts/:id/versions', component: VersionHistoryComponent },
  { path: 'prompts/:promptId/versions/:versionId/test', component: PlaygroundComponent },
];

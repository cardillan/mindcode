<script lang="ts">
	import {
		closeSearchPanel,
		findNext,
		findPrevious,
		getSearchQuery,
		replaceAll,
		replaceNext,
		SearchQuery,
		setSearchQuery
	} from '@codemirror/search';
	import { runScopeHandlers, ViewUpdate } from '@codemirror/view';
	import type { EditorView } from 'codemirror';
	import { Input } from './ui/input';
	import { Button } from './ui/button';
	import { untrack } from 'svelte';
	import {
		X,
		ChevronDown,
		ChevronUp,
		Replace,
		CaseSensitive,
		Regex,
		WholeWord
	} from '@lucide/svelte';
	import { Toggle } from './ui/toggle';

	let { view }: { view: EditorView } = $props();
	let currentQuery = getSearchQuery(untrack(() => view.state));
	let readOnly = $state(untrack(() => view.state.readOnly));

	let searchField = $state<HTMLInputElement | null>(null);
	let replaceField = $state<HTMLInputElement | null>(null);
	let replaceText = $state(currentQuery.replace);
	let matchCase = $state(currentQuery.caseSensitive);
	let regexp = $state(currentQuery.regexp);
	let byWord = $state(currentQuery.wholeWord);

	$effect(() => {
		selectInput();
		if (searchField) {
			searchField.value = currentQuery.search;
		}
		if (replaceField) {
			replaceField.value = currentQuery.replace;
		}
	});

	export function selectInput() {
		searchField?.select();
	}

	export function update(update: ViewUpdate) {
		for (let tr of update.transactions) {
			for (let effect of tr.effects) {
				if (effect.is(setSearchQuery)) {
					setQuery(effect.value);
				}
			}
		}
		readOnly = update.state.readOnly;
	}

	function phrase(str: string) {
		return view.state.phrase(str);
	}

	function keydown(e: KeyboardEvent) {
		if (runScopeHandlers(view, e, 'search-panel')) {
			e.preventDefault();
		} else if (e.keyCode == 13 && e.target == searchField) {
			e.preventDefault();
			(e.shiftKey ? findPrevious : findNext)(view);
		} else if (e.keyCode == 13 && e.target == replaceField) {
			e.preventDefault();
			replaceNext(view);
		}
	}

	function commit() {
		let query = new SearchQuery({
			search: searchField!.value,
			caseSensitive: matchCase,
			regexp: regexp,
			wholeWord: byWord,
			replace: replaceText
		});

		if (!query.eq(currentQuery)) {
			currentQuery = query;
			view.dispatch({ effects: setSearchQuery.of(query) });
		}
	}

	function setQuery(query: SearchQuery) {
		currentQuery = query;
		searchField!.value = query.search;
		matchCase = query.caseSensitive;
		regexp = query.regexp;
		byWord = query.wholeWord;
		replaceText = query.replace;
	}

	// we have to use the style attribute to override the styles
	// defined by the imported codemirror themes
	function toggleStyle(selected: boolean) {
		return selected ? 'background-color: var(--primary); color: var(--primary-foreground)' : '';
	}
</script>

<!-- svelte-ignore a11y_interactive_supports_focus -->
<!-- svelte-ignore a11y_no_static_element_interactions -->
<div
	class="cm-search border-b border-border bg-background/95 px-3 py-1.5 text-xs backdrop-blur-sm"
	onkeydown={keydown}
>
	<!-- Search Row -->
	<div class="flex items-center gap-1.5">
		<!-- main-field is used by CodeMirror to find the search input -->
		<Input
			bind:ref={searchField}
			value={untrack(() => currentQuery.search)}
			name="search"
			placeholder={phrase('Find')}
			aria-label={phrase('Find')}
			onchange={commit}
			onkeyup={commit}
			class="h-6 w-40 px-2 py-0 text-xs"
			main-field="true"
		/>

		<Button
			size="icon-sm"
			variant="ghost"
			name="previous"
			onclick={() => findPrevious(view)}
			title={phrase('previous')}
			class="h-6 w-6"
		>
			<ChevronUp class="h-3 w-3" />
		</Button>
		<Button
			size="icon-sm"
			variant="ghost"
			name="next"
			onclick={() => findNext(view)}
			title={phrase('next')}
			class="h-6 w-6"
		>
			<ChevronDown class="h-3 w-3" />
		</Button>

		<div class="mx-1 h-4 w-px bg-border"></div>

		<Toggle
			bind:pressed={matchCase}
			size="sm"
			variant="outline"
			onchange={commit}
			title={phrase('match case')}
			class="h-6 w-6 p-0"
			style={toggleStyle(matchCase)}
		>
			<CaseSensitive class="h-3 w-3" />
		</Toggle>
		<Toggle
			bind:pressed={byWord}
			size="sm"
			variant="outline"
			onchange={commit}
			title={phrase('by word')}
			class="h-6 w-6 p-0"
			style={toggleStyle(byWord)}
		>
			<WholeWord class="h-3 w-3" />
		</Toggle>
		<Toggle
			bind:pressed={regexp}
			size="sm"
			variant="outline"
			onchange={commit}
			title={phrase('regexp')}
			class="h-6 w-6 p-0"
			style={toggleStyle(regexp)}
		>
			<Regex class="h-3 w-3" />
		</Toggle>

		<Button
			size="icon-sm"
			variant="ghost"
			name="close"
			onclick={() => closeSearchPanel(view)}
			title="Close"
			class="ml-auto h-6 w-6"
		>
			<X class="h-3 w-3" />
		</Button>
	</div>

	<!-- Replace Row -->
	{#if !readOnly}
		<div class="mt-1.5 flex items-center gap-1.5">
			<Input
				bind:ref={replaceField}
				bind:value={replaceText}
				name="replace"
				placeholder={phrase('Replace')}
				aria-label={phrase('Replace')}
				onchange={commit}
				onkeyup={commit}
				class="h-6 w-40 px-2 py-0 text-xs"
			/>

			<Button
				size="sm"
				variant="ghost"
				name="replace"
				onclick={() => replaceNext(view)}
				class="h-6 px-2 text-xs"
			>
				<Replace class="mr-1 h-3 w-3" />
				Replace
			</Button>
			<Button
				size="sm"
				variant="ghost"
				name="replaceAll"
				onclick={() => replaceAll(view)}
				class="h-6 px-2 text-xs"
			>
				Replace All
			</Button>
		</div>
	{/if}
</div>

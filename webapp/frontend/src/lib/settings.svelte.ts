import { browser } from '$app/environment';
import { getContext, setContext, tick } from 'svelte';
import { toast } from 'svelte-sonner';

const storageKey = 'mindcode-settings';

export interface Settings {
	mlogWatcherPort: number;
}

const defaultSettings: Settings = {
	mlogWatcherPort: 9992
};

/**
 * Manages application settings with automatic persistence to localStorage.
 * Settings are reactive and automatically saved when changed.
 */
export class SettingsStore {
	mlogWatcherPort = $state(defaultSettings.mlogWatcherPort);

	constructor() {
		if (!browser) return;

		this.#loadSettings();

		// Auto-save settings when they change
		$effect(() => {
			const currentSettings: Settings = {
				mlogWatcherPort: this.mlogWatcherPort
			};

			this.#saveSettings(currentSettings);
		});
	}

	/**
	 * Load settings from localStorage
	 */
	#loadSettings() {
		try {
			const stored = localStorage.getItem(storageKey);
			if (stored) {
				const parsed = JSON.parse(stored) as Partial<Settings>;

				// Apply loaded settings with defaults as fallback
				this.mlogWatcherPort = parsed.mlogWatcherPort ?? defaultSettings.mlogWatcherPort;
			}
		} catch (error) {
			console.error('Failed to load settings from localStorage:', error);
			tick().then(() => toast.error('Failed to load settings. Using defaults.'));
		}
	}

	/**
	 * Save settings to localStorage
	 */
	#saveSettings(settings: Settings) {
		try {
			localStorage.setItem(storageKey, JSON.stringify(settings));
		} catch (error) {
			console.error('Failed to save settings to localStorage:', error);
			tick().then(() => toast.error('Failed to save settings. Changes may not persist.'));
		}
	}

	/**
	 * Reset all settings to defaults
	 */
	reset() {
		this.mlogWatcherPort = defaultSettings.mlogWatcherPort;
	}

	/**
	 * Export current settings as JSON
	 */
	export(): Settings {
		return {
			mlogWatcherPort: this.mlogWatcherPort
		};
	}

	/**
	 * Import settings from JSON
	 */
	import(settings: Partial<Settings>) {
		if (settings.mlogWatcherPort !== undefined) {
			this.mlogWatcherPort = settings.mlogWatcherPort;
		}
	}
}

const settingsContextKey = 'settingsStore';

export function setSettingsContext() {
	setContext(settingsContextKey, new SettingsStore());
}

export function getSettingsContext(): SettingsStore {
	return getContext<SettingsStore>(settingsContextKey);
}

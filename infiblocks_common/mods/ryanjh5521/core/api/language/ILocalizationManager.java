package mods.ryanjh5521.core.api.language;

public interface ILocalizationManager {
	/**
	 * Loads language files from the classpath.
	 * Location is /mods/{modpath}/lang/{language}.lang
	 * Files should be in UTF-8 format.
	 * Should still be called on servers.
	 */
	public void loadLanguageFiles(Object mod, String modpath);
}

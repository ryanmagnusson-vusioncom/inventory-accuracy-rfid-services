package io.vusion.vtransmit.v2.commons.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import com.google.gson.JsonArray;

import io.vusion.gson.utils.GsonHelper;

public class FileUtils {
	
	private static String getResourceAsString(Resource resource) {
		try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
			return FileCopyUtils.copyToString(reader);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public static JsonArray readFileToJsonArray(String path) {
		final ResourceLoader resourceLoader = new DefaultResourceLoader();
		final Resource resource = resourceLoader.getResource(path);
		final String resourceAsString = getResourceAsString(resource);
		return GsonHelper.getGson().fromJson(resourceAsString, JsonArray.class);
	}
	
}

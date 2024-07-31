package io.vusion.vtransmit.v2.commons;

import org.apache.commons.lang3.BooleanUtils;

import io.vusion.gson.utils.GsonHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LoggingProperties {
	
	@Setter @Getter
	@AllArgsConstructor @NoArgsConstructor
	public static class WebProperties {
		private Boolean verbose = false;
		
		public boolean isVerbose() {
			return BooleanUtils.isTrue(this.verbose);
		}
		
		@Override
		public String toString() { return GsonHelper.toJson(this); }
	}
	
	private WebProperties web;
	
	@Override
	public String toString() { return GsonHelper.toJson(this); }
	
}

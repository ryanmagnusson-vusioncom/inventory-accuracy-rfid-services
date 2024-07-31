package io.vusion.vtransmit.v2.commons.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {
	
	private static class ContextReference {
		private static final ContextHolder CONTEXT_HOLDER = new ContextHolder();
		
		private ContextReference() {
		}
	}
	
	private static final class ContextHolder {
		private ApplicationContext context;
		
		private ContextHolder() {
		}
		
		private void setContext(ApplicationContext context) {
			this.context = context;
		}
	}
	
	public static ApplicationContext getApplicationContext() {
		return ContextReference.CONTEXT_HOLDER.context;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext ac) {
		ContextReference.CONTEXT_HOLDER.setContext(ac);
	}

}
